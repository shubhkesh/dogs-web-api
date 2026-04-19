package com.dogs.api.constants;

public class ApiConstants {

    public static final String API_V1 = "/api/v1";
    public static final String BREEDS_PATH = API_V1 + "/breeds";
    public static final String BREED_BY_NAME_PATH = BREEDS_PATH + "/{breedName}";
    public static final String SUB_BREEDS_PATH = BREED_BY_NAME_PATH + "/sub-breeds";
    public static final String SUB_BREED_BY_NAME_PATH = SUB_BREEDS_PATH + "/{subBreedName}";

    private ApiConstants() {
        throw new IllegalStateException("Utility class");
    }
}
