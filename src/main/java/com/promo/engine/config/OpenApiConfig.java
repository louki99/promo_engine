package com.promo.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI promotionEngineOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Promotion Engine API")
                .description("RESTful API for managing and applying promotional rules and discounts")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Promotion Engine Team")
                    .email("support@promoengine.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server")
            ));
    }
} 