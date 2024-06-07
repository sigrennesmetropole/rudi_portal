package org.rudi.microservice.apigateway.facade.config.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class MicroServiceOpenApiSwaggerConfig {

	@Bean
	public OpenAPI springOpenAPI() {
		return new OpenAPI().openapi("3.0.0").info(new Info().title("API Rudi"))
				.components(new Components().addSecuritySchemes("jwt-oauth2",
						new SecurityScheme().type(Type.HTTP).in(In.HEADER).scheme("bearer").bearerFormat("JWT")));
	}

	@Bean
	public GroupedOpenApi publicMgn() {
		return GroupedOpenApi.builder().group("apigateway")
				.packagesToScan("org.rudi.microservice.apigateway.facade.controller").build();
	}

}
