package org.rudi.facet.apimaccess.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.rudi.facet.apimaccess.api.admin.AdminOperationAPI;
import org.rudi.facet.apimaccess.api.apis.APIsOperationAPI;
import org.rudi.facet.apimaccess.api.policy.ThrottlingPolicyOperationAPI;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.APIWorkflowResponse;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.bean.PolicyLevel;
import org.rudi.facet.apimaccess.bean.SearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.apimaccess.exception.APIsOperationException;
import org.rudi.facet.apimaccess.exception.APIsOperationWithIdException;
import org.rudi.facet.apimaccess.exception.AdminOperationException;
import org.rudi.facet.apimaccess.exception.ThrottlingPolicyOperationException;
import org.rudi.facet.apimaccess.exception.UpdateAPILifecycleStatusException;
import org.rudi.facet.apimaccess.helper.generator.OpenApiGenerator;
import org.rudi.facet.apimaccess.helper.search.QueryBuilder;
import org.rudi.facet.apimaccess.service.APIsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wso2.carbon.apimgt.rest.api.admin.APICategory;
import org.wso2.carbon.apimgt.rest.api.admin.APICategoryList;
import org.wso2.carbon.apimgt.rest.api.admin.Environment;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;
import org.wso2.carbon.apimgt.rest.api.publisher.APIRevision;
import org.wso2.carbon.apimgt.rest.api.publisher.APIRevisionDeployment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.DEFAULT_API_VERSION;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT_MAX_VALUE;
import static org.rudi.facet.apimaccess.helper.api.APIContextHelper.buildAPIContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIsServiceImpl implements APIsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(APIsServiceImpl.class);
	private static final String API_ID_NOT_GIVEN = "L'identifiant de l'API n'est pas renseigné";

	private final APIsOperationAPI apIsOperationAPI;
	private final AdminOperationAPI adminOperationAPI;
	private final ThrottlingPolicyOperationAPI throttlingPolicyOperationAPI;
	private final OpenApiGenerator openApiGenerator;
	private final QueryBuilder queryBuilder;
	private final APIDescriptionMapper apiDescriptionMapper;

	@Value("${apimanager.api.store.api.categories:RudiData}")
	private String[] apiCategories;
	private boolean apiCategoriesCreated = false;

	/**
	 * Création d'une API
	 *
	 * @param apiDescription paramètre de la nouvelle API
	 * @return API
	 * @throws APIManagerException Erreur lors de la création
	 */
	private API createAPI(APIDescription apiDescription) throws APIManagerException {
		if (apiDescription == null) {
			throw new IllegalArgumentException("Les paramètres de l'API à créer sont absents");
		}
		setAPIDefaultValues(apiDescription);

		final var api = buildAPIToSave(apiDescription);
		// création de l'api
		final var apiResult = apIsOperationAPI.createAPI(api);

		// mise à jour de la définition openapi de l'api
		apIsOperationAPI.updateAPIOpenapiDefinition(
				openApiGenerator.generate(apiDescription, buildAPIContext(apiDescription)), apiResult.getId());

		// création de la révision - par défaut la description peut rester vide
		final APIRevision apiRevision = apIsOperationAPI.createApiRevision(apiResult.getId(), Strings.EMPTY);

		// recherche des différentes gateways sur lesquelles déployer
		List<Environment> gateways = adminOperationAPI.getSelectedGatewayEnvironments();

		// Déploiement de la révision dans le publisher
		final APIRevisionDeployment[] apiRevisionDeploymentResult = apIsOperationAPI
				.deployApiRevisionInPublisher(apiResult.getId(), apiRevision.getId(), gateways);
		if (apiRevisionDeploymentResult == null || apiRevisionDeploymentResult.length < 1) {
			throw new APIManagerException(String.format(
					"Erreur lors du déploiement de la révision %s de l'api d'id %s dans le publisher, resultat %s",
					apiRevision.getId(), apiResult.getId(), apiRevisionDeploymentResult));
		}

		// publication de l'api
		updateAPILifecycleStatus(apiResult.getId(), APILifecycleStatusAction.PUBLISH);

		// Déploiement dans la gateway
		apIsOperationAPI.redeployApiInGateway(apiResult.getName(),
				apiResult.getVersion(), apiResult.getId());

		return apIsOperationAPI.getAPI(apiResult.getId());
	}

	@Override
	public API createOrUnarchiveAPI(APIDescription apiDescription) throws APIManagerException {
		APIList apiList = searchAPIByName(apiDescription);
		if (apiList != null && apiList.getCount() > 0) {
			String apiId = apiList.getList().get(0).getId();
			unarchiveAPI(apiId);
			return apIsOperationAPI.getAPI(apiId);
		} else {
			return createAPI(apiDescription);
		}
	}

	@Override
	public void updateAPIByName(APIDescription apiDescription) throws APIManagerException {
		val apiToUpdated = getAPIByName(apiDescription);

		buildAPIToSave(apiDescription, apiToUpdated);
		val apiResult = apIsOperationAPI.updateAPI(apiToUpdated);

		apIsOperationAPI.updateAPIOpenapiDefinition(
				openApiGenerator.generate(apiDescription, buildAPIContext(apiDescription)), apiResult.getId());
	}

	@Override
	public void createOrUpdateAPIByName(APIDescription apiDescription) throws APIManagerException {
		val apiList = searchAPIByName(apiDescription);
		if (apiList != null && apiList.getCount() > 0) {
			updateAPIByName(apiDescription);
		} else {
			createOrUnarchiveAPI(apiDescription);
		}
	}

	@Override
	public APIWorkflowResponse updateAPILifecycleStatus(String apiId, APILifecycleStatusAction apiLifecycleStatusAction)
			throws UpdateAPILifecycleStatusException {
		return apIsOperationAPI.updateAPILifecycleStatus(apiId, apiLifecycleStatusAction);
	}

	@Override
	public API archiveAPIByName(APIDescription apiDescription) throws UpdateAPILifecycleStatusException,
			APIsOperationException, APINotFoundException, APIsOperationWithIdException {
		final var api = getAPIByName(apiDescription);
		archiveAPI(api.getId());
		return api;
	}

	@Override
	public void archiveAPI(String apiId) throws UpdateAPILifecycleStatusException {
		updateAPILifecycleStatus(apiId, APILifecycleStatusAction.BLOCK);
	}

	@Override
	public void unarchiveAPI(String apiId) throws UpdateAPILifecycleStatusException {
		updateAPILifecycleStatus(apiId, APILifecycleStatusAction.RE_PUBLISH);
	}

	@Override
	public void deleteAPI(String apiId) throws APIsOperationWithIdException {
		apIsOperationAPI.deleteAPI(apiId);
	}

	private API getAPIByName(APIDescription apiDescription)
			throws APINotFoundException, APIsOperationException, APIsOperationWithIdException {
		APIList apis = searchAPIByName(apiDescription);
		if (apis == null || apis.getCount() == 0) {
			throw new APINotFoundException(apiDescription);
		}
		// il y a normalement qu'une seule api avec ce nom
		return getAPI(apis.getList().get(0).getId());
	}

	private APIList searchAPIByName(APIDescription apiDescription) throws APIsOperationException {
		if (apiDescription == null) {
			throw new IllegalArgumentException("Les paramètres de l'API à rechercher par nom sont absents");
		}
		setAPIDefaultValues(apiDescription);

		// recherche de l'api par son nom
		val apiSearchCriteria = new APISearchCriteria().globalId(apiDescription.getGlobalId())
				.providerUuid(apiDescription.getProviderUuid()).providerCode(apiDescription.getProviderCode())
				.name(apiDescription.getName());
		return searchAPI(apiSearchCriteria);
	}

	@Override
	public API getAPI(String apiId) throws APIsOperationWithIdException {
		if (StringUtils.isEmpty(apiId)) {
			throw new IllegalArgumentException(APIsServiceImpl.API_ID_NOT_GIVEN);
		}
		return apIsOperationAPI.getAPI(apiId);
	}

	@Override
	public APIList searchAPI(APISearchCriteria apiSearchCriteria) throws APIsOperationException {
		if (apiSearchCriteria == null) {
			apiSearchCriteria = new APISearchCriteria();
		}
		if (apiSearchCriteria.getLimit() != null && apiSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn("Le nombre d'API demandé dépasse le nombre d'élément maximum autorisé: {} > {}",
					apiSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			apiSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		apiSearchCriteria.setQuery(queryBuilder.buildFrom(apiSearchCriteria));
		return apIsOperationAPI.searchAPI(apiSearchCriteria);
	}

	@Override
	public List<LimitingPolicy> getAPISubscriptionPolicies(String apiId) throws APIsOperationWithIdException {
		if (StringUtils.isEmpty(apiId)) {
			throw new IllegalArgumentException(APIsServiceImpl.API_ID_NOT_GIVEN);
		}
		return apIsOperationAPI.getAPISubscriptionPolicies(apiId);
	}

	private void setAPIDefaultValues(APIDescription apiDescription) {
		if (StringUtils.isEmpty(apiDescription.getVersion())) {
			apiDescription.setVersion(DEFAULT_API_VERSION);
		}
	}

	API buildAPIToSave(APIDescription apiDescription)
			throws ThrottlingPolicyOperationException, AdminOperationException {
		// get actual subscription policies (pour le moment on récupère toutes les subscriptions policies)
		final var searchCriteria = new SearchCriteria().offset(0).limit(200);
		final var policyLevel = PolicyLevel.SUBSCRIPTION;
		final var limitingPolicies = throttlingPolicyOperationAPI.searchLimitingPoliciesByPublisher(searchCriteria,
				policyLevel);
		if (limitingPolicies == null || limitingPolicies.getCount() == 0) {
			throw new ThrottlingPolicyOperationException(searchCriteria, policyLevel);
		}

		checkApiCategories();

		return apiDescriptionMapper.map(apiDescription, limitingPolicies, apiCategories);
	}

	private void checkApiCategories() throws AdminOperationException {
		if (!apiCategoriesCreated) {
			final List<String> apiCategoriesNamesToCreate = List.of(apiCategories);
			final APICategoryList existingApiCategories = adminOperationAPI.getApiCategories();
			final List<String> existingCategoriesNames = existingApiCategories.getList().stream()
					.map(APICategory::getName).collect(Collectors.toList());

			for (final String apiCategoryNameToCreate : apiCategoriesNamesToCreate) {
				if (!existingCategoriesNames.contains(apiCategoryNameToCreate)) {
					adminOperationAPI.createApiCategory(new APICategory().name(apiCategoryNameToCreate));
				}
			}

			apiCategoriesCreated = true;
		}
	}

	private void buildAPIToSave(APIDescription apiDescription, API api) {

		apiDescriptionMapper.map(apiDescription, api);
	}

	@Override
	public boolean existsApi(UUID globalId, UUID mediaId) throws APIsOperationException {

		// On regarde s'il y'a une API via une requête de recherche
		final var apiList = searchAPI(new APISearchCriteria().globalId(globalId).mediaUuid(mediaId));

		// OK si taille retour pas 0
		return apiList != null && apiList.getCount() != 0;
	}

}
