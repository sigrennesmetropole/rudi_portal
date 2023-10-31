package org.rudi.microservice.konsent.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.facade",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.facet.acl",
		"org.rudi.facet.organization",
		"org.rudi.microservice.konsent.service",
		"org.rudi.microservice.konsent.core",
		"org.rudi.microservice.konsent.storage",
		"org.rudi.facet.strukture",
		"org.rudi.facet.organization",
		"org.rudi.facet.generator",
		"org.rudi.facet.generator.docx",
		"org.rudi.facet.generator.pdf",
		"org.rudi.facet.buckets3",
})
public class SpringBootTestApplication {
}
