package org.rudi.microservice.projekt.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.core",
		"org.rudi.common.facade",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.facet.acl.helper",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kmedia",
		"org.rudi.facet.oauth2",
		"org.rudi.facet.strukture",
		"org.rudi.facet.organization",
		"org.rudi.facet.bpmn",
		"org.rudi.facet.email",
		"org.rudi.facet.generator",
		"org.rudi.facet.kaccess",
		"org.rudi.microservice.projekt.core",
		"org.rudi.microservice.projekt.facade",
		"org.rudi.microservice.projekt.service",
		"org.rudi.microservice.projekt.storage",
		"org.rudi.facet.projekt.helper"
})
@EnableEurekaClient
@PropertySource(value = { "classpath:projekt/projekt-common.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "projekt");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
