package com.kolmir.feed_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@EnableCaching
@EnableMethodSecurity
@SpringBootApplication
public class FeedServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeedServiceApplication.class, args);
	}
}
