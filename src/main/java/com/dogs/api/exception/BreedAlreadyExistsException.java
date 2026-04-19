package com.dogs.api.exception;

public class BreedAlreadyExistsException extends RuntimeException {

    public BreedAlreadyExistsException(String breedName) {
        super("Breed '" + breedName + "' already exists");
    }
}
