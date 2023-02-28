package org.rudi.microservice.selfdata.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.common.core",
		"org.rudi.facet.apimaccess",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.email",
		"org.rudi.facet.generator",
		"org.rudi.facet.organization",
		"org.rudi.facet.rva",
		"org.rudi.facet.strukture",
		"org.rudi.facet.acl.helper",
		"org.rudi.facet.bpmn",
		"org.rudi.microservice.selfdata.core",
		"org.rudi.microservice.selfdata.service",
		"org.rudi.microservice.selfdata.storage",
		"org.rudi.facet.providers",
		"org.rudi.facet.doks",
		"org.rudi.facet.crypto",
})
public class SpringBootTestApplication {
}
