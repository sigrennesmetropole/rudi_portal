package org.rudi.facet.strukture;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rudi.strukture")
@Getter
@Setter
public class StruktureProperties {

	/**
	 * URL de base pour appeler le micro-service Strukture via Load Balancer
	 */
	private String serviceBaseUrl = "lb://RUDI-STRUKTURE/strukture/v1/";

}
