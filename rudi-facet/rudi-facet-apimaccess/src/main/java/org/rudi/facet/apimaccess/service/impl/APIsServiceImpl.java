package org.rudi.facet.apimaccess.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.api.apis.APIsOperationAPI;
import org.rudi.facet.apimaccess.api.policy.ThrottlingPolicyOperationAPI;
import org.rudi.facet.apimaccess.bean.API;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APIEndpointConfig;
import org.rudi.facet.apimaccess.bean.APIEndpointConfigHttp;
import org.rudi.facet.apimaccess.bean.APIEndpointType;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.PolicyLevel;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.generator.OpenApiGenerator;
import org.rudi.facet.apimaccess.helper.search.SearchCriteriaMapper;
import org.rudi.facet.apimaccess.service.APIsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.EXTENSION;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.GLOBAL_ID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.INTERFACE_CONTRACT;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.MEDIA_UUID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_CODE;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_UUID;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_SEARCH_MAPPER;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.DEFAULT_API_VERSION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT_MAX_VALUE;

@Service
public class APIsServiceImpl implements APIsService {

	private static final String CONTEXT_SEPARATOR = "/";
	private static final Logger LOGGER = LoggerFactory.getLogger(APIsServiceImpl.class);

	@Autowired
	private APIsOperationAPI apIsOperationAPI;

	@Autowired
	private ThrottlingPolicyOperationAPI throttlingPolicyOperationAPI;

	@Autowired
	private OpenApiGenerator openApiGenerator;

	@Autowired
	@Qualifier(value = API_MACCESS_SEARCH_MAPPER)
	private SearchCriteriaMapper searchCriteriaMapper;

	@Value("${apimanager.api.store.api.categories:RudiData}")
	private String[] apiCategories;

	@Override
	public API createAPI(APIDescription apiDescription) throws APIManagerException {
		if (apiDescription == null) {
			throw new APIManagerException("Les paramètres de l'API à créer sont absents");
		}
		setAPIDefaultValues(apiDescription);

		API api = buildAPIToSave(apiDescription);
		// création de l'api
		API apiResult = apIsOperationAPI.createAPI(api);

		// mise à jour de la définition openapi de l'api
		apIsOperationAPI.updateAPIOpenapiDefinition(openApiGenerator.generate(apiDescription, buildAPIContext(apiDescription)), apiResult.getId());
		// publication de l'api
		updateAPILifecycleStatus(apiResult.getId(), APILifecycleStatusAction.PUBLISH);

		return apIsOperationAPI.getAPI(apiResult.getId());
	}

	@Override
	public API updateAPI(APIDescription apiDescription, String apiId) throws APIManagerException {
		if (apiDescription == null) {
			throw new APIManagerException("Les paramètres de l'API à modifier sont absents");
		}
		setAPIDefaultValues(apiDescription);

		API apiToUpdated = getAPI(apiId);
		API apiResult = apIsOperationAPI.updateAPI(buildAPIToSave(apiDescription, apiToUpdated));

		apIsOperationAPI.updateAPIOpenapiDefinition(openApiGenerator.generate(apiDescription, buildAPIContext(apiDescription)), apiResult.getId());

		return apIsOperationAPI.getAPI(apiResult.getId());
	}

	@Override
	public API updateAPIByName(APIDescription apiDescription) throws APIManagerException {
		if (apiDescription == null) {
			throw new APIManagerException("Les paramètres de l'API à modifier par le nom sont absents");
		}
		setAPIDefaultValues(apiDescription);

		// recherche de l'api par son nom
		APISearchCriteria apiSearchCriteria = new APISearchCriteria()
				.globalId(apiDescription.getGlobalId())
				.providerUuid(apiDescription.getProviderUuid())
				.providerCode(apiDescription.getProviderCode())
				.name(apiDescription.getName());
		APIList apis = searchAPI(apiSearchCriteria);
		if (apis.getCount() == 0) {
			throw new APIManagerException("Aucune API ne possède le nom " + apiDescription.getName());
		}
		// il y a normalement qu'une seule api avec ce nom
		API apiToUpdated = getAPI(apis.getList().get(0).getId());

		apiToUpdated = buildAPIToSave(apiDescription, apiToUpdated);
		API apiResult = apIsOperationAPI.updateAPI(apiToUpdated);

		apIsOperationAPI.updateAPIOpenapiDefinition(openApiGenerator.generate(apiDescription, buildAPIContext(apiDescription)), apiResult.getId());

		return apIsOperationAPI.getAPI(apiResult.getId());
	}

