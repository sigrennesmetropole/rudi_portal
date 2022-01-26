package org.rudi.facet.apimaccess.service;

import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;

public interface PolicyService {

    /**
     * Recherche de subscription policies
     * @param searchCriteria            Critères de recherche
     * @return                          LimitingPolicies
     */
    LimitingPolicies searchSubscriptionLimitingPolicies(SearchCriteria searchCriteria) throws APIManagerException;

    /**
     * Recherche de application policies
     * @param searchCriteria            Critères de recherche
     * @return                          LimitingPolicies
     */
    LimitingPolicies searchApplicationLimitingPolicies(SearchCriteria searchCriteria, String username) throws APIManagerException;

    /**
     * Récupération d'un subscription policy à partir de son nom
     * @param policyName                Nom du subscription policy
     * @return                          LimitingPolicy
     */
    LimitingPolicy getSubscriptionLimitingPolicy(String policyName) throws APIManagerException;

    /**
     * Récupération d'une application policy à partir de son nom
     * @param policyName                Nom de l'application policy
     * @return                          LimitingPolicy
     */
    LimitingPolicy getApplicationLimitingPolicy(String policyName, String username) throws APIManagerException;
}
