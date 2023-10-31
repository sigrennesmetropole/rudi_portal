package org.rudi.microservice.kalim.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = {
		"org.rudi.common.facade",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.common.core",
		"org.rudi.facet.apimaccess",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.kos",
		"org.rudi.facet.acl",
		"org.rudi.facet.strukture",
		"org.rudi.facet.providers",
		"org.rudi.facet.organization",
		"org.rudi.microservice.kalim.facade",
		"org.rudi.microservice.kalim.service",
		"org.rudi.microservice.kalim.storage",
		"org.rudi.facet.apimremote",
})
@EnableEurekaClient
@EnableScheduling
@PropertySource(value = { "classpath:kalim/kalim-common.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "kalim");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
