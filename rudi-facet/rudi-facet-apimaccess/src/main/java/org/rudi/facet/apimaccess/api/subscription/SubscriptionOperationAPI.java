package org.rudi.facet.apimaccess.api.subscription;

import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.ManagerAPIProperties;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptions;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_WEBCLIENT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.APPLICATION_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.SUBSCRIPTION_ID;

@Component
@SuppressWarnings({ "java:S1075" })
public class SubscriptionOperationAPI extends AbstractManagerAPI {

	private static final String SUBSCRIPTION_PATH = "/subscriptions";
	private static final String SUBSCRIPTION_GET_PATH = SUBSCRIPTION_PATH + "/{subscriptionId}";

	SubscriptionOperationAPI(
			@Qualifier(API_MACCESS_WEBCLIENT) WebClient webClient,
			ManagerAPIProperties managerAPIProperties
	) {
		super(webClient, managerAPIProperties);
	}

	public ApplicationAPISubscriptions searchApplicationAPISubscriptions(ApplicationAPISubscriptionSearchCriteria applicationAPISubscriptionSearchCriteria, String username) throws APIManagerException {
		final Mono<ApplicationAPISubscriptions> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(SUBSCRIPTION_PATH),
				uriBuilder -> uriBuilder
						.queryParam(API_ID, applicationAPISubscriptionSearchCriteria.getApiId())
						.queryParam(APPLICATION_ID, applicationAPISubscriptionSearchCriteria.getApplicationId())
						.queryParam(LIMIT, applicationAPISubscriptionSearchCriteria.getLimit())
						.queryParam(OFFSET, applicationAPISubscriptionSearchCriteria.getOffset()).build())
				.retrieve()
				.bodyToMono(ApplicationAPISubscriptions.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public ApplicationAPISubscription createApplicationAPISubscription(ApplicationAPISubscription applicationAPISubscription, String username) throws APIManagerException {
		final Mono<ApplicationAPISubscription> mono = populateRequestWithRegistrationId(HttpMethod.POST, username, buildDevPortalURIPath(SUBSCRIPTION_PATH))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationAPISubscription), ApplicationAPISubscription.class)
				.retrieve()
				.bodyToMono(ApplicationAPISubscription.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public ApplicationAPISubscription updateApplicationAPISubscription(ApplicationAPISubscription applicationAPISubscription, String username) throws APIManagerException {
		final Mono<ApplicationAPISubscription> mono = populateRequestWithRegistrationId(HttpMethod.PUT, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH),
				Map.of(SUBSCRIPTION_ID, applicationAPISubscription.getSubscriptionId()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationAPISubscription), ApplicationAPISubscription.class)
				.retrieve()
				.bodyToMono(ApplicationAPISubscription.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public void deleteApplicationAPISubscription(String subscriptionId, String username) throws APIManagerException {
		final Mono<Void> mono = populateRequestWithRegistrationId(HttpMethod.DELETE, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH), Map.of(SUBSCRIPTION_ID, subscriptionId))
				.retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public ApplicationAPISubscription getApplicationAPISubscription(String subscriptionId, String username) throws APIManagerException {
		final Mono<ApplicationAPISubscription> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH), Map.of(SUBSCRIPTION_ID, subscriptionId))
				.retrieve()
				.bodyToMono(ApplicationAPISubscription.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

}
