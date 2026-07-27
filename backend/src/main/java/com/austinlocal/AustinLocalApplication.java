package com.austinlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AustinLocalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AustinLocalApplication.class, args);
    }
}
