package org.rudi.microservice.acl.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.service", "org.rudi.common.storage",
		"org.rudi.microservice.acl.service", "org.rudi.microservice.acl.storage", "org.rudi.facet.apimaccess" })
@PropertySource(value = { "classpath:acl_test.properties" }, ignoreResourceNotFound = false)
public class SpringBootTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);

	}
}
