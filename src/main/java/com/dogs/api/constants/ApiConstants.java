package com.dogs.api.constants;

public class ApiConstants {

    public static final String API_V1 = "/api/v1";
    public static final String BREEDS_PATH = API_V1 + "/breeds";

    private ApiConstants() {
        throw new IllegalStateException("Utility class");
    }
}
