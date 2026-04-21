package com.dogs.api.controllers;

import com.dogs.api.constants.ApiConstants;
import com.dogs.api.dto.request.AddSubBreedRequest;
import com.dogs.api.dto.request.CreateBreedRequest;
import com.dogs.api.dto.request.UpdateBreedRequest;
import com.dogs.api.dto.request.UpdateSubBreedRequest;
import com.dogs.api.dto.response.ApiResponse;
import com.dogs.api.dto.response.BreedResponse;
import com.dogs.api.service.BreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BREEDS_PATH)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Breeds", description = "CRUD operations for dog breeds and their sub-breeds")
public class BreedController {

    private final BreedService breedService;

    @GetMapping
    @Operation(summary = "Get all breeds", description = "Returns all dog breeds with their sub-breeds, sorted alphabetically")
    public ResponseEntity<ApiResponse<List<BreedResponse>>> getAllBreeds() {
        log.info("GET /breeds");
        return ResponseEntity.ok(ApiResponse.success("Breeds retrieved successfully", breedService.getAllBreeds()));
    }

    @GetMapping("/{breedName}")
    @Operation(summary = "Get a breed by name", description = "Returns a single breed and its sub-breeds")
    public ResponseEntity<ApiResponse<BreedResponse>> getBreed(
            @Parameter(description = "Name of the breed", example = "bulldog") @PathVariable String breedName) {
        log.info("GET /breeds/{}", breedName);
        return ResponseEntity.ok(ApiResponse.success("Breed retrieved successfully", breedService.getBreed(breedName)));
    }

    @PostMapping
    @Operation(summary = "Create a new breed", description = "Creates a new dog breed, optionally with sub-breeds")
    public ResponseEntity<ApiResponse<BreedResponse>> createBreed(
            @Valid @RequestBody CreateBreedRequest request) {
        log.info("POST /breeds - name={}", request.getName());
        BreedResponse created = breedService.createBreed(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Breed created successfully", created));
    }

    @PutMapping("/{breedName}")
    @Operation(summary = "Update a breed", description = "Renames an existing breed")
    public ResponseEntity<ApiResponse<BreedResponse>> updateBreed(
            @Parameter(description = "Current name of the breed", example = "bulldog") @PathVariable String breedName,
            @Valid @RequestBody UpdateBreedRequest request) {
        log.info("PUT /breeds/{}", breedName);
        return ResponseEntity.ok(ApiResponse.success("Breed updated successfully", breedService.updateBreed(breedName, request)));
    }

    @DeleteMapping("/{breedName}")
    @Operation(summary = "Delete a breed", description = "Permanently deletes a breed and all its sub-breeds")
    public ResponseEntity<ApiResponse<Void>> deleteBreed(
            @Parameter(description = "Name of the breed to delete", example = "pug") @PathVariable String breedName) {
        log.info("DELETE /breeds/{}", breedName);
        breedService.deleteBreed(breedName);
        return ResponseEntity.ok(ApiResponse.success("Breed deleted successfully", null));
    }

    @GetMapping("/{breedName}/sub-breeds")
    @Operation(summary = "Get sub-breeds", description = "Returns all sub-breeds for a given breed")
    public ResponseEntity<ApiResponse<List<String>>> getSubBreeds(
            @Parameter(description = "Name of the breed", example = "terrier") @PathVariable String breedName) {
        return ResponseEntity.ok(ApiResponse.success("Sub-breeds retrieved successfully", breedService.getSubBreeds(breedName)));
    }

    @PostMapping("/{breedName}/sub-breeds")
    @Operation(summary = "Add a sub-breed", description = "Adds a new sub-breed to an existing breed")
    public ResponseEntity<ApiResponse<BreedResponse>> addSubBreed(
            @Parameter(description = "Name of the breed", example = "bulldog") @PathVariable String breedName,
            @Valid @RequestBody AddSubBreedRequest request) {
        BreedResponse updated = breedService.addSubBreed(breedName, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sub-breed added successfully", updated));
    }

    @PutMapping("/{breedName}/sub-breeds/{subBreedName}")
    @Operation(summary = "Update a sub-breed", description = "Renames an existing sub-breed within a breed")
    public ResponseEntity<ApiResponse<BreedResponse>> updateSubBreed(
            @Parameter(description = "Name of the breed", example = "bulldog") @PathVariable String breedName,
            @Parameter(description = "Current name of the sub-breed", example = "french") @PathVariable String subBreedName,
            @Valid @RequestBody UpdateSubBreedRequest request) {
        log.info("PUT /breeds/{}/sub-breeds/{}", breedName, subBreedName);
        return ResponseEntity.ok(ApiResponse.success("Sub-breed updated successfully", breedService.updateSubBreed(breedName, subBreedName, request)));
    }

    @DeleteMapping("/{breedName}/sub-breeds/{subBreedName}")
    @Operation(summary = "Delete a sub-breed", description = "Removes a specific sub-breed from a breed")
    public ResponseEntity<ApiResponse<BreedResponse>> deleteSubBreed(
            @Parameter(description = "Name of the breed", example = "bulldog") @PathVariable String breedName,
            @Parameter(description = "Name of the sub-breed to delete", example = "french") @PathVariable String subBreedName) {
        return ResponseEntity.ok(ApiResponse.success("Sub-breed deleted successfully", breedService.deleteSubBreed(breedName, subBreedName)));
    }
}
