package com.recipecommunity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecipeCommunityApplication {
    private static final Logger logger = LoggerFactory.getLogger(RecipeCommunityApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RecipeCommunityApplication.class, args);
    }

}
