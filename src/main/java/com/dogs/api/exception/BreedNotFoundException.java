package com.dogs.api.exception;

public class BreedNotFoundException extends RuntimeException {

    public BreedNotFoundException(String breedName) {
        super("Breed '" + breedName + "' not found");
    }
}
