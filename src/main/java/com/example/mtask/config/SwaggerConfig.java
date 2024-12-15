package com.example.mtask.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures and provides an {@link OpenAPI} bean for API documentation.
     * <p>
     * - Defines the API title and version.<br>
     * - Adds a basic authentication security scheme to secure API endpoints.<br>
     * - Associates the security scheme with the API documentation.
     * </p>
     *
     * @return a customized {@link OpenAPI} instance for the application's API documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info().title("Product and Category API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}