package org.rudi.microservice.apigateway.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.facet.crypto",
		"org.rudi.microservice.apigateway.service",
		"org.rudi.microservice.apigateway.storage",
})
public class SpringBootTestApplication {
}
