package org.rudi.microservice.kos.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.facade", "org.rudi.common.service",
		"org.rudi.common.storage", "org.rudi.microservice.kos.facade", "org.rudi.microservice.kos.service",
		"org.rudi.microservice.kos.storage" })
@EnableEurekaClient
@PropertySource(value = { "classpath:kos-common.properties" }, ignoreResourceNotFound = false)
@PropertySource(value = { "file:${rudi.config}/kos/kos.properties" }, ignoreResourceNotFound = true)
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "kos");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
