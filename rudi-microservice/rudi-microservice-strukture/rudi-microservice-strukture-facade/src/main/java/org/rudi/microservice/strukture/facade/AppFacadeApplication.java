package org.rudi.microservice.strukture.facade;

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
		"org.rudi.common.facade", 
		"org.rudi.common.service",
		"org.rudi.common.storage", 
		"org.rudi.common.core", 
		"org.rudi.facet.acl", 
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kmedia",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.email", 
		"org.rudi.facet.projekt.helper",
		"org.rudi.facet.generator",
		"org.rudi.microservice.strukture.facade",
		"org.rudi.microservice.strukture.service", 
		"org.rudi.microservice.strukture.storage"
		})
@EnableEurekaClient
@PropertySource(value = { "classpath:strukture/strukture-common.properties" })
@PropertySource(value = { "classpath:strukture/strukture-email.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "strukture");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
