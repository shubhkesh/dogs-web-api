package com.dogs.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreedResponse {

    private Long id;
    private String name;
    private List<String> subBreeds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
