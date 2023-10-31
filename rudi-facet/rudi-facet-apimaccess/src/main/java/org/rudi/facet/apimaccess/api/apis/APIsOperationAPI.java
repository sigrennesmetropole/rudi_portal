package org.rudi.facet.apimaccess.api.apis;

import static org.rudi.facet.apimaccess.constant.QueryParameterKey.ACTION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_DEFINITION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OPENAPI_VERSION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.QUERY;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.OpenAPIVersion;
import org.rudi.facet.apimaccess.exception.APIsOperationException;
import org.rudi.facet.apimaccess.exception.APIsOperationWithIdException;
import org.rudi.facet.apimaccess.exception.PublisherHttpExceptionFactory;
import org.rudi.facet.apimaccess.exception.UpdateAPILifecycleStatusException;
import org.rudi.facet.apimaccess.exception.UpdateAPIOpenapiDefinitionException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;

import reactor.core.publisher.Mono;

@Component
public class APIsOperationAPI extends AbstractManagerAPI {

	private static final String API_PATH = "/apis";
	private static final String API_GET_PATH = API_PATH + "/{apiId}";
	private static final String API_GET_POLICY_PATH = API_GET_PATH + "/subscription-policies";
	private static final String API_UPDATE_SWAGGER_DEFINITION_PATH = API_GET_PATH + "/swagger";
	private static final String API_UPDATE_LIFECYCLE_STATUS_PATH = API_PATH + "/change-lifecycle";

	APIsOperationAPI(PublisherHttpExceptionFactory publisherHttpExceptionFactory,
			WebClient.Builder apimWebClientBuilder, APIManagerProperties apiManagerProperties) {
		super(apimWebClientBuilder, publisherHttpExceptionFactory, apiManagerProperties);
	}

	public List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIsOperationWithIdException {
		// noinspection Convert2Diamond Provoque l'erreur : java: Compilation failed: internal java compiler error
		final Mono<List<LimitingPolicy>> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET,
				buildPublisherURIPath(API_GET_POLICY_PATH), Map.of(API_ID, apiId)).retrieve()
						.bodyToMono(new ParameterizedTypeReference<List<LimitingPolicy>>() {
						});
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	@Nonnull
	public API getAPI(String apiId) throws APIsOperationWithIdException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET,
				buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, apiId)).retrieve().bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	@Nonnull
	public API getAPIFromDevportal(String apiId, String username) throws APIsOperationWithIdException {
		final Mono<API> mono = populateRequestWithRegistrationId(HttpMethod.GET, username,
				buildDevPortalURIPath(API_GET_PATH), Map.of(API_ID, apiId)).retrieve().bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	public APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIsOperationException {
		final Mono<APIList> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET,
				buildPublisherURIPath(API_PATH),
				uriBuilder -> uriBuilder.queryParam(OFFSET, apiSearchCriteria.getOffset())
						.queryParam(LIMIT, apiSearchCriteria.getLimit()).queryParam(QUERY, apiSearchCriteria.getQuery())
						.build()).retrieve().bodyToMono(APIList.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationException(apiSearchCriteria, e));
	}

	public API createAPI(API api) throws APIsOperationException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST, buildPublisherURIPath(API_PATH),
				uriBuilder -> uriBuilder.queryParam(OPENAPI_VERSION, OpenAPIVersion.V3).build())
						.contentType(MediaType.APPLICATION_JSON).body(Mono.just(api), API.class).retrieve()
						.bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationException(api, e));
	}

	public API updateAPI(API api) throws APIsOperationException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.PUT,
				buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, api.getId()))
						.contentType(MediaType.APPLICATION_JSON).body(Mono.just(api), API.class).retrieve()
						.bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationException(api, e));
	}

	public void updateAPIOpenapiDefinition(String apiDefinition, String apiId)
			throws UpdateAPIOpenapiDefinitionException {
		final Mono<String> mono = populateRequestWithAdminRegistrationId(HttpMethod.PUT,
				buildPublisherURIPath(API_UPDATE_SWAGGER_DEFINITION_PATH), Map.of(API_ID, apiId))
						.contentType(MediaType.MULTIPART_FORM_DATA)
						.body(BodyInserters.fromMultipartData(API_DEFINITION, apiDefinition)).retrieve()
						.bodyToMono(String.class);
		MonoUtils.blockOrThrow(mono, e -> new UpdateAPIOpenapiDefinitionException(apiId, apiDefinition, e));
	}

	/**
	 * @see <a href=
	 *      "https://apim.docs.wso2.com/en/3.2.0/develop/product-apis/publisher-apis/publisher-v1/publisher-v1/#tag/API-Lifecycle/paths/~1apis~1change-lifecycle/post">Documentation
	 *      WSO2</a>
	 */
	public APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction)
			throws UpdateAPILifecycleStatusException {
		final Mono<APIWorkflowResponse> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST,
				buildPublisherURIPath(API_UPDATE_LIFECYCLE_STATUS_PATH),
				uriBuilder -> uriBuilder.queryParam(API_ID, apiId)
						.queryParam(ACTION, apiLifecycleStatusAction.getValue()).build())
								.contentType(MediaType.APPLICATION_JSON).retrieve()
								.bodyToMono(APIWorkflowResponse.class);
		return MonoUtils.blockOrThrow(mono,
				e -> new UpdateAPILifecycleStatusException(apiId, apiLifecycleStatusAction, e));
	}

	public void deleteAPI(String apiId) throws APIsOperationWithIdException {
		final Mono<Void> mono = populateRequestWithAdminRegistrationId(HttpMethod.DELETE,
				buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, apiId)).retrieve().bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}
}
