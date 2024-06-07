/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.helper;

import java.util.List;

import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;

/**
 * @author FNI18300
 *
 */
public interface ApiManagerHelper {

	/**
	 * Création des APIs liées aux métadonnées
	 *
	 * @param integrationRequest demande d'intégration
	 * @param metadata           médatonnées
	 * @return la liste des API créées
	 * @throws APIManagerException erreur lors de la création des apis
	 */
	void createApis(IntegrationRequestEntity integrationRequest, Metadata metadata) throws APIManagerException;

	/**
	 * mise à des APIs liées aux métadonnées
	 *
	 * @param integrationRequest demande d'intégration
	 * @param metadata           médatonnées qui représentent le nouvel état
	 * @param actualMetadata     métadonnées qui représentent l'état avant la modif
	 * @throws APIManagerException erreur lors de la mise à jour des APIs
	 */
	void updateApis(IntegrationRequestEntity integrationRequest, Metadata metadata, Metadata actualMetadata)
			throws APIManagerException;

	/**
	 * Suppression totale des APIs d'un JDD (et de toutes les souscriptions à cette API)
	 *
	 * @param integrationRequest demande d'intégration
	 * @throws APIManagerException erreur lors de la suppression des APIs
	 */
	void deleteApis(IntegrationRequestEntity integrationRequest) throws APIManagerException;

	/**
	 * Filtrage des média "valides" pour RUDI dans un JDD
	 *
	 * @param metadata le JDD qui contient les médias
	 * @return la liste des médias sur lesquels on va vraiment faire quelque chose
	 */
	List<Media> getValidMedias(Metadata metadata);

	List<Api> synchronizeMedias(Metadata metadata);
}
