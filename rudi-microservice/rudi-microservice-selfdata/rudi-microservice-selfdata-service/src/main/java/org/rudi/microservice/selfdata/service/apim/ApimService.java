package org.rudi.microservice.selfdata.service.apim;

import javax.net.ssl.SSLException;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;

public interface ApimService {

	/**
	 * Active les accès par APIs pour l'utilisateur correspondant aux éléments donnés
	 *
	 * @param credentials les identifiants et mots de passe
	 * @throws SSLException        erreur SSL
	 * @throws APIManagerException erreur WSO2
	 */
	void enableApi(Credentials credentials) throws SSLException, APIManagerException, AppServiceForbiddenException;

	/**
	 * Vérifie si les accès ont été activés pour le user correspondant aux éléments donnés
	 *
	 * @param credentials les identifiants et mots de passe
	 * @return vrai/faux activation des APIs ?
	 * @throws SSLException erreur SSL
	 */
	boolean hasEnabledApi(Credentials credentials) throws SSLException, AppServiceForbiddenException, GetClientRegistrationException;
}
