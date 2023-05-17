package org.rudi.microservice.selfdata.facade;

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
		"org.rudi.common.core",
		"org.rudi.common.service",
		"org.rudi.common.storage",
		"org.rudi.common.core",
		"org.rudi.facet.apimaccess",
		"org.rudi.facet.crypto",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.doks",
		"org.rudi.facet.kaccess",
		"org.rudi.facet.acl.helper",
		"org.rudi.facet.bpmn",
		"org.rudi.facet.email",
		"org.rudi.facet.generator",
		"org.rudi.facet.organization",
		"org.rudi.facet.rva",
		"org.rudi.facet.strukture",
		"org.rudi.facet.dataverse",
		"org.rudi.facet.kaccess",
		"org.rudi.microservice.selfdata.core",
		"org.rudi.microservice.selfdata.facade",
		"org.rudi.microservice.selfdata.service",
		"org.rudi.microservice.selfdata.storage",
		"org.rudi.facet.providers",
		"org.rudi.facet.apimremote",
})
@EnableEurekaClient
@PropertySource(value = { "classpath:selfdata/selfdata-common.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "selfdata");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
