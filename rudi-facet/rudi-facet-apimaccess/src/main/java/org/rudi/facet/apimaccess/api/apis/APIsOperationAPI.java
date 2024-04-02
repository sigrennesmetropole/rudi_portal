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

import org.apache.commons.collections4.CollectionUtils;
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
import org.wso2.carbon.apimgt.rest.api.admin.Environment;
import org.wso2.carbon.apimgt.rest.api.gateway.APIArtifact;
import org.wso2.carbon.apimgt.rest.api.gateway.DeployResponse;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;
import org.wso2.carbon.apimgt.rest.api.publisher.APIRevision;
import org.wso2.carbon.apimgt.rest.api.publisher.APIRevisionDeployment;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class APIsOperationAPI extends AbstractManagerAPI {

	private static final String API_PATH = "/apis";
	private static final String API_GET_PATH = API_PATH + "/{apiId}";
	private static final String API_GET_POLICY_PATH = API_GET_PATH + "/subscription-policies";
	private static final String API_UPDATE_SWAGGER_DEFINITION_PATH = API_GET_PATH + "/swagger";
	private static final String API_UPDATE_LIFECYCLE_STATUS_PATH = API_PATH + "/change-lifecycle";
	private static final String API_REVISIONS_PATH = API_GET_PATH + "/revisions";
	private static final String API_PUBLISHER_DEPLOYMENT_PATH = API_GET_PATH + "/deploy-revision";
	private static final String API_GATEWAY_DEPLOYMENT_PATH = "/redeploy-api";
	private static final String API_GATEWAY_ARTIFACT_PATH = "/api-artifact";

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
	public org.wso2.carbon.apimgt.rest.api.devportal.API getAPIFromDevportal(String apiId, String username)
			throws APIsOperationWithIdException {
		final Mono<org.wso2.carbon.apimgt.rest.api.devportal.API> mono = populateRequestWithRegistrationId(
				HttpMethod.GET, username, buildDevPortalURIPath(API_GET_PATH), Map.of(API_ID, apiId)).retrieve()
						.bodyToMono(org.wso2.carbon.apimgt.rest.api.devportal.API.class);
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
	 *      "https://apim.docs.wso2.com/en/latest/reference/product-apis/publisher-apis/publisher-v4/publisher-v4/#tag/API-Lifecycle/operation/changeAPILifecycle">Documentation
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

	public APIRevision createApiRevision(String apiId, String description) throws APIsOperationWithIdException {

		final Mono<APIRevision> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST,
				buildPublisherURIPath(API_REVISIONS_PATH), Map.of(API_ID, apiId))
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just(new APIRevision().description(description)), APIRevision.class).retrieve()
						.bodyToMono(APIRevision.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	public APIRevisionDeployment[] deployApiRevisionInPublisher(String apiId, String apiRevisionId,
			List<Environment> gateways) throws APIsOperationWithIdException {

		// récupération de tous les environnements/vhosts où déployer la révision
		APIRevisionDeployment[] apiRevisionDeployments = CollectionUtils
				.emptyIfNull(gateways).stream().flatMap(gateway -> CollectionUtils.emptyIfNull(gateway.getVhosts())
						.stream().map(vhost -> new APIRevisionDeployment().name(gateway.getName())
								.vhost(vhost.getHost()).displayOnDevportal(true)))
				.toArray(APIRevisionDeployment[]::new);

		final Mono<APIRevisionDeployment[]> mono = populateRequestWithAdminRegistrationId(HttpMethod.POST,
				buildPublisherURIPath(API_PUBLISHER_DEPLOYMENT_PATH),
				uriBuilder -> uriBuilder.queryParam("revisionId", apiRevisionId).build(apiId))
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just(apiRevisionDeployments), APIRevisionDeployment[].class).retrieve()
						.bodyToMono(APIRevisionDeployment[].class);

		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	public DeployResponse redeployApiInGateway(String apiName, String version, String apiId)
			throws APIsOperationWithIdException {
		WebClient.RequestBodySpec req = populateRequestWithAdminRegistrationId(HttpMethod.POST,
				buildGatewayURIPath(API_GATEWAY_DEPLOYMENT_PATH),
				uriBuilder -> uriBuilder.queryParam("apiName", apiName).queryParam("version", version).build());
		final Mono<DeployResponse> mono = req.retrieve().bodyToMono(DeployResponse.class);

		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}

	public APIArtifact getApiInGateway(String apiName, String version, String apiId)
			throws APIsOperationWithIdException {
		WebClient.RequestBodySpec req = populateRequestWithAdminRegistrationId(HttpMethod.GET,
				buildGatewayURIPath(API_GATEWAY_ARTIFACT_PATH),
				uriBuilder -> uriBuilder.queryParam("apiName", apiName).queryParam("version", version).build());
		final Mono<APIArtifact> mono = req.retrieve().bodyToMono(APIArtifact.class);
		return MonoUtils.blockOrThrow(mono, e -> new APIsOperationWithIdException(apiId, e));
	}
}
