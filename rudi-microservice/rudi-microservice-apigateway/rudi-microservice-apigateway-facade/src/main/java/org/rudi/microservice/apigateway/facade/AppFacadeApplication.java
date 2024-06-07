package org.rudi.microservice.apigateway.facade;

import org.rudi.common.core.yml.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { 
		"org.rudi.common.core", 
		"org.rudi.common.service", 
		"org.rudi.common.storage",
		"org.rudi.facet.acl",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.projekt",
		"org.rudi.facet.selfdata",
		"org.rudi.microservice.apigateway.facade", "org.rudi.microservice.apigateway.service",
		"org.rudi.microservice.apigateway.storage", })
@EnableEurekaClient
@EnableJpaAuditing
@PropertySource(value = { "classpath:apigateway/apigateway-common.properties" })
@PropertySource(value = {
		"classpath:apigateway/apigateway.yml" }, ignoreResourceNotFound = true, factory = YamlPropertySourceFactory.class)
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "apigateway");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
