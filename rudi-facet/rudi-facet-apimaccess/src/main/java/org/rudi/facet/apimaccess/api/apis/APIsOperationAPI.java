package org.rudi.facet.apimaccess.api.apis;

import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.ManagerAPIProperties;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.bean.API;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.OpenAPIVersion;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_WEBCLIENT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.ACTION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_DEFINITION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.API_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OPENAPI_VERSION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.QUERY;

@Component
public class APIsOperationAPI extends AbstractManagerAPI {

	private static final String API_PATH = "/apis";
	private static final String API_GET_PATH = API_PATH + "/{apiId}";
	private static final String API_GET_POLICY_PATH = API_GET_PATH + "/subscription-policies";
	private static final String API_UPDATE_SWAGGER_DEFINITION_PATH = API_GET_PATH + "/swagger";
	private static final String API_UPDATE_LIFECYCLE_STATUS_PATH = API_PATH + "/change-lifecycle";

	APIsOperationAPI(
			@Qualifier(API_MACCESS_WEBCLIENT) WebClient webClient,
			ManagerAPIProperties managerAPIProperties
	) {
		super(webClient, managerAPIProperties);
	}

	public List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIManagerException {
		//noinspection Convert2Diamond Provoque l'erreur : java: Compilation failed: internal java compiler error
		final Mono<List<LimitingPolicy>> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(API_GET_POLICY_PATH), Map.of(API_ID, apiId))
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<LimitingPolicy>>() {
				});
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public API getAPI(String apiId) throws APIManagerException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, apiId))
				.retrieve()
				.bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIManagerException {
		final Mono<APIList> mono = populateRequestWithAdminRegistrationId(HttpMethod.GET, buildPublisherURIPath(API_PATH),
				uriBuilder -> uriBuilder
						.queryParam(OFFSET, apiSearchCriteria.getOffset())
						.queryParam(LIMIT, apiSearchCriteria.getLimit())
						.queryParam(QUERY, apiSearchCriteria.getQuery()).build())
				.retrieve()
				.bodyToMono(APIList.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public API createAPI(API api) throws APIManagerException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST, buildPublisherURIPath(API_PATH),
				uriBuilder -> uriBuilder
						.queryParam(OPENAPI_VERSION, OpenAPIVersion.V3).build())
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(api), API.class)
				.retrieve()
				.bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public API updateAPI(API api) throws APIManagerException {
		final Mono<API> mono = populateRequestWithAdminRegistrationId(HttpMethod.PUT, buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, api.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(api), API.class)
				.retrieve()
				.bodyToMono(API.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public void updateAPIOpenapiDefinition(String apiDefinition, String apiId) throws APIManagerException {
		final Mono<String> mono = populateRequestWithAdminRegistrationId(HttpMethod.PUT, buildPublisherURIPath(API_UPDATE_SWAGGER_DEFINITION_PATH), Map.of(API_ID, apiId))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(API_DEFINITION, apiDefinition))
				.retrieve()
				.bodyToMono(String.class);
		MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction) throws APIManagerException {
		final Mono<APIWorkflowResponse> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST, buildPublisherURIPath(API_UPDATE_LIFECYCLE_STATUS_PATH),
				uriBuilder -> uriBuilder
						.queryParam(API_ID, apiId)
						.queryParam(ACTION, apiLifecycleStatusAction.getValue())
						.build())
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(APIWorkflowResponse.class);
		return MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}

	public void deleteAPI(String apiId) throws APIManagerException {
		final Mono<Void> mono = populateRequestWithAdminRegistrationId(HttpMethod.DELETE, buildPublisherURIPath(API_GET_PATH), Map.of(API_ID, apiId))
				.retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, APIManagerException.class);
	}
}
