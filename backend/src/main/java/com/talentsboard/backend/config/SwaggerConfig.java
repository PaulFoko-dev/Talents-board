package com.talentsboard.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI talentsBoardOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Talents-Board API")
                .description("API REST pour la plateforme de mise en relation candidats ↔ entreprises")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Talents-Board Team")
                    .email("contact@talentsboard.com")));
    }
}
