package org.rudi.facet.apimaccess.service;

import org.rudi.facet.apimaccess.bean.API;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.exception.APIManagerException;

import java.util.List;

public interface APIsService {

    /**
     * Création d'une API
     * @param apiDescription        paramètre de la nouvelle API
     * @return                      API
     * @throws APIManagerException  Erreur lors de la création
     */
    API createAPI(APIDescription apiDescription) throws APIManagerException;

    /**
     * Mise à jour d'une API
     * @param apiDescription        paramètres de l'API
     * @param apiId                 Identifiant de l'API
     * @return                      API
     * @throws APIManagerException  Erreur lors de la mise à jour
     */
    API updateAPI(APIDescription apiDescription, String apiId) throws APIManagerException;

    /**
     * Mise à jour d'une API en se basant sur son nom
     * @param apiDescription        paramètres de l'API
     * @return                      API
     * @throws APIManagerException  Erreur lors de la mise à jour
     */
    API updateAPIByName(APIDescription apiDescription) throws APIManagerException;

    /**
     * Modifier le cycle de vie d'une API
     * @param apiId                         Identifiant de l'API
     * @param apiLifecycleStatusAction      Nouveau status
     * @return                              APIWorkflowResponse
     * @throws APIManagerException          Erreur lors du changement de status
     */
    APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction) throws APIManagerException;

    /**
     * Suppression d'une API
     * @param apiId         identifiant de l'API
     * @throws APIManagerException  Erreur lors du changement de status
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
    APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIManagerException;

    /**
     * Liste des subscription policies disponibles pour une API
     * @param apiId                 Identifiant de l'API
     * @return                      List de SubscriptionLimitingPolicy
     * @throws APIManagerException  Erreur lors de la récupération de la liste des subscription policies
     */
    List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIManagerException;
}
