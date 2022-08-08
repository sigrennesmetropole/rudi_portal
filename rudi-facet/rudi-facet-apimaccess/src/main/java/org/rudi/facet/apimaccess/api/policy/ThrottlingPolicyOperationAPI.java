package org.rudi.facet.apimaccess.api.policy;

import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.ManagerAPIProperties;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.PolicyLevel;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.ThrottlingPolicyOperationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_WEBCLIENT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.POLICY_LEVEL;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.POLICY_NAME;

@Component
public class ThrottlingPolicyOperationAPI extends AbstractManagerAPI {

	private static final String POLICY_LIST_PATH = "/throttling-policies/{policyLevel}";
	private static final String POLICY_GET_PATH = POLICY_LIST_PATH + "/{policyName}";

	ThrottlingPolicyOperationAPI(
			@Qualifier(API_MACCESS_WEBCLIENT) WebClient webClient,
			ManagerAPIProperties managerAPIProperties
	) {
		super(webClient, managerAPIProperties);
	}

	public LimitingPolicies searchLimitingPoliciesByPublisher(SearchCriteria searchCriteria, PolicyLevel policyLevel) throws ThrottlingPolicyOperationException {
		final Mono<LimitingPolicies> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(POLICY_LIST_PATH),
				uriBuilder -> uriBuilder
						.queryParam(OFFSET, searchCriteria.getOffset())
						.queryParam(LIMIT, searchCriteria.getLimit())
						.build(Map.of(POLICY_LEVEL, policyLevel.getValue().toLowerCase())))
				.retrieve()
				.bodyToMono(LimitingPolicies.class);
		return MonoUtils.blockOrThrow(mono, e -> new ThrottlingPolicyOperationException(searchCriteria, policyLevel, null, e));
	}

	public LimitingPolicies searchLimitingPoliciesByDev(SearchCriteria searchCriteria, PolicyLevel policyLevel, String username) throws ThrottlingPolicyOperationException {
		final Mono<LimitingPolicies> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(POLICY_LIST_PATH),
				uriBuilder -> uriBuilder
						.queryParam(OFFSET, searchCriteria.getOffset())
						.queryParam(LIMIT, searchCriteria.getLimit())
						.build(Map.of(POLICY_LEVEL, policyLevel.getValue().toLowerCase())))
				.retrieve()
				.bodyToMono(LimitingPolicies.class);
		return MonoUtils.blockOrThrow(mono, e -> new ThrottlingPolicyOperationException(searchCriteria, policyLevel, username, e));
	}

	public LimitingPolicy getLimitingPolicyByPublisher(String policyName, PolicyLevel policyLevel) throws ThrottlingPolicyOperationException {
		final Mono<LimitingPolicy> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(POLICY_GET_PATH),
				Map.of(POLICY_LEVEL, policyLevel.getValue().toLowerCase(), POLICY_NAME, policyName))
				.retrieve()
				.bodyToMono(LimitingPolicy.class);
		return MonoUtils.blockOrThrow(mono, e -> new ThrottlingPolicyOperationException(policyName, policyLevel, null, e));
	}

	public LimitingPolicy getLimitingPolicyByDev(String policyName, PolicyLevel policyLevel, String username) throws ThrottlingPolicyOperationException {
		final Mono<LimitingPolicy> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(POLICY_GET_PATH),
				Map.of(POLICY_LEVEL, policyLevel.getValue().toLowerCase(), POLICY_NAME, policyName))
				.retrieve()
				.bodyToMono(LimitingPolicy.class);
		return MonoUtils.blockOrThrow(mono, e -> new ThrottlingPolicyOperationException(policyName, policyLevel, username, e));
	}
}
