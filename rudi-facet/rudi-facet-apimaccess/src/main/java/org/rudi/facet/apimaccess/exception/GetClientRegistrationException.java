package org.rudi.facet.apimaccess.exception;

public class GetClientRegistrationException extends APIManagerException {

	private static final long serialVersionUID = -3005497551304514179L;

	public GetClientRegistrationException(String username, Throwable cause) {
		super(String.format("Erreur lors de la récupération de l'enregistrement client pour %s", username), cause);
	}
}
