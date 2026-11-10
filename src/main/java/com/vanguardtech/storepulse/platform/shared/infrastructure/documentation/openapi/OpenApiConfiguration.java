package com.vanguardtech.storepulse.platform.shared.infrastructure.documentation.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * OpenAPI documentation of the REST API, served by Swagger UI at {@code /swagger-ui.html}.
 *
 * <p>The {@code bearerAuth} scheme documents the JWT issued by Identity and Access Management.
 */

@Configuration
public class OpenApiConfiguration {
    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI storePulseOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("StorePulse REST API")
                        .description("Security, smoke detection and verifiable utility consumption for commercial galleries.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
