package org.rudi.microservice.template.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.microservice.template.service",
		"org.rudi.microservice.template.storage",
})
public class SpringBootTestApplication {
}
