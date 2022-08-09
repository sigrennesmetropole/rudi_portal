package org.rudi.facet.apimaccess.exception;

import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;

public class APISubscriptionException extends APIManagerException {
	public APISubscriptionException(Subscription applicationAPISubscription, String username, Throwable cause) {
		super(String.format("Erreur reçue de l'API Manager WSO2 lors de la souscription par l'utilisateur %s à l'API suivante : %s",
				username,
				applicationAPISubscription), cause);
	}
}
