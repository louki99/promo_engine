package com.promo.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PromoEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromoEngineApplication.class, args);
    }
} 