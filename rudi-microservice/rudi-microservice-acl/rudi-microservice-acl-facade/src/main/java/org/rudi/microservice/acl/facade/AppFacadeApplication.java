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
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication(scanBasePackages = { "org.rudi.common.facade", "org.rudi.common.service",
		"org.rudi.common.storage", "org.rudi.microservice.acl.facade", "org.rudi.microservice.acl.service",
		"org.rudi.microservice.acl.storage", "org.rudi.facet.apimaccess" })
@EnableEurekaClient
@EnableAuthorizationServer
@PropertySource(value = { "classpath:acl-common.properties" }, ignoreResourceNotFound = false)
@PropertySource(value = { "file:${rudi.config}/acl/acl.properties" }, ignoreResourceNotFound = true)
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
