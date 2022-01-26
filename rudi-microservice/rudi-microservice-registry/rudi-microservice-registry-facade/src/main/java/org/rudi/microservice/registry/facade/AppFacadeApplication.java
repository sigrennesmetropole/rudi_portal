package org.rudi.microservice.registry.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.facade", "org.rudi.common.service",
		"org.rudi.common.storage", "org.rudi.microservice.registry.facade" })
@EnableEurekaServer
@PropertySource(value = { "classpath:registry-common.properties" }, ignoreResourceNotFound = false)
@PropertySource(value = { "file:${rudi.config}/registry/registry.properties" }, ignoreResourceNotFound = true)
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "registry");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
