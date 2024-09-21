package com.cg.stock_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder()
                .group("Stock API") // Name of the API group
                .pathsToMatch("/stocks/**") // Endpoints to document
                .build();
    }
}