	@Override
	public APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction) throws APIManagerException {
		return apIsOperationAPI.updateAPILifecycleStatus(apiId, apiLifecycleStatusAction);
	}

	@Override
	public void deleteAPI(String apiId) throws APIManagerException {
		apIsOperationAPI.deleteAPI(apiId);
	}

	@Override
	public API getAPI(String apiId) throws APIManagerException {
		if (StringUtils.isEmpty(apiId)) {
			throw new APIManagerException("L'identifiant de l'API n'est pas renseigné");
		}
		return apIsOperationAPI.getAPI(apiId);
	}

	@Override
	public APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIManagerException {
		if (apiSearchCriteria == null) {
			apiSearchCriteria = new APISearchCriteria();
		}
		if (apiSearchCriteria.getLimit() != null && apiSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn("Le nombre d'API demandé dépasse le nombre d'élément maximum autorisé: {} > {}", apiSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			apiSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		return apIsOperationAPI.searchAPI(searchCriteriaMapper.buildAPISearchCriteriaQuery(apiSearchCriteria));
	}

	@Override
	public List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIManagerException {
		if (StringUtils.isEmpty(apiId)) {
			throw new APIManagerException("L'identifiant de l'API n'est pas renseigné");
		}
		return apIsOperationAPI.getAPISubscriptionPolicies(apiId);
	}

	private void setAPIDefaultValues(APIDescription apiDescription) {
		if (StringUtils.isEmpty(apiDescription.getVersion())) {
			apiDescription.setVersion(DEFAULT_API_VERSION);
		}
	}

	private API buildAPIToSave(APIDescription apiDescription) throws APIManagerException {
		// get actual subscription policies (pour le moment on récupère toutes les subscriptions policies)
		LimitingPolicies limitingPolicies = throttlingPolicyOperationAPI.searchLimitingPoliciesByPublisher(new SearchCriteria().offset(0), PolicyLevel.SUBSCRIPTION);
		if (limitingPolicies == null || limitingPolicies.getCount() == 0) {
			throw new APIManagerException("Aucune politique d'abonnement pour des APIs n'a été trouvée");
		}

		MediaType mediaType;
		try {
			mediaType = MediaType.parseMediaType(apiDescription.getMediaType());
		} catch (InvalidMediaTypeException e) {
			throw new APIManagerException("Le media type est invalide : " + apiDescription.getMediaType(), e);
		}

		return new API()
				.name(apiDescription.getName())
				.endpointConfig(new APIEndpointConfigHttp()
						.endpointType(APIEndpointType.HTTP)
						.productionEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl()))
						.sandboxEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl())))
				.policies(limitingPolicies.getList().stream().map(LimitingPolicy::getName).collect(Collectors.toList()))
				.categories(Arrays.asList(apiCategories))
				.context(buildAPIContext(apiDescription))
				.isDefaultVersion(true)
				.transport(Arrays.asList(API.TransportEnum.HTTP, API.TransportEnum.HTTPS))
				.gatewayEnvironments(Collections.singletonList(API.GatewayEnvironmentsEnum.PRODUCTION_AND_SANDBOX))
				.version(StringUtils.isNotEmpty(apiDescription.getVersion()) ? apiDescription.getVersion() : DEFAULT_API_VERSION)
				.additionalProperties(Map.of(
						PROVIDER_UUID, apiDescription.getProviderUuid().toString(),
						PROVIDER_CODE, apiDescription.getProviderCode(),
						GLOBAL_ID, apiDescription.getGlobalId().toString(),
						MEDIA_UUID, apiDescription.getMediaUuid().toString(),
						INTERFACE_CONTRACT, apiDescription.getInterfaceContract(),
						EXTENSION, mediaType.getSubtype())
				);
	}

	private API buildAPIToSave(APIDescription apiDescription, API api) throws APIManagerException {

		MediaType mediaType;
		try {
			mediaType = MediaType.parseMediaType(apiDescription.getMediaType());
		} catch (InvalidMediaTypeException e) {
			throw new APIManagerException("Le media type est invalide : " + apiDescription.getMediaType(), e);
		}

		api = api
				.name(apiDescription.getName())
				.endpointConfig(new APIEndpointConfigHttp()
						.endpointType(APIEndpointType.HTTP)
						.productionEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl()))
						.sandboxEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl())))
				.context(buildAPIContext(apiDescription))
				.additionalProperties(Map.of(
						PROVIDER_UUID, apiDescription.getProviderUuid().toString(),
						PROVIDER_CODE, apiDescription.getProviderCode(),
						GLOBAL_ID, apiDescription.getGlobalId().toString(),
						MEDIA_UUID, apiDescription.getMediaUuid().toString(),
						INTERFACE_CONTRACT, apiDescription.getInterfaceContract(),
						EXTENSION, mediaType.getSubtype())
				);

		return api;
	}

	private String buildAPIContext(APIDescription apiDescription) {
		return CONTEXT_SEPARATOR + "datasets" + CONTEXT_SEPARATOR + apiDescription.getMediaUuid() + CONTEXT_SEPARATOR + apiDescription.getInterfaceContract();
	}

	public static String getInterfaceContractFromContext(String context) {
		return StringUtils.substringAfterLast(context, CONTEXT_SEPARATOR);
	}

}
