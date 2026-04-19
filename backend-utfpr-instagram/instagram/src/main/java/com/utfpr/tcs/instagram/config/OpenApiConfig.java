package com.utfpr.tcs.instagram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UTFPR Instagram API")
                        .version("0.0.1")
                        .description("Documentação OpenAPI gerada com springdoc-openapi")
                        .contact(new Contact().name("UTFPR TCS").email("contato@example.com")));
    }
}

