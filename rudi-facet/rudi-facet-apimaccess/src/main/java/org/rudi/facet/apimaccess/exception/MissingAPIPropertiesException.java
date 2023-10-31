package org.rudi.facet.apimaccess.exception;

/**
 * Chaque API déclarée dans WSO2 par RUDI doit posséder des propriétés. On les retrouve dans le Publisher WSO2 pour chaque API dans la section
 * "Properties". Exemple d'URL d'API : /publisher/apis/05b0b019-ae02-4d09-917e-29f2dc91a6c9/properties
 *
 * @see <a href="https://apim.docs.wso2.com/en/latest/design/create-api/adding-custom-properties-to-apis/">Documentation WSO2 sur les Properties</a>
 */
public class MissingAPIPropertiesException extends APIManagerException {

	private static final long serialVersionUID = -916446440316523867L;

	public MissingAPIPropertiesException(String apiId) {
		super(String.format("L'API %s ne possède aucune propriétés", apiId));
	}
}
