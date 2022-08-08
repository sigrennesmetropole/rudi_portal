package org.rudi.facet.apimaccess.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.api.application.ApplicationOperationAPI;
import org.rudi.facet.apimaccess.api.subscription.SubscriptionOperationAPI;
import org.rudi.facet.apimaccess.bean.APIInfo;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptions;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.apimaccess.exception.APISubscriptionException;
import org.rudi.facet.apimaccess.exception.APIsOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.SubscriptionOperationException;
import org.rudi.facet.apimaccess.helper.search.QueryBuilder;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;

import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT_MAX_VALUE;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

	@Value("${apimanager.api.store.application.default.name}")
	private String defaultApplicationName;

	@Value("${apimanager.api.store.application.default.requestPolicy}")
	private String defaultApplicationRequestPolicy;

	@Value("${apimanager.api.store.subscription.default.policy}")
	private String defaultSubscriptionPolicy;

	@Value("${apimanager.api.store.subscription.anonymous.policy}")
	private String anonymousSubscriptionPolicy;

	@Value("${apimanager.oauth2.client.anonymous.username:anonymous}")
	private String anonymousUsername;

	private final ApplicationOperationAPI applicationOperationAPI;
	private final APIsService apIsService;
	private final SubscriptionOperationAPI subscriptionOperationAPI;
	private final QueryBuilder queryBuilder;

	@Override
	public Application createApplication(Application application, String username) throws ApplicationOperationException {
		return applicationOperationAPI.createApplication(application, username);
	}

	@Override
	public Application getApplication(String applicationId, String username) throws ApplicationOperationException {
		return applicationOperationAPI.getApplication(applicationId, username);
	}

	@Override
	public Applications searchApplication(ApplicationSearchCriteria applicationSearchCriteria, String username) throws ApplicationOperationException {
		if (applicationSearchCriteria == null) {
			applicationSearchCriteria = new ApplicationSearchCriteria();
		}
		if (applicationSearchCriteria.getLimit() != null && applicationSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn("Le nombre d'applications demandé dépasse le nombre d'élément maximum autorisé: {} > {}",
					applicationSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			applicationSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		applicationSearchCriteria.setQuery(queryBuilder.buildFrom(applicationSearchCriteria));
		return applicationOperationAPI.searchApplication(applicationSearchCriteria, username);
	}

	@Override
	public ApplicationAPISubscriptions searchApplicationAPISubscriptions(ApplicationAPISubscriptionSearchCriteria applicationAPISubscriptionSearchCriteria, String username) throws SubscriptionOperationException {
		if (applicationAPISubscriptionSearchCriteria == null) {
			throw new IllegalArgumentException("Les paramètres de recherche sont absents");
		}
		if (StringUtils.isEmpty(applicationAPISubscriptionSearchCriteria.getApplicationId())
				&& StringUtils.isEmpty(applicationAPISubscriptionSearchCriteria.getApiId())) {
			throw new IllegalArgumentException("Au moins un des paramètres apiId et applicationId est obligatoire");
		}
		if (applicationAPISubscriptionSearchCriteria.getLimit() != null && applicationAPISubscriptionSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn("Le nombre de souscriptions application - api dépasse le nombre d'élément maximum autorisé: {} > {}",
					applicationAPISubscriptionSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			applicationAPISubscriptionSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		return subscriptionOperationAPI.searchApplicationAPISubscriptions(applicationAPISubscriptionSearchCriteria, username);
	}

	@Override
	public ApplicationAPISubscription subscribeAPI(ApplicationAPISubscription applicationAPISubscription, String username) throws APISubscriptionException {
		checkApplicationAPISubscription(applicationAPISubscription);
		return subscriptionOperationAPI.createApplicationAPISubscription(applicationAPISubscription, username);
	}

	@Override
	public ApplicationAPISubscription subscribeAPIToDefaultUserApplication(String apiId, String username) throws APISubscriptionException, ApplicationOperationException {
		String applicationId = getDefaultApplicationId(username);
		ApplicationAPISubscription applicationAPISubscription = new ApplicationAPISubscription()
				.applicationId(applicationId)
				.apiId(apiId)
				.throttlingPolicy(getThrottlingPolicy(username));
		return subscribeAPI(applicationAPISubscription, username);
	}

	@Override
	public void unsubscribeAPIToDefaultUserApplication(String apiId, String username) throws SubscriptionOperationException, ApplicationOperationException {
		val applicationId = getDefaultApplicationId(username);
		val searchCriteria = new ApplicationAPISubscriptionSearchCriteria()
				.apiId(apiId)
				.applicationId(applicationId);
		val applicationAPISubscriptions = searchApplicationAPISubscriptions(searchCriteria, username);
		for (final ApplicationAPISubscription applicationAPISubscription : applicationAPISubscriptions.getList()) {
			unsubscribeAPI(applicationAPISubscription.getSubscriptionId(), username);
		}
	}

	@Override
	public boolean hasSubscribeAPIToDefaultUserApplication(String apiId, String username) throws ApplicationOperationException, SubscriptionOperationException {
		String applicationId = getDefaultApplicationId(username);
		return hasSubscribeAPI(apiId, applicationId, username);
	}

	@Override
	public boolean hasSubscribeAPI(String apiId, String applicationId, String username) throws SubscriptionOperationException {
		ApplicationAPISubscriptions applicationAPISubscriptions = searchApplicationAPISubscriptions(
				new ApplicationAPISubscriptionSearchCriteria().apiId(apiId).applicationId(applicationId),
				username);
		return CollectionUtils.isNotEmpty(applicationAPISubscriptions.getList());
	}

	@Override
	public ApplicationAPISubscription getSubscriptionAPI(String subscriptionId, String username) throws SubscriptionOperationException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à récupérer est absent");
		}
		return subscriptionOperationAPI.getApplicationAPISubscription(subscriptionId, username);
	}

	@Override
	public ApplicationAPISubscription updateSubscriptionAPI(ApplicationAPISubscription applicationAPISubscription, String username) throws SubscriptionOperationException {
		checkApplicationAPISubscription(applicationAPISubscription);
		if (StringUtils.isEmpty(applicationAPISubscription.getSubscriptionId())) {
			throw new IllegalArgumentException("L'identifiant de la souscription à mettre à jour est absent");
		}
		return subscriptionOperationAPI.updateApplicationAPISubscription(applicationAPISubscription, username);
	}

	@Override
	public void unsubscribeAPI(String subscriptionId, String username) throws SubscriptionOperationException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à supprimer est absent");
		}
		subscriptionOperationAPI.deleteApplicationAPISubscription(subscriptionId, username);
	}

	@Override
	public DocumentContent downloadAPIContent(UUID globalId, UUID mediaId, String username) throws APIManagerException, IOException {
		final APIInfo apiInfo = getApiInfo(globalId, mediaId);
		return applicationOperationAPI.getAPIContent(apiInfo.getContext(), apiInfo.getVersion(), getDefaultApplicationId(username), username);
	}

	@Override
	public void deleteApplication(String applicationId, String username) throws ApplicationOperationException {
		applicationOperationAPI.deleteApplication(applicationId, username);
	}

	private void checkApplicationAPISubscription(ApplicationAPISubscription applicationAPISubscription)  {
		if (applicationAPISubscription == null) {
			throw new IllegalArgumentException("Les paramètres de souscription sont absents");
		}
		if (StringUtils.isEmpty(applicationAPISubscription.getApplicationId())) {
			throw new IllegalArgumentException("L'identifiant de l'application qui souscrit est absent");
		}
		if (StringUtils.isEmpty(applicationAPISubscription.getApiId())) {
			throw new IllegalArgumentException("L'identifiant de l'API de souscription est absent");
		}
		if (StringUtils.isEmpty(applicationAPISubscription.getThrottlingPolicy())) {
			throw new IllegalArgumentException("La politique de souscription est absente");
		}
	}

	/**
	 * Récupération de l'identifiant de l'application par défaut de l'utilisateur connecté
	 *
	 * @return String
	 */
	private String getDefaultApplicationId(String username) throws ApplicationOperationException {

		Applications applications = searchApplication(new ApplicationSearchCriteria().name(defaultApplicationName), username);
		if (CollectionUtils.isEmpty(applications.getList())) {
			// création de l'application par défaut pour l'admin
			Application application = new Application().name(defaultApplicationName).throttlingPolicy(defaultApplicationRequestPolicy);
			String applicationId = applicationOperationAPI.createApplication(application, username).getApplicationId();
			applicationOperationAPI.generateKeysApplication(applicationId, username);
			return applicationId;
		} else {
			// récupération de l'application par défaut
			return applications.getList().get(0).getApplicationId();
		}
	}

	private String getThrottlingPolicy(@Nonnull String username) {
		if (username.equals(anonymousUsername)) {
			return anonymousSubscriptionPolicy;
		} else {
			return defaultSubscriptionPolicy;
		}
	}

	@Override
	public String buildAPIAccessUrl(UUID globalId, UUID mediaId) throws APIsOperationException, APINotFoundException {
		final APIInfo apiInfo = getApiInfo(globalId, mediaId);
		return applicationOperationAPI.buildAPIAccessUrl(apiInfo.getContext(), apiInfo.getVersion());
	}

	@Override
	public boolean hasApi(UUID globalId, UUID mediaId) throws APIsOperationException {

		// On regarde s'il y'a une API via une requête de recherche
		final APIList apiList = apIsService.searchAPI(new APISearchCriteria()
				.globalId(globalId)
				.mediaUuid(mediaId));

		// OK si taille retour pas 0
		return apiList.getCount() != 0;
	}

	private APIInfo getApiInfo(UUID globalId, UUID mediaId) throws APIsOperationException, APINotFoundException {
		final APIList apiList = apIsService.searchAPI(new APISearchCriteria()
				.globalId(globalId)
				.mediaUuid(mediaId));
		if (apiList.getCount() == 0) {
			throw new APINotFoundException(globalId, mediaId);
		}
		return apiList.getList().get(0);
	}

}
