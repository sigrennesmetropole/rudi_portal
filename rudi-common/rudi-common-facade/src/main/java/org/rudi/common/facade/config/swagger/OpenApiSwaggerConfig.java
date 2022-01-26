package org.rudi.common.facade.config.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

public class OpenApiSwaggerConfig {

	@Bean
	public GroupedOpenApi commonApi() {
		return GroupedOpenApi.builder().group("common-api").packagesToScan("org.rudi.common.facade.controller.api")
				.build();
	}

	@Bean
	public OpenAPI springOpenAPI() {
		return new OpenAPI().openapi("3.0.0").info(new Info().title("API Rudi"))
				.components(new Components().addSecuritySchemes("jwt-oauth2",
						new SecurityScheme().type(Type.HTTP).in(In.HEADER).scheme("bearer").bearerFormat("JWT")));
	}

}
