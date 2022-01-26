package org.rudi.microservice.providers.facade.config.swagger;

import org.rudi.common.facade.config.swagger.OpenApiSwaggerConfig;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger/open API pour le module
 * 
 * @author FNI18300
 *
 */
@Configuration
public class MicroServiceOpenApiSwaggerConfig extends OpenApiSwaggerConfig {

	@Bean
	public GroupedOpenApi publicProviders() {
		return GroupedOpenApi.builder().group("providers")
				.packagesToScan("org.rudi.microservice.providers.facade.controller").build();
	}

}
