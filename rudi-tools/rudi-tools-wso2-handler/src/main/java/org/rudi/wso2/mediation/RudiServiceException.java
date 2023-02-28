package org.rudi.wso2.mediation;

class RudiServiceException extends RuntimeException {

	private static final long serialVersionUID = 9164344589788946415L;

	public RudiServiceException(String serviceName, Throwable cause) {
		super("Erreur lors de l'appel au service RUDI " + serviceName, cause);
	}
}
