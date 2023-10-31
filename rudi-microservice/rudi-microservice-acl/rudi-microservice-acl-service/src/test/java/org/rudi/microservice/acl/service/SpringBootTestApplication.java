package org.rudi.microservice.acl.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { 
		"org.rudi.common.core", 
		"org.rudi.common.service",
		"org.rudi.common.storage", 
		"org.rudi.facet.apimaccess", 
		"org.rudi.facet.email", 
		"org.rudi.facet.generator", 
		"org.rudi.microservice.acl.service", 
		"org.rudi.microservice.acl.storage"
		})
public class SpringBootTestApplication {
}
