package org.rudi.wso2.mediation;

import lombok.Getter;
import lombok.Setter;

/**
 * Propriétés chargées depuis le fichier <code>org.rudi.wso2.handler.properties</code>.
 */
@WSO2HandlerProperties(prefix = "selfdata")
@Getter
@Setter
@SuppressWarnings({ "java:S1075", // Les URL dans le code sont les valeurs par défaut. Elles sont surchargées dans les fichiers de properties.
})
class SelfdataProperties {

	/**
	 * URL (sans slash à la fin) de base de l'API de selfdata, tel que décrit dans son swagger, pour l'environnement actuel. Exemple :
	 * <code>https://<host>/selfdata/v1</code>
	 */
	@SuppressWarnings("JavadocLinkAsPlainText") // On ne veut pas que l'URL soit cliquable.
	private String basePath;

	/**
	 * Chemin (avec un slash au début) pour accéder à l'API qui récupère le token selfdata pour le tuple JDD/utilisateur.
	 */
	private String matchingTokenPath = "/matching/{dataset-uuid}/{login}";
}
