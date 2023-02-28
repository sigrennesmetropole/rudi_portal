package org.rudi.facet.selfdata.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.selfdata")
@Getter
@Setter
@SuppressWarnings("java:S1075") // Les URL par défaut des paramètres sont obligatoirement en dur dans le code
public class SelfdataProperties {
	/**
	 * URL de base de l'API selfdata (cf basePath dans le fichier rudi-selfdata-api.json)
	 */
	private String baseUrl = "lb://RUDI-SELFDATA/selfdata/v1";
	private String hasMatchingToDatasetPath = "/matching/{userUuid}/check-my-matching/{datasetUuid}";
}