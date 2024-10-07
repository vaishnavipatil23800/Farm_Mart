package com.farmmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is the MAIN entry point - replaces your server/index.js
// @SpringBootApplication automatically sets up:
//   - Component scanning (finds all @Controller, @Service, etc.)
//   - Auto configuration
//   - Spring Boot starter

@SpringBootApplication
public class FarmMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmMartApplication.class, args);
        System.out.println("====================================");
        System.out.println(" FarmMart Server is Running!");
        System.out.println(" Port: 8080");
        System.out.println("====================================");
    }
}
