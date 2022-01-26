package org.rudi.tools.nodestub.config.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiSwaggerConfig {

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("nodestub").packagesToScan("org.rudi.tools.nodestub.controller").build();
	}
	
	@Bean
	public OpenAPI springOpenAPI() {
		return new OpenAPI().openapi("3.0.0").info(new Info().title("API Rudi Node Stub")).components(new Components());
	}

}
