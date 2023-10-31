package org.rudi.facet.apimaccess.api;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.facet.apimaccess.api",
		"org.rudi.facet.apimaccess.helper",
		"org.rudi.facet.apimaccess.exception",
		"org.rudi.facet.apimaccess.service",
})
public class SpringBootTestApplication {
	
	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}
}
