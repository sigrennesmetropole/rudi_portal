package org.rudi.facet.apigateway.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.apigateway")
@Getter
@Setter
@SuppressWarnings("java:S1075") // Les URL par défaut des paramètres sont obligatoirement en dur dans le code
public class ApiGatewayProperties {
	/**
	 * URL de base pour appeler le micro-service Strukture via Load Balancer
	 */
	private String serviceBaseUrl = "lb://RUDI-APIGATEWAY/apigateway/v1/";

	private String apisPath = "/apis";

	private String apisDeletePath = "/apis/{api-uuid}";

	private String throttlingsPath = "/throttlings";

}
