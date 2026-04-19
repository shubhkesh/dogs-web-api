package com.dogs.api.configuration;

import com.dogs.api.model.Breed;
import com.dogs.api.model.SubBreed;
import com.dogs.api.repository.BreedRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final BreedRepository breedRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (breedRepository.count() > 0) {
            log.info("Database already contains data, skipping seed");
            return;
        }

        log.info("Seeding database from dogs.json...");

        InputStream inputStream = getClass().getResourceAsStream("/dogs.json");
        if (inputStream == null) {
            log.warn("dogs.json not found in classpath, skipping seed");
            return;
        }

        TypeReference<Map<String, List<String>>> typeRef = new TypeReference<>() {};
        Map<String, List<String>> dogsData = objectMapper.readValue(inputStream, typeRef);

        dogsData.forEach((breedName, subBreedNames) -> {
            Breed breed = Breed.builder()
                    .name(breedName.toLowerCase().trim())
                    .build();

            subBreedNames.stream()
                    .map(s -> s.toLowerCase().trim())
                    .map(subName -> SubBreed.builder().name(subName).breed(breed).build())
                    .forEach(breed.getSubBreeds()::add);

            breedRepository.save(breed);
        });

        log.info("Database seeded successfully with {} breeds", dogsData.size());
    }
}
