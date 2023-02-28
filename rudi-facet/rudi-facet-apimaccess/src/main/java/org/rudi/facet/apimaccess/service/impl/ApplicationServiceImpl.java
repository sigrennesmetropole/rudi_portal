package org.rudi.facet.apimaccess.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.application.ApplicationOperationAPI;
import org.rudi.facet.apimaccess.api.subscription.SubscriptionOperationAPI;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationInfo;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.bean.PublisherSubscriptionSearchCriteria;
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
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;
import org.wso2.carbon.apimgt.rest.api.devportal.SubscriptionList;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
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

	private final ApplicationOperationAPI applicationOperationAPI;
	private final APIsService apIsService;
	private final SubscriptionOperationAPI subscriptionOperationAPI;
	private final QueryBuilder queryBuilder;
	private final APIManagerProperties apiManagerProperties;

	/**
	 * Usernames dont on peut gérer les souscriptions car on connaît leurs mots-de-passe
	 */
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final List<String> handledSubscriptionUsernames = Arrays.asList(
			apiManagerProperties.getRudiUsername(),
			apiManagerProperties.getAnonymousUsername()
	);

	@Override
	public Application createApplication(Application application, String username) throws ApplicationOperationException {
		final var createdApplication = applicationOperationAPI.createApplication(application, username);
		applicationOperationAPI.generateApplicationKey(createdApplication.getApplicationId(), username, EndpointKeyType.PRODUCTION);
		applicationOperationAPI.generateApplicationKey(createdApplication.getApplicationId(), username, EndpointKeyType.SANDBOX);
		return createdApplication;
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
	public SubscriptionList searchApplicationAPISubscriptions(DevPortalSubscriptionSearchCriteria devPortalSubscriptionSearchCriteria, String username) throws SubscriptionOperationException {
		if (devPortalSubscriptionSearchCriteria == null) {
			throw new IllegalArgumentException("Les paramètres de recherche sont absents");
		}
		if (StringUtils.isEmpty(devPortalSubscriptionSearchCriteria.getApplicationId())
				&& StringUtils.isEmpty(devPortalSubscriptionSearchCriteria.getApiId())) {
			throw new IllegalArgumentException("Au moins un des paramètres apiId et applicationId est obligatoire");
		}
		if (devPortalSubscriptionSearchCriteria.getLimit() != null && devPortalSubscriptionSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn("Le nombre de souscriptions application - api dépasse le nombre d'élément maximum autorisé: {} > {}",
					devPortalSubscriptionSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			devPortalSubscriptionSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		return subscriptionOperationAPI.searchApplicationAPISubscriptions(devPortalSubscriptionSearchCriteria, username);
	}

	private List<org.wso2.carbon.apimgt.rest.api.publisher.Subscription> getAllSubscriptions(@Nonnull String apiId) {
		final var searchCriteria = new PublisherSubscriptionSearchCriteria().apiId(apiId);
		return subscriptionOperationAPI.searchAPISubscriptions(searchCriteria);
	}

	@Override
	public Subscription subscribeAPI(Subscription applicationAPISubscription, String username) throws APISubscriptionException {
		checkApplicationAPISubscription(applicationAPISubscription);
		return subscriptionOperationAPI.createApplicationAPISubscription(applicationAPISubscription, username);
	}

	@Override
	public Subscription subscribeAPIToDefaultUserApplication(String apiId, String username) throws APISubscriptionException, ApplicationOperationException {
		String applicationId = getDefaultApplicationId(username);
		val applicationAPISubscription = new Subscription()
				.applicationId(applicationId)
				.apiId(apiId)
				.throttlingPolicy(getThrottlingPolicy(username));
		return subscribeAPI(applicationAPISubscription, username);
	}

	@Override
	public void createDefaultSubscriptions(String apiId, boolean isRestricted) throws APISubscriptionException, ApplicationOperationException {

		final List<String> usernamesThatHasAlreadySubscribed = getAllSubscriptions(apiId).stream()
				.map(applicationAPISubscription -> {
					final var subscriber = applicationAPISubscription.getApplicationInfo().getSubscriber();
					return APIManagerProperties.Domains.removeDomainFromUsername(subscriber);
				})
				.collect(Collectors.toList());

		final var rudiUsername = apiManagerProperties.getRudiUsername();
		if (!usernamesThatHasAlreadySubscribed.contains(rudiUsername)) {
			subscribeAPIToDefaultUserApplication(apiId, rudiUsername);
		}

		final var anonymousUsername = apiManagerProperties.getAnonymousUsername();
		if (!usernamesThatHasAlreadySubscribed.contains(anonymousUsername) && !isRestricted) {
			subscribeAPIToDefaultUserApplication(apiId, anonymousUsername);
		}
	}

	@Override
	public void deleteAllSubscriptionsWithoutRetiringAPI(String apiId) throws SubscriptionOperationException {
		final var allSubscriptions = getAllSubscriptions(apiId);
		for (final org.wso2.carbon.apimgt.rest.api.publisher.Subscription applicationAPISubscription : allSubscriptions) {
			final var subscriber = applicationAPISubscription.getApplicationInfo().getSubscriber();
			final var username = APIManagerProperties.Domains.removeDomainFromUsername(subscriber);
			if (getHandledSubscriptionUsernames().contains(username)) {
				unsubscribeAPI(applicationAPISubscription.getSubscriptionId(), username);
			} else {
				final var causeMessage = String.format("Cannot delete subscriptions for usernames other than %s. Please change API status to RETIRED to automatically remove all subscriptions.", StringUtils.join(getHandledSubscriptionUsernames(), " or "));
				final var cause = new NotImplementedException(causeMessage);
				throw new SubscriptionOperationException(applicationAPISubscription.getSubscriptionId(), username, cause);
			}
		}
	}

	@Override
	public boolean hasSubscribeAPIToDefaultUserApplication(String apiId, String username) throws ApplicationOperationException, SubscriptionOperationException {
		String applicationId = getDefaultApplicationId(username);
		return hasSubscribeAPI(apiId, applicationId, username);
	}

	@Override
	public boolean hasSubscribeAPI(String apiId, String applicationId, String username) throws SubscriptionOperationException {
		SubscriptionList applicationAPISubscriptions = searchApplicationAPISubscriptions(
				new DevPortalSubscriptionSearchCriteria().apiId(apiId).applicationId(applicationId),
				username);
		return CollectionUtils.isNotEmpty(applicationAPISubscriptions.getList());
	}

	@Override
	public Subscription getSubscriptionAPI(String subscriptionId, String username) throws SubscriptionOperationException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à récupérer est absent");
		}
		return subscriptionOperationAPI.getApplicationAPISubscription(subscriptionId, username);
	}

	@Override
	public Subscription updateSubscriptionAPI(Subscription applicationAPISubscription, String username) throws SubscriptionOperationException {
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
		final var apiInfo = getApiInfo(globalId, mediaId);
		return applicationOperationAPI.getAPIContent(apiInfo.getContext(), apiInfo.getVersion(), getDefaultApplicationId(username), username);
	}

	@Override
	public void deleteApplication(String applicationId, String username) throws ApplicationOperationException {
		applicationOperationAPI.deleteApplication(applicationId, username);
	}

	private void checkApplicationAPISubscription(Subscription applicationAPISubscription) {
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
		return getOrCreateDefaultApplication(username, applicationIdProcessor);
	}

	private String getThrottlingPolicy(@Nonnull String username) {
		if (username.equals(apiManagerProperties.getAnonymousUsername())) {
			return anonymousSubscriptionPolicy;
		} else {
			return defaultSubscriptionPolicy;
		}
	}

	@Override
	public String buildAPIAccessUrl(UUID globalId, UUID mediaId) throws APIsOperationException, APINotFoundException {
		final var apiInfo = getApiInfo(globalId, mediaId);
		return applicationOperationAPI.buildAPIAccessUrl(apiInfo.getContext(), apiInfo.getVersion());
	}

	private APIInfo getApiInfo(UUID globalId, UUID mediaId) throws APIsOperationException, APINotFoundException {
		val apiList = apIsService.searchAPI(new APISearchCriteria()
				.globalId(globalId)
				.mediaUuid(mediaId));
		if (apiList.getCount() == 0) {
			throw new APINotFoundException(globalId, mediaId);
		}
		return apiList.getList().get(0);
	}

	@Override
	public ApplicationKey getApplicationKey(String applicationId, String username, EndpointKeyType keyType) throws ApplicationOperationException {
		return applicationOperationAPI.getApplicationKeyList(applicationId, username)
				.getList()
				.stream()
				.filter(key -> key.getKeyType() == keyType)
				.findFirst()
				.orElse(null);
	}

	@Override
	public Application getOrCreateDefaultApplication(String username) throws ApplicationOperationException {
		return getOrCreateDefaultApplication(username, applicationProcessor);
	}

	private <R> R getOrCreateDefaultApplication(String username, ApplicationProcessor<R> applicationProcessor) throws ApplicationOperationException {
		final var applications = searchApplication(new ApplicationSearchCriteria().name(defaultApplicationName), username);
		if (CollectionUtils.isEmpty(applications.getList())) {
			final var application = new Application().name(defaultApplicationName).throttlingPolicy(defaultApplicationRequestPolicy);
			final var createdApplication = createApplication(application, username);
			return applicationProcessor.processCreatedApplication(createdApplication);
		} else {
			final var applicationInfo = applications.getList().get(0);
			return applicationProcessor.processExistingApplicationInfo(applicationInfo, username);
		}
	}

	private interface ApplicationProcessor<R> {
		R processCreatedApplication(Application application);

		R processExistingApplicationInfo(ApplicationInfo applicationInfo, String username) throws ApplicationOperationException;
	}

	private final ApplicationProcessor<Application> applicationProcessor = new ApplicationProcessor<>() {
		@Override
		public Application processCreatedApplication(Application application) {
			return application;
		}

		@Override
		public Application processExistingApplicationInfo(ApplicationInfo applicationInfo, String username) throws ApplicationOperationException {
			return applicationOperationAPI.getApplication(applicationInfo.getApplicationId(), username);
		}
	};

	private final ApplicationProcessor<String> applicationIdProcessor = new ApplicationProcessor<>() {
		@Override
		public String processCreatedApplication(Application application) {
			return application.getApplicationId();
		}

		@Override
		public String processExistingApplicationInfo(ApplicationInfo applicationInfo, String username) {
			return applicationInfo.getApplicationId();
		}
	};

}
