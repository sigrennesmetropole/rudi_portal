package org.rudi.microservice.projekt.service.project;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "projekt.project-media")
@Getter
@Setter
public class ProjectMediaProperties {
	private LogoProperties logo = new LogoProperties();

	@Configuration
	@ConfigurationProperties(prefix = "projekt.project-media.logo")
	@Getter
	@Setter
	public static class LogoProperties {
		private final String[] extensions = { "png", "jpg" };
	}
}
