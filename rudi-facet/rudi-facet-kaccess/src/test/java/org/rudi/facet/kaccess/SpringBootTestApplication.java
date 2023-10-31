package org.rudi.facet.kaccess;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.rudi.facet.kaccess.config.KaccessSubtypeMapperConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(scanBasePackages = { "org.rudi.facet.dataverse.api", "org.rudi.facet.dataverse.fields",
		"org.rudi.facet.dataverse.helper", "org.rudi.facet.kaccess.service", "org.rudi.facet.kaccess.helper",
		"org.rudi.facet.kaccess.config" })
public class SpringBootTestApplication {

	@Autowired
	private KaccessSubtypeMapperConfigurer kaccessSubtypeMapperConfigurer;

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new DefaultJackson2ObjectMapperBuilder();
		builder.postConfigurer(objectMapper -> kaccessSubtypeMapperConfigurer.addSubTypes(objectMapper));
		return builder;
	}
}
