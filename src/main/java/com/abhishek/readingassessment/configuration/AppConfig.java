package com.abhishek.readingassessment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

    @Bean
    public RestClient.Builder restClientBuilder(){
        return RestClient.builder();
    }
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}

// an instance of the both the classes are created and managed so that they can be easily used in the
// service class, basically just creating a variable and generating constructor for it
// So whenever there is a call - there is a object which is instantiated and is used for further operation
// this is called as Dependency Injection

// if the AppConfig is not created we have to manually create objects everytime when we want to use it
