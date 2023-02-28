package org.rudi.facet.doks.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.doks")
@Getter
@Setter
public
class DoksProperties {

	/**
	 * Chemin vers la clé publique utilisée pour chiffrer les documents nécessitant un chiffrement.
	 * Relatif au classpath ou additional-location (cf. org.rudi.common.service.helper.ResourceHelper#getResourceFromAdditionalLocationOrFromClasspath(java.lang.String))
	 */
	private String publicKeyPath = "doks_public_key.pem";

	/**
	 * Chemin vers la clé publique utilisée pour chiffrer les documents nécessitant un chiffrement.
	 * Relatif au classpath ou additional-location (cf. org.rudi.common.service.helper.ResourceHelper#getResourceFromAdditionalLocationOrFromClasspath(java.lang.String))
	 */
	private String privateKeyPath = "doks_private_key.pem";

	/**
	 * Taille max d'un document. Par défaut : 10 Mo
	 */
	private long maxFileSize = (long) 10 * 1024 * 1024;

	/**
	 * Autoriser la création de dossiers temporaires non sécurisés pour extraire les documents.
	 * <b>Interdit en production, si les documents contiennent des données sensibles, car les documents déchiffrés
	 * se retrouveraient accessibles par n'importe qui ayant accès aux dossiers temporaires.</b>
	 */
	private boolean unsecuredTempDirectoryAllowed = false;

}
