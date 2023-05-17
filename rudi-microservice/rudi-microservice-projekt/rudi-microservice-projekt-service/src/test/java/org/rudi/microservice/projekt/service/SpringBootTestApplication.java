package org.rudi.microservice.projekt.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core.json",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.common.facade",
		"org.rudi.facet.acl",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kmedia",
		"org.rudi.facet.strukture",
		"org.rudi.facet.organization",
		"org.rudi.facet.bpmn",
		"org.rudi.facet.email",
		"org.rudi.facet.generator",
		"org.rudi.facet.kaccess",
		"org.rudi.microservice.projekt.core",
		"org.rudi.microservice.projekt.service",
		"org.rudi.microservice.projekt.storage",
		"org.rudi.facet.apimremote",
		"org.rudi.facet.apimaccess",
})
public class SpringBootTestApplication {
}
