package org.rudi.microservice.template.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.service", "org.rudi.common.storage",
		"org.rudi.microservice.template.service", "org.rudi.microservice.template.storage" })
@PropertySource(value = { "classpath:template_test.properties" }, ignoreResourceNotFound = false)
public class SpringBootTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);

	}
}
