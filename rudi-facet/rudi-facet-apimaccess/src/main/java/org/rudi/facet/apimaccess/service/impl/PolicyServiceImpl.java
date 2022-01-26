package org.rudi.facet.apimaccess.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.api.policy.ThrottlingPolicyOperationAPI;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.PolicyLevel;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT_MAX_VALUE;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyServiceImpl.class);

    @Autowired
    private ThrottlingPolicyOperationAPI throttlingPolicyOperationAPI;

    @Override
    public LimitingPolicies searchSubscriptionLimitingPolicies(SearchCriteria searchCriteria) throws APIManagerException {
        if (searchCriteria == null) {
            searchCriteria = new SearchCriteria();
        }
        if (searchCriteria.getLimit() != null && searchCriteria.getLimit() > LIMIT_MAX_VALUE ) {
            LOGGER.warn("Le nombre de politique de souscription demandé dépasse le nombre d'élément maximum autorisé: {} > {}",
                    searchCriteria.getLimit(), LIMIT_MAX_VALUE);
            searchCriteria.setLimit(LIMIT_MAX_VALUE);
        }
        return throttlingPolicyOperationAPI.searchLimitingPoliciesByPublisher(searchCriteria, PolicyLevel.SUBSCRIPTION);
    }

    @Override
    public LimitingPolicies searchApplicationLimitingPolicies(SearchCriteria searchCriteria, String username) throws APIManagerException {
        if (searchCriteria == null) {
            searchCriteria = new SearchCriteria();
        }
        if (searchCriteria.getLimit() != null && searchCriteria.getLimit() > LIMIT_MAX_VALUE) {
            LOGGER.warn("Le nombre de politique de requête pour une application demandé dépasse le nombre d'élément maximum autorisé: {} > {}",
                    searchCriteria.getLimit(), LIMIT_MAX_VALUE);
            searchCriteria.setLimit(LIMIT_MAX_VALUE);
        }
        return throttlingPolicyOperationAPI.searchLimitingPoliciesByDev(searchCriteria, PolicyLevel.APPLICATION, username);
    }

    @Override
    public LimitingPolicy getSubscriptionLimitingPolicy(String policyName) throws APIManagerException {
        if (StringUtils.isEmpty(policyName)) {
            throw new APIManagerException("Le nom de la politique de souscription à une API est manquante");
        }
        return throttlingPolicyOperationAPI.getLimitingPolicyByPublisher(policyName, PolicyLevel.SUBSCRIPTION);
    }

    @Override
    public LimitingPolicy getApplicationLimitingPolicy(String policyName, String username) throws APIManagerException {
        if (StringUtils.isEmpty(policyName)) {
            throw new APIManagerException("Le nom de la politique de souscription pour une application est manquante");
        }
        return throttlingPolicyOperationAPI.getLimitingPolicyByDev(policyName, PolicyLevel.APPLICATION, username);
    }
}
