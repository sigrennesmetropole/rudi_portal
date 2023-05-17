package org.rudi.facet.apimaccess.service;

import java.util.List;
import java.util.UUID;

import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APIsOperationException;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;

public interface APIsService {

	/**
	 * Création d'une API ou mise à jour si l'API existe déjà
	 *
	 * @param apiDescription paramètre de l'API
	 * @return API
	 * @throws APIManagerException Erreur lors de la création / mise à jour
	 */
	API createOrUnarchiveAPI(APIDescription apiDescription) throws APIManagerException;

	/**
	 * Mise à jour d'une API en se basant sur son nom
	 *
	 * @param apiDescription paramètres de l'API
	 * @throws APIManagerException Erreur lors de la mise à jour
	 */
	void updateAPIByName(APIDescription apiDescription) throws APIManagerException;

	/**
	 * Modifier le cycle de vie d'une API
	 *
	 * @param apiId                    Identifiant de l'API
	 * @param apiLifecycleStatusAction Nouveau status
	 * @return APIWorkflowResponse
	 * @throws APIManagerException Erreur lors du changement de status
	 */
	APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction) throws APIManagerException;

	/**
	 * Archivage d'une API par son nom avant suppression définitive.
	 * L'API est automatiquement désarchivée lorsqu'on la recrée avec {@link #createOrUnarchiveAPI(APIDescription)}.
	 *
	 * @param apiDescription identifiant de l'API
	 * @return l'API archivée
	 * @throws APIManagerException Erreur lors du changement de statut
	 * @see #deleteAPI(String)
	 * @see #createOrUnarchiveAPI(APIDescription)
	 */
	API archiveAPIByName(APIDescription apiDescription) throws APIManagerException;

	/**
	 * Archivage d'une API avant suppression définitive.
	 * L'API est automatiquement désarchivée lorsqu'on la recrée avec {@link #createOrUnarchiveAPI(APIDescription)}.
	 *
	 * @param apiId identifiant de l'API
	 * @throws APIManagerException Erreur lors du changement de statut
	 * @see #deleteAPI(String)
	 * @see #createOrUnarchiveAPI(APIDescription)
	 */
	void archiveAPI(String apiId) throws APIManagerException;

	/**
	 * Désarchivage / Republication d'une API.
	 *
	 * @param apiId identifiant de l'API
	 * @throws APIManagerException Erreur lors du changement de statut
	 */
	void unarchiveAPI(String apiId) throws APIManagerException;

	/**
	 * Suppression définitive d'une API.
	 * Possible seulement si toutes les souscriptions ont été supprimées,
	 * par exemple via {@link org.rudi.facet.apimaccess.service.ApplicationService#deleteAllSubscriptionsWithoutRetiringAPI(String)}.
	 *
	 * @param apiId identifiant de l'API
	 * @throws APIManagerException Erreur lors du changement de statut
	 * @see #archiveAPI(String)
	 */
	void deleteAPI(String apiId) throws APIManagerException;

	/**
	 * Récupération d'une API
	 *
	 * @param apiId Identifiant de l'API
	 * @return API
	 * @throws APIManagerException Erreur lors de la récupération d'une API
	 */
	API getAPI(String apiId) throws APIManagerException;

	API getAPIFromDevportal(String apiId, String username) throws APIManagerException;

	/**
	 * Recherche des API
	 *
	 * @param apiSearchCriteria Critères de recherche
	 * @return APIList
	 * @throws APIManagerException Erreur lors de la recherche
	 */
	APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIsOperationException;

	/**
	 * Liste des subscription policies disponibles pour une API
	 *
	 * @param apiId Identifiant de l'API
	 * @return List de SubscriptionLimitingPolicy
	 * @throws APIManagerException Erreur lors de la récupération de la liste des subscription policies
	 */
	List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIManagerException;

	/**
	 * Est-ce que pour les identifiants fournis du JDD, on a bien une API de créée
	 *
	 * @param globalId Identifiant des métadonnées
	 * @param mediaId  Identifiant du média des métadonnées
	 * @return si oui ou non le JDD a une API
	 */
	boolean existsApi(UUID globalId, UUID mediaId) throws APIManagerException;
}
