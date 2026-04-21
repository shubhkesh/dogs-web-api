package com.dogs.api.service;

import com.dogs.api.dto.request.AddSubBreedRequest;
import com.dogs.api.dto.request.CreateBreedRequest;
import com.dogs.api.dto.request.UpdateBreedRequest;
import com.dogs.api.dto.request.UpdateSubBreedRequest;
import com.dogs.api.dto.response.BreedResponse;

import java.util.List;

public interface BreedService {

    List<BreedResponse> getAllBreeds();

    BreedResponse getBreed(String breedName);

    BreedResponse createBreed(CreateBreedRequest request);

    BreedResponse updateBreed(String breedName, UpdateBreedRequest request);

    void deleteBreed(String breedName);

    List<String> getSubBreeds(String breedName);

    BreedResponse addSubBreed(String breedName, AddSubBreedRequest request);

    BreedResponse updateSubBreed(String breedName, String subBreedName, UpdateSubBreedRequest request);

    BreedResponse deleteSubBreed(String breedName, String subBreedName);
}
