package org.rudi.microservice.konsult.facade;

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
		"org.rudi.facet.acl",
		"org.rudi.facet.apimaccess",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.projekt",
		"org.rudi.facet.selfdata",
		"org.rudi.facet.rva",
		"org.rudi.facet.organization",
		"org.rudi.microservice.konsult.facade",
		"org.rudi.microservice.konsult.service",
		"org.rudi.facet.apimremote",
})
@EnableEurekaClient
@PropertySource(value = { "classpath:konsult/konsult-common.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "konsult");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
