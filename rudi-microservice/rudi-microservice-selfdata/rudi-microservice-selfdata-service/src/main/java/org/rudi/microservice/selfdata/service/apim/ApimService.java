package org.rudi.microservice.selfdata.service.apim;

import javax.net.ssl.SSLException;

import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.facet.apimaccess.exception.APIManagerException;

public interface ApimService {

	/**
	 * Active les accès par APIs pour l'utilisateur correspondant aux éléments donnés
	 * @param credentials les identifiants et mots de passe
	 * @throws SSLException erreur SSL
	 * @throws APIManagerException erreur WSO2
	 */
	void enableApi(Credentials credentials) throws SSLException, APIManagerException;

	/**
	 * Vérifie si les accès ont été activés pour le user correspondant aux éléments donnés
	 * @param credentials les identifiants et mots de passe
	 * @return vrai/faux activation des APIs ?
	 * @throws SSLException erreur SSL
	 */
	boolean hasEnabledApi(Credentials credentials) throws SSLException;
}
