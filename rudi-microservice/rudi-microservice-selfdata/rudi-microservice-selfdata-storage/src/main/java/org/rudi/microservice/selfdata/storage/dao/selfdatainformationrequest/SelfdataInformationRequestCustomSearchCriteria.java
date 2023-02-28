package org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest;

import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequestSearchCriteria;

import lombok.Getter;
import lombok.Setter;

/**
 * Critères de recherche pour une recherche custom de demandes
 */
@Getter
@Setter
public class SelfdataInformationRequestCustomSearchCriteria extends SelfdataInformationRequestSearchCriteria {

	/**
	 * Le login d'un utilisateur ayant créé des demandes
	 */
	String login;
}
