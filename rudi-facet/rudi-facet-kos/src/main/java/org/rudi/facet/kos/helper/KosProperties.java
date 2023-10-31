package org.rudi.facet.kos.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.kos")
@Getter
@Setter
@SuppressWarnings("java:S1075") // Les URL par défaut des paramètres sont obligatoirement en dur dans le code
public class KosProperties {

	private String serviceBaseUrl = "lb://RUDI-KOS/kos/v1";

	private String searchSkosConceptsPath = "/skosConcepts";
}
