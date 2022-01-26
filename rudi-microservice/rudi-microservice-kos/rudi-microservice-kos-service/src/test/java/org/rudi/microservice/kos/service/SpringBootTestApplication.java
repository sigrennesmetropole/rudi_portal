package org.rudi.microservice.kos.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.microservice.kos.service",
		"org.rudi.microservice.kos.storage"
})
@PropertySource(value = { "classpath:kos_test.properties" }) // ignoreResourceNotFound = false
public class SpringBootTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);

	}
}
