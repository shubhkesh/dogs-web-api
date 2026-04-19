package com.dogs.api.service.impl;

import com.dogs.api.dto.request.AddSubBreedRequest;
import com.dogs.api.dto.request.CreateBreedRequest;
import com.dogs.api.dto.request.UpdateBreedRequest;
import com.dogs.api.dto.response.BreedResponse;
import com.dogs.api.exception.BreedAlreadyExistsException;
import com.dogs.api.exception.BreedNotFoundException;
import com.dogs.api.exception.SubBreedAlreadyExistsException;
import com.dogs.api.mapper.BreedMapper;
import com.dogs.api.model.Breed;
import com.dogs.api.model.SubBreed;
import com.dogs.api.repository.BreedRepository;
import com.dogs.api.service.BreedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BreedServiceImpl implements BreedService {

    private final BreedRepository breedRepository;
    private final BreedMapper breedMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BreedResponse> getAllBreeds() {
        log.debug("Fetching all breeds");
        return breedMapper.toResponseList(breedRepository.findAllWithSubBreeds());
    }

    @Override
    @Transactional(readOnly = true)
    public BreedResponse getBreed(String breedName) {
        log.debug("Fetching breed: {}", breedName);
        return breedMapper.toResponse(findBreedByName(breedName));
    }

    @Override
    @Transactional
    public BreedResponse createBreed(CreateBreedRequest request) {
        String normalizedName = normalize(request.getName());
        log.info("Creating breed: {}", normalizedName);

        if (breedRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BreedAlreadyExistsException(normalizedName);
        }

        Breed breed = Breed.builder()
                .name(normalizedName)
                .build();

        if (request.getSubBreeds() != null) {
            request.getSubBreeds().stream()
                    .map(this::normalize)
                    .distinct()
                    .map(subName -> SubBreed.builder().name(subName).breed(breed).build())
                    .forEach(breed.getSubBreeds()::add);
        }

        return breedMapper.toResponse(breedRepository.save(breed));
    }

    @Override
    @Transactional
    public BreedResponse updateBreed(String breedName, UpdateBreedRequest request) {
        Breed breed = findBreedByName(breedName);
        String newName = normalize(request.getName());
        log.info("Updating breed '{}' to '{}'", breedName, newName);

        if (!breed.getName().equalsIgnoreCase(newName) && breedRepository.existsByNameIgnoreCase(newName)) {
            throw new BreedAlreadyExistsException(newName);
        }

        breed.setName(newName);
        return breedMapper.toResponse(breedRepository.save(breed));
    }

    @Override
    @Transactional
    public void deleteBreed(String breedName) {
        log.info("Deleting breed: {}", breedName);
        Breed breed = findBreedByName(breedName);
        breedRepository.delete(breed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSubBreeds(String breedName) {
        log.debug("Fetching sub-breeds for: {}", breedName);
        return findBreedByName(breedName).getSubBreeds().stream()
                .map(SubBreed::getName)
                .sorted()
                .toList();
    }

    @Override
    @Transactional
    public BreedResponse addSubBreed(String breedName, AddSubBreedRequest request) {
        Breed breed = findBreedByName(breedName);
        String subBreedName = normalize(request.getName());
        log.info("Adding sub-breed '{}' to breed '{}'", subBreedName, breedName);

        boolean alreadyExists = breed.getSubBreeds().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(subBreedName));
        if (alreadyExists) {
            throw new SubBreedAlreadyExistsException(subBreedName, breedName);
        }

        SubBreed subBreed = SubBreed.builder()
                .name(subBreedName)
                .breed(breed)
                .build();

        breed.getSubBreeds().add(subBreed);
        return breedMapper.toResponse(breedRepository.save(breed));
    }

    @Override
    @Transactional
    public BreedResponse deleteSubBreed(String breedName, String subBreedName) {
        Breed breed = findBreedByName(breedName);
        log.info("Deleting sub-breed '{}' from breed '{}'", subBreedName, breedName);

        SubBreed subBreed = breed.getSubBreeds().stream()
                .filter(s -> s.getName().equalsIgnoreCase(subBreedName))
                .findFirst()
                .orElseThrow(() -> new BreedNotFoundException(
                        "Sub-breed '" + subBreedName + "' not found in breed '" + breedName + "'"));

        breed.getSubBreeds().remove(subBreed);
        return breedMapper.toResponse(breedRepository.save(breed));
    }

    private Breed findBreedByName(String breedName) {
        return breedRepository.findByNameIgnoreCase(breedName)
                .orElseThrow(() -> new BreedNotFoundException(breedName));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }
}
