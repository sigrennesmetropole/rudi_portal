package org.rudi.microservice.konsult.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.storage",
		"org.rudi.common.service",
		"org.rudi.facet.acl",
		"org.rudi.facet.rva",
		"org.rudi.facet.apimaccess",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.projekt",
		"org.rudi.facet.selfdata",
		"org.rudi.facet.organization",
		"org.rudi.facet.apimremote",
		"org.rudi.microservice.konsult.service",
})
public class SpringBootTestApplication {
}
