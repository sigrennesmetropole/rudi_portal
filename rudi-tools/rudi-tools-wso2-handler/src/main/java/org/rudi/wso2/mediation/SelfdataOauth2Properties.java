package org.rudi.wso2.mediation;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Propriétés oauth2 inspirées de : org.rudi.facet.oauth2.config.WebClientConfig.
 */
@WSO2HandlerProperties(prefix = "selfdata.oauth2")
@Getter
@Setter
@SuppressWarnings({
		"java:S1075", // Les URL dans le code sont les valeurs par défaut. Elles sont surchargées dans les fichiers de properties.
})
class SelfdataOauth2Properties {
	public static final String REGISTRATION_ID = "rudi_module";
	private String clientId;
	private String clientSecret;
	private String tokenUri;

	/**
	 * Scope(s) séparés par des virgules
	 */
	private String scope;

	String getScopeSeparatedWithSpaces() {
		if (scope == null) {
			return null;
		} else {
			return StringUtils.joinWith(" ", (Object[]) scope.split(","));
		}
	}
}
