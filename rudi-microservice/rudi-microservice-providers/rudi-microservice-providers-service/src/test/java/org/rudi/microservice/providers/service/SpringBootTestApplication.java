package org.rudi.microservice.providers.service;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.service", "org.rudi.common.storage",
		"org.rudi.facet.acl", "org.rudi.facet.dataverse", "org.rudi.facet.kmedia", "org.rudi.microservice.providers.service",
		"org.rudi.microservice.providers.storage" })
@PropertySource(value = { "classpath:providers_test.properties" }, ignoreResourceNotFound = false)
public class SpringBootTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);

	}

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}

}
