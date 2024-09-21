package com.cg.stockmarket.admin_exchange_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi exchangeApi() {
        return GroupedOpenApi.builder()
                .group("Exchange API")
                .pathsToMatch("/exchanges/**")
                .build();
    }
}
