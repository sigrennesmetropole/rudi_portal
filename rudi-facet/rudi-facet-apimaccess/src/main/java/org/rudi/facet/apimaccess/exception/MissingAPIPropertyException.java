package org.rudi.facet.apimaccess.exception;

/**
 * Il manque une propriété attendue dans une API WSO2.
 *
 * @see MissingAPIPropertiesException
 * @see org.rudi.facet.apimaccess.constant.APISearchPropertyKey
 */
public class MissingAPIPropertyException extends APIManagerException {

	private static final long serialVersionUID = 3814060433620240319L;

	/**
	 * @param apiSearchPropertyKey Nom d'une propriété parmi les constantes de la classe {@link org.rudi.facet.apimaccess.constant.APISearchPropertyKey}
	 * @see org.rudi.facet.apimaccess.constant.APISearchPropertyKey
	 */
	public MissingAPIPropertyException(String apiSearchPropertyKey, String apiId) {
		super(String.format("L'API %s ne possède pas la propriété %s", apiId, apiSearchPropertyKey));
	}
}
