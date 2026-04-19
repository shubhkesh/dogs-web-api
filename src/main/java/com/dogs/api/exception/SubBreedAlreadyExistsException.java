package com.dogs.api.exception;

public class SubBreedAlreadyExistsException extends RuntimeException {

    public SubBreedAlreadyExistsException(String subBreedName, String breedName) {
        super("Sub-breed '" + subBreedName + "' already exists in breed '" + breedName + "'");
    }
}
