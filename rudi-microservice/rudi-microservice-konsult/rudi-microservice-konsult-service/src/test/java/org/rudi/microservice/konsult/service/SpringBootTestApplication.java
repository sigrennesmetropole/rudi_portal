package org.rudi.microservice.konsult.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.service", "org.rudi.common.storage",
		"org.rudi.microservice.konsult.service", "org.rudi.microservice.konsult.storage", "org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess", "org.rudi.facet.apimaccess", "org.rudi.facet.acl" })
@PropertySource(value = { "classpath:konsult_test.properties" }, ignoreResourceNotFound = false)
public class SpringBootTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);

	}
}
