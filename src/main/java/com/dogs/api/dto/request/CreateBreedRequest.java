package com.dogs.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBreedRequest {

    @NotBlank(message = "Breed name must not be blank")
    private String name;

    private List<String> subBreeds = new ArrayList<>();
}
