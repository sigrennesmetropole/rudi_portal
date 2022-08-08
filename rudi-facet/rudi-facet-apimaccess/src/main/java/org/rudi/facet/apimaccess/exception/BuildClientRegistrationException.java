package org.rudi.facet.apimaccess.exception;

/**
 * Lorsque cette erreur apparaît, on peut tenter de faire disparaître l'erreur en se connectant avec le login concerné sur les différentes applications WSO2. Notamment :
 *
 * <ul>
 *     <li>Carbon : <code>/carbon</code></li>
 *     <li>Dev Portal : <code>/devportal/applications</code> puis sélectionner <code>rudi_application</code> puis Production Keys</li>
 * </ul>
 */
public class BuildClientRegistrationException extends APIManagerException {
	public BuildClientRegistrationException(String username, Throwable cause) {
		super(String.format("Erreur lors de la construction de l'enregistrement client pour %s", username), cause);
	}
}
