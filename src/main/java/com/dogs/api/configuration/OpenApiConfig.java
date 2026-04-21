package com.dogs.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public OpenAPI dogsWebApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dogs Web API")
                        .description("""
                                A RESTful API for managing dog breeds and sub-breeds.
                                
                                ## Features
                                - Full CRUD for breeds and sub-breeds
                                - All operations are persisted to PostgreSQL
                                - Pre-seeded with 70+ breeds from the dogs dataset
                                
                                ## Getting Started
                                Use the endpoints below to explore and manage the dog breed list.
                                All breed names are case-insensitive and stored in lowercase.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dogs Web API")
                                .url("https://github.com/shubhkesh/dogs-web-api"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
