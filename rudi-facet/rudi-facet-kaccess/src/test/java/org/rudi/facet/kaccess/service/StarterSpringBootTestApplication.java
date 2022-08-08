package org.rudi.facet.kaccess.service;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(scanBasePackages = {
		"org.rudi.facet.dataverse.api",
		"org.rudi.facet.dataverse.fields",
		"org.rudi.facet.dataverse.helper",
		"org.rudi.facet.kaccess.service",
		"org.rudi.facet.kaccess.helper"
})
public class StarterSpringBootTestApplication {
	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}
}
