package org.rudi.microservice.acl.facade.config.swagger;

import org.rudi.common.facade.config.swagger.OpenApiSwaggerConfig;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroServiceOpenApiSwaggerConfig extends OpenApiSwaggerConfig {

	@Bean
	public GroupedOpenApi publicMgn() {
		return GroupedOpenApi.builder().group("acl").packagesToScan("org.rudi.microservice.acl.facade.controller")
				.build();
	}

}
