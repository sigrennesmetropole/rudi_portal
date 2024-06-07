package org.rudi.microservice.apigateway.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.microservice.apigateway.service",
		"org.rudi.microservice.apigateway.storage",
})
public class SpringBootTestApplication {
}
