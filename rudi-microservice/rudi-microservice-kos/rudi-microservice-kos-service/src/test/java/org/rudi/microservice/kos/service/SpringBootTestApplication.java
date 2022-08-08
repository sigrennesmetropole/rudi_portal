package org.rudi.microservice.kos.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

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
public class SpringBootTestApplication {
}
