package org.rudi.facet.bpmn;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Classe application pour les tests unitaires de la couche service
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.service", "org.rudi.common.storage", "org.rudi.facet.bpmn",
		"org.rudi.facet.generator", "org.rudi.facet.email", "org.rudi.facet.acl" })
public class SpringBootTestApplication {

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}

}
