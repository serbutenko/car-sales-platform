package ru.butenko.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API сервиса заказа")
                        .version("v1")
                        .description("Создание заказов и сборка конфигураций"))
                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                .components(new Components()
                        .addSecuritySchemes(
                                "keycloak",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        .tokenUrl("http://localhost:8081/realms/car-app/protocol/openid-connect/token")
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID scope")
                                                        )
                                                )
                                        )
                        )
                );
    }
}