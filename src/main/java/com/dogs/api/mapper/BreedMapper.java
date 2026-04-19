package com.dogs.api.mapper;

import com.dogs.api.dto.response.BreedResponse;
import com.dogs.api.model.Breed;
import com.dogs.api.model.SubBreed;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BreedMapper {

    public BreedResponse toResponse(Breed breed) {
        return BreedResponse.builder()
                .id(breed.getId())
                .name(breed.getName())
                .subBreeds(breed.getSubBreeds().stream()
                        .map(SubBreed::getName)
                        .sorted()
                        .toList())
                .createdAt(breed.getCreatedAt())
                .updatedAt(breed.getUpdatedAt())
                .build();
    }

    public List<BreedResponse> toResponseList(List<Breed> breeds) {
        return breeds.stream()
                .map(this::toResponse)
                .toList();
    }
}
