/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.facade", "org.rudi.common.service",
		"org.rudi.common.storage", "org.rudi.microservice.acl.facade", "org.rudi.microservice.acl.service",
		"org.rudi.microservice.acl.storage", "org.rudi.facet.apimaccess", "org.rudi.facet.email",
		"org.rudi.facet.generator" })
@EnableEurekaClient
@EnableScheduling
@EnableAuthorizationServer
@PropertySource(value = { "classpath:acl/acl-common.properties" })
@PropertySource(value = { "classpath:acl/acl-email.properties" })
public class AppFacadeApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {

		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "acl");
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(AppFacadeApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(AppFacadeApplication.class);
	}

}
