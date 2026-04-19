package com.dogs.api.controllers;

import com.dogs.api.dto.request.AddSubBreedRequest;
import com.dogs.api.dto.request.CreateBreedRequest;
import com.dogs.api.dto.response.BreedResponse;
import com.dogs.api.exception.BreedAlreadyExistsException;
import com.dogs.api.exception.BreedNotFoundException;
import com.dogs.api.exception.GlobalExceptionHandler;
import com.dogs.api.service.BreedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BreedController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("BreedController Tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BreedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BreedService breedService;

    private BreedResponse sampleBreedResponse() {
        return BreedResponse.builder()
                .id(1L)
                .name("bulldog")
                .subBreeds(List.of("boston", "french"))
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/breeds returns 200 with all breeds")
    void getAllBreeds_returns200() throws Exception {
        when(breedService.getAllBreeds()).thenReturn(List.of(sampleBreedResponse()));

        mockMvc.perform(get("/api/v1/breeds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].name").value("bulldog"))
                .andExpect(jsonPath("$.data[0].subBreeds[0]").value("boston"));
    }

    @Test
    @DisplayName("GET /api/v1/breeds/{name} returns 200 for existing breed")
    void getBreed_existingBreed_returns200() throws Exception {
        when(breedService.getBreed("bulldog")).thenReturn(sampleBreedResponse());

        mockMvc.perform(get("/api/v1/breeds/bulldog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("bulldog"));
    }

    @Test
    @DisplayName("GET /api/v1/breeds/{name} returns 404 for unknown breed")
    void getBreed_unknownBreed_returns404() throws Exception {
        when(breedService.getBreed("ghost")).thenThrow(new BreedNotFoundException("ghost"));

        mockMvc.perform(get("/api/v1/breeds/ghost"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Breed 'ghost' not found"));
    }

    @Test
    @DisplayName("POST /api/v1/breeds returns 201 for valid request")
    void createBreed_validRequest_returns201() throws Exception {
        CreateBreedRequest request = new CreateBreedRequest("labradoodle", List.of());
        when(breedService.createBreed(any(CreateBreedRequest.class))).thenReturn(
                BreedResponse.builder().id(2L).name("labradoodle").subBreeds(List.of()).build()
        );

        mockMvc.perform(post("/api/v1/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("labradoodle"));
    }

    @Test
    @DisplayName("POST /api/v1/breeds returns 400 for blank name")
    void createBreed_blankName_returns400() throws Exception {
        CreateBreedRequest request = new CreateBreedRequest("", List.of());

        mockMvc.perform(post("/api/v1/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @DisplayName("POST /api/v1/breeds returns 409 for duplicate breed")
    void createBreed_duplicateBreed_returns409() throws Exception {
        CreateBreedRequest request = new CreateBreedRequest("bulldog", List.of());
        when(breedService.createBreed(any())).thenThrow(new BreedAlreadyExistsException("bulldog"));

        mockMvc.perform(post("/api/v1/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName("DELETE /api/v1/breeds/{name} returns 200 for existing breed")
    void deleteBreed_existingBreed_returns200() throws Exception {
        mockMvc.perform(delete("/api/v1/breeds/pug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Breed deleted successfully"));
    }

    @Test
    @DisplayName("DELETE /api/v1/breeds/{name} returns 404 for unknown breed")
    void deleteBreed_unknownBreed_returns404() throws Exception {
        doThrow(new BreedNotFoundException("ghost")).when(breedService).deleteBreed("ghost");

        mockMvc.perform(delete("/api/v1/breeds/ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/breeds/{name}/sub-breeds returns 201 for valid sub-breed")
    void addSubBreed_validRequest_returns201() throws Exception {
        AddSubBreedRequest request = new AddSubBreedRequest("english");
        when(breedService.addSubBreed(eq("bulldog"), any(AddSubBreedRequest.class)))
                .thenReturn(BreedResponse.builder().id(1L).name("bulldog").subBreeds(List.of("boston", "english", "french")).build());

        mockMvc.perform(post("/api/v1/breeds/bulldog/sub-breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.subBreeds[1]").value("english"));
    }

    @Test
    @DisplayName("DELETE /api/v1/breeds/{name}/sub-breeds/{subBreed} returns 200")
    void deleteSubBreed_existingSubBreed_returns200() throws Exception {
        when(breedService.deleteSubBreed("bulldog", "french"))
                .thenReturn(BreedResponse.builder().id(1L).name("bulldog").subBreeds(List.of("boston")).build());

        mockMvc.perform(delete("/api/v1/breeds/bulldog/sub-breeds/french"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subBreeds[0]").value("boston"));
    }
}
