package org.rudi.facet.apimaccess.exception;

/**
 * Il manque une propriété attendue dans une API WSO2.
 *
 * @see MissingAPIPropertiesException
 * @see org.rudi.facet.apimaccess.constant.APISearchPropertyKey
 */
public class MissingAPIPropertyException extends APIManagerException {
	/**
	 * @param apiSearchPropertyKey Nom d'une propriété parmi les constantes de la classe {@link org.rudi.facet.apimaccess.constant.APISearchPropertyKey}
	 * @see org.rudi.facet.apimaccess.constant.APISearchPropertyKey
	 */
	public MissingAPIPropertyException(String apiSearchPropertyKey, String apiId) {
		super(String.format("L'API %s ne possède pas la propriété %s", apiId, apiSearchPropertyKey));
	}
}
