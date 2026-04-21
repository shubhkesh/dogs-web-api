package com.dogs.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubBreedRequest {

    @NotBlank(message = "Sub-breed name must not be blank")
    private String name;
}
