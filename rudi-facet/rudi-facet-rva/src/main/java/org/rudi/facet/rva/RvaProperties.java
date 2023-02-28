package org.rudi.facet.rva;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.rva")
@Getter
@Setter
public class RvaProperties {
	/**
	 * Clé de l'API RVA
	 */
	private String key = "4bac63d09d9d45b747b6";

	/**
	 * Version de l'API
	 */
	private String version = "1.0";

	/**
	 * URL de l'API
	 */
	private String url = "https://api-rva.sig.rennesmetropole.fr";

	/**
	 * Commande getFullAddress
	 */
	private String commandFullAddresses = "getfulladdresses";

	/**
	 * Commande getAddressById
	 */
	private String commandAddressById = "getaddressbyid";

	/**
	 * Format de la réponse
	 */
	private String format = "json";

	/**
	 * Système de réference
	 */
	private String epsg = "4326";

	private int queryMinLength = 3;
}
