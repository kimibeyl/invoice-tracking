package com.capitec.invoicetracking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Invoice Tracking",
                version = "2.4.0"))
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customizePublicOpenAPI(SpringDocConfigProperties springDocConfigProperties) {
        final String bearerAuth = "bearerAuth";

        List<String> packagesToScan = new ArrayList<>();
        packagesToScan.add("com.capitec.invoicetracking.controller");
        springDocConfigProperties.setPackagesToScan(packagesToScan);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(bearerAuth))
                .components(new Components()
                        .addSecuritySchemes(bearerAuth, new io.swagger.v3.oas.models.security.SecurityScheme()
                                .name(bearerAuth)
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

