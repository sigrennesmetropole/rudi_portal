package org.rudi.facet.apimaccess.api.subscription;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.api.PageResultUtils;
import org.rudi.facet.apimaccess.api.PaginationUtils;
import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.PublisherSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.rudi.facet.apimaccess.exception.APISubscriptionException;
import org.rudi.facet.apimaccess.exception.SubscriptionOperationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;
import org.wso2.carbon.apimgt.rest.api.devportal.SubscriptionList;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.APPLICATION_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.SUBSCRIPTION_ID;

@Component
@SuppressWarnings({ "java:S1075" })
@Slf4j
public class SubscriptionOperationAPI extends AbstractManagerAPI {

	private static final String SUBSCRIPTION_PATH = "/subscriptions";
	private static final String SUBSCRIPTION_GET_PATH = SUBSCRIPTION_PATH + "/{subscriptionId}";

	SubscriptionOperationAPI(
			WebClient.Builder apimWebClientBuilder,
			APIManagerHttpExceptionFactory apiManagerHttpExceptionFactory,
			APIManagerProperties apiManagerProperties
	) {
		super(apimWebClientBuilder, apiManagerHttpExceptionFactory, apiManagerProperties);
	}

	public SubscriptionList searchApplicationAPISubscriptions(DevPortalSubscriptionSearchCriteria applicationAPISubscriptionSearchCriteria, String username) throws SubscriptionOperationException {
		final Mono<SubscriptionList> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(SUBSCRIPTION_PATH),
				uriBuilder -> uriBuilder
						.queryParam(API_ID, applicationAPISubscriptionSearchCriteria.getApiId())
						.queryParam(APPLICATION_ID, applicationAPISubscriptionSearchCriteria.getApplicationId())
						.queryParam(LIMIT, applicationAPISubscriptionSearchCriteria.getLimit())
						.queryParam(OFFSET, applicationAPISubscriptionSearchCriteria.getOffset()).build())
				.retrieve()
				.bodyToMono(SubscriptionList.class);
		return MonoUtils.blockOrThrow(mono, e -> new SubscriptionOperationException(applicationAPISubscriptionSearchCriteria, username, e));
	}

	public SubscriptionList searchUserSubscriptions(DevPortalSubscriptionSearchCriteria userSubscriptionSearchCriteria, String username) throws SubscriptionOperationException {
		final Mono<SubscriptionList> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(SUBSCRIPTION_PATH),
				uriBuilder -> uriBuilder
						.queryParam(APPLICATION_ID, userSubscriptionSearchCriteria.getApplicationId()).build())
				.retrieve()
				.bodyToMono(SubscriptionList.class);
		return MonoUtils.blockOrThrow(mono, e -> new SubscriptionOperationException(userSubscriptionSearchCriteria, username, e));
	}

	public List<org.wso2.carbon.apimgt.rest.api.publisher.Subscription> searchAPISubscriptions(PublisherSubscriptionSearchCriteria searchCriteria) {
		return PageResultUtils.fetchAllElementsUsing(
				buildAPISubscriptionsSearchPageFetcher(searchCriteria),
				org.wso2.carbon.apimgt.rest.api.publisher.SubscriptionList::getList,
				PaginationUtils::getNextPageOffset);
	}

	private Function<Integer, Mono<org.wso2.carbon.apimgt.rest.api.publisher.SubscriptionList>> buildAPISubscriptionsSearchPageFetcher(PublisherSubscriptionSearchCriteria searchCriteria) {
		return offset -> populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(SUBSCRIPTION_PATH),
				uriBuilder -> uriBuilder
						.queryParam(API_ID, searchCriteria.getApiId())
						.queryParam(LIMIT, searchCriteria.getLimit())
						.queryParam(OFFSET, offset).build())
				.retrieve()
				.bodyToMono(org.wso2.carbon.apimgt.rest.api.publisher.SubscriptionList.class);
	}

	public Subscription createApplicationAPISubscription(Subscription applicationAPISubscription, String username) throws APISubscriptionException {
		final Mono<Subscription> mono = populateRequestWithRegistrationId(HttpMethod.POST, username, buildDevPortalURIPath(SUBSCRIPTION_PATH))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationAPISubscription), Subscription.class)
				.retrieve()
				.bodyToMono(Subscription.class);
		return MonoUtils.blockOrThrow(mono, e -> new APISubscriptionException(applicationAPISubscription, username, e));
	}

	public Subscription updateApplicationAPISubscription(Subscription applicationAPISubscription, String username) throws SubscriptionOperationException {
		final Mono<Subscription> mono = populateRequestWithRegistrationId(HttpMethod.PUT, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH),
				Map.of(SUBSCRIPTION_ID, applicationAPISubscription.getSubscriptionId()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationAPISubscription), Subscription.class)
				.retrieve()
				.bodyToMono(Subscription.class);
		return MonoUtils.blockOrThrow(mono, e -> new SubscriptionOperationException(applicationAPISubscription, username, e));
	}

	public void deleteApplicationAPISubscription(String subscriptionId, String username) throws SubscriptionOperationException {
		final Mono<Void> mono = populateRequestWithRegistrationId(HttpMethod.DELETE, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH), Map.of(SUBSCRIPTION_ID, subscriptionId))
				.retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, e -> new SubscriptionOperationException(subscriptionId, username, e));
	}

	public Subscription getApplicationAPISubscription(String subscriptionId, String username) throws SubscriptionOperationException {
		final Mono<Subscription> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(SUBSCRIPTION_GET_PATH), Map.of(SUBSCRIPTION_ID, subscriptionId))
				.retrieve()
				.bodyToMono(Subscription.class);
		return MonoUtils.blockOrThrow(mono, e -> new SubscriptionOperationException(subscriptionId, username, e));
	}
}
