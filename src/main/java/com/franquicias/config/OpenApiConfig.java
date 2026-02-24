package com.franquicias.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franquiciasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franquicias API")
                        .description("API for managing franquicias, sucursales and productos.")
                        .version("v1"));
    }
}
