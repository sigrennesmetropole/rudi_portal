package org.rudi.facet.apimaccess.service;

import org.rudi.facet.apimaccess.bean.API;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APIsOperationException;

import java.util.List;

public interface APIsService {

    /**
     * Création d'une API ou mise à jour si l'API existe déjà
     * @param apiDescription        paramètre de l'API
     * @return                      API
     * @throws APIManagerException  Erreur lors de la création / mise à jour
     */
    API createOrUnarchiveAPI(APIDescription apiDescription) throws APIManagerException;

    /**
     * Mise à jour d'une API en se basant sur son nom
     * @param apiDescription        paramètres de l'API
     * @throws APIManagerException  Erreur lors de la mise à jour
     */
    void updateAPIByName(APIDescription apiDescription) throws APIManagerException;

    /**
     * Modifier le cycle de vie d'une API
     * @param apiId                         Identifiant de l'API
     * @param apiLifecycleStatusAction      Nouveau status
     * @return                              APIWorkflowResponse
     * @throws APIManagerException          Erreur lors du changement de status
     */
    APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction) throws APIManagerException;

    /**
     * Archivage d'une API par son nom avant suppression définitive.
     * L'API est automatiquement désarchivée lorsqu'on la recrée avec {@link #createOrUnarchiveAPI(APIDescription)}.
     *
     * @param apiDescription identifiant de l'API
     * @throws APIManagerException Erreur lors du changement de statut
     * @see #deleteAPI(String)
     * @see #createOrUnarchiveAPI(APIDescription)
     * @return l'API archivée
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
     * Suppression définitive d'une API
     *
     * @param apiId identifiant de l'API
     * @throws APIManagerException Erreur lors du changement de statut
     * @see #archiveAPI(String)
     */
    void deleteAPI(String apiId) throws APIManagerException;

    /**
     * Récupération d'une API
     * @param apiId                 Identifiant de l'API
     * @return                      API
     * @throws APIManagerException  Erreur lors de la récupération d'une API
     */
    API getAPI(String apiId) throws APIManagerException;

    /**
     * Recherche des API
     * @param apiSearchCriteria     Critères de recherche
     * @return                      APIList
     * @throws APIManagerException  Erreur lors de la recherche
     */
    APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIsOperationException;

    /**
     * Liste des subscription policies disponibles pour une API
     * @param apiId                 Identifiant de l'API
     * @return                      List de SubscriptionLimitingPolicy
     * @throws APIManagerException  Erreur lors de la récupération de la liste des subscription policies
     */
    List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIManagerException;
}
