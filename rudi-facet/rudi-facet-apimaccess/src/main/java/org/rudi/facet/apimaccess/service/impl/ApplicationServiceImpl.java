package org.rudi.facet.apimaccess.service.impl;

import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT_MAX_VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.application.ApplicationOperationAPI;
import org.rudi.facet.apimaccess.api.subscription.SubscriptionOperationAPI;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusState;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationInfo;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.bean.HasSubscriptionStatus;
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
import org.springframework.util.MultiValueMap;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription.StatusEnum;
import org.wso2.carbon.apimgt.rest.api.devportal.SubscriptionList;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;
import org.wso2.carbon.apimgt.rest.api.publisher.Subscription.SubscriptionStatusEnum;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

	private static final List<StatusEnum> BLOCKED_DEVPORTAL_STATUS = List.of(StatusEnum.BLOCKED,
			StatusEnum.PROD_ONLY_BLOCKED);
	private static final List<SubscriptionStatusEnum> BLOCKED_PUBLISHER_STATUS = List.of(SubscriptionStatusEnum.BLOCKED,
			SubscriptionStatusEnum.PROD_ONLY_BLOCKED);

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
	private static final String DATASET_UUID_FIELD = "global_id";
	/**
	 * Usernames dont on peut gérer les souscriptions car on connaît leurs mots-de-passe
	 */
	@Getter(value = AccessLevel.PRIVATE, lazy = true)
	private final List<String> handledSubscriptionUsernames = Arrays.asList(apiManagerProperties.getRudiUsername(),
			apiManagerProperties.getAnonymousUsername());

	@Override
	public Application createApplication(Application application, String username)
			throws ApplicationOperationException {
		final var createdApplication = applicationOperationAPI.createApplication(application, username);
		applicationOperationAPI.generateApplicationKey(createdApplication.getApplicationId(), username,
				EndpointKeyType.PRODUCTION);
		applicationOperationAPI.generateApplicationKey(createdApplication.getApplicationId(), username,
				EndpointKeyType.SANDBOX);
		return createdApplication;
	}

	@Override
	public Application getApplication(String applicationId, String username) throws ApplicationOperationException {
		return applicationOperationAPI.getApplication(applicationId, username);
	}

	@Override
	public Applications searchApplication(ApplicationSearchCriteria applicationSearchCriteria, String username)
			throws ApplicationOperationException {
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
	public SubscriptionList searchApplicationAPISubscriptions(
			DevPortalSubscriptionSearchCriteria devPortalSubscriptionSearchCriteria, String username)
			throws SubscriptionOperationException {
		if (devPortalSubscriptionSearchCriteria == null) {
			throw new IllegalArgumentException("Les paramètres de recherche sont absents");
		}
		if (StringUtils.isEmpty(devPortalSubscriptionSearchCriteria.getApplicationId())
				&& StringUtils.isEmpty(devPortalSubscriptionSearchCriteria.getApiId())) {
			throw new IllegalArgumentException("Au moins un des paramètres apiId et applicationId est obligatoire");
		}
		if (devPortalSubscriptionSearchCriteria.getLimit() != null
				&& devPortalSubscriptionSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn(
					"Le nombre de souscriptions application - api dépasse le nombre d'élément maximum autorisé: {} > {}",
					devPortalSubscriptionSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			devPortalSubscriptionSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		return subscriptionOperationAPI.searchApplicationAPISubscriptions(devPortalSubscriptionSearchCriteria,
				username);
	}

	private List<org.wso2.carbon.apimgt.rest.api.publisher.Subscription> getAllSubscriptions(@Nonnull String apiId) {
		final var searchCriteria = new PublisherSubscriptionSearchCriteria().apiId(apiId);
		return subscriptionOperationAPI.searchAPISubscriptions(searchCriteria);
	}

	@Override
	public Subscription subscribeAPI(Subscription applicationAPISubscription, String username)
			throws APISubscriptionException {
		// controle des données d'entrée
		checkApplicationAPISubscription(applicationAPISubscription);

		// récupération de l'application
		String applicationId = applicationAPISubscription.getApplicationId();
		String apiId = applicationAPISubscription.getApiId();

		// recherche pour savoir si l'utilisateur à déjà souscrit à cette api
		org.wso2.carbon.apimgt.rest.api.publisher.Subscription subscription = lookupSubscription(apiId, applicationId,
				username);

		Subscription result = null;
		if (subscription == null) {
			// si pas de résultat on fait la souscription
			result = subscriptionOperationAPI.createApplicationAPISubscription(applicationAPISubscription, username);
		} else if (BLOCKED_PUBLISHER_STATUS.contains(subscription.getSubscriptionStatus())) {
			// si on a déjà souscrit mais l'api est bloquée
			try {
				// on débloque
				unblockAPISubscription(subscription.getSubscriptionId());
				result = getSubscriptionAPI(subscription.getSubscriptionId(), username);
			} catch (Exception e) {
				throw new APISubscriptionException(new Subscription().applicationId(applicationId).apiId(apiId)
						.throttlingPolicy(getThrottlingPolicy(username)), username, e);
			}
		} else {
			// si l'api est déjà souscripte et active on récupère
			try {
				result = getSubscriptionAPI(subscription.getSubscriptionId(), username);
			} catch (Exception e) {
				throw new APISubscriptionException(new Subscription().applicationId(applicationId).apiId(apiId)
						.throttlingPolicy(getThrottlingPolicy(username)), username, e);
			}
		}
		return result;
	}

	@Override
	public Subscription subscribeAPIToDefaultUserApplication(String apiId, String username)
			throws APISubscriptionException, ApplicationOperationException {
		Subscription result = null;
		// récupération de l'application
		String applicationId = getDefaultApplicationId(username);
		org.wso2.carbon.apimgt.rest.api.publisher.Subscription subscription = lookupSubscription(apiId, applicationId,
				username);
		if (subscription == null) {
			val applicationAPISubscription = new Subscription().applicationId(applicationId).apiId(apiId)
					.throttlingPolicy(getThrottlingPolicy(username));
			result = subscribeAPI(applicationAPISubscription, username);
		} else if (BLOCKED_PUBLISHER_STATUS.contains(subscription.getSubscriptionStatus())) {
			try {
				unblockAPISubscription(subscription.getSubscriptionId());
				result = getSubscriptionAPI(subscription.getSubscriptionId(), username);
			} catch (Exception e) {
				throw new APISubscriptionException(new Subscription().applicationId(applicationId).apiId(apiId)
						.throttlingPolicy(getThrottlingPolicy(username)), username, e);
			}
		} else {
			try {
				result = getSubscriptionAPI(subscription.getSubscriptionId(), username);
			} catch (Exception e) {
				throw new APISubscriptionException(new Subscription().applicationId(applicationId).apiId(apiId)
						.throttlingPolicy(getThrottlingPolicy(username)), username, e);
			}
		}
		return result;
	}

	@Override
	public void createDefaultSubscriptions(String apiId, boolean isRestricted)
			throws APISubscriptionException, ApplicationOperationException {

		final List<String> usernamesThatHasAlreadySubscribed = getAllSubscriptions(apiId).stream()
				.map(applicationAPISubscription -> {
					final var subscriber = applicationAPISubscription.getApplicationInfo().getSubscriber();
					return APIManagerProperties.Domains.removeDomainFromUsername(subscriber);
				}).collect(Collectors.toList());

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
				final var causeMessage = String.format(
						"Cannot delete subscriptions for usernames other than %s. Please change API status to RETIRED to automatically remove all subscriptions.",
						StringUtils.join(getHandledSubscriptionUsernames(), " or "));
				final var cause = new NotImplementedException(causeMessage);
				throw new SubscriptionOperationException(applicationAPISubscription.getSubscriptionId(), username,
						cause);
			}
		}
	}

	@Override
	public HasSubscriptionStatus hasSubscribeAPIToDefaultUserApplication(String apiId, String username)
			throws ApplicationOperationException, SubscriptionOperationException {
		String applicationId = getDefaultApplicationId(username);
		return hasSubscribeAPI(apiId, applicationId, username);
	}

	@Override
	public HasSubscriptionStatus hasSubscribeAPI(String apiId, String applicationId, String username)
			throws SubscriptionOperationException {
		HasSubscriptionStatus result = null;
		SubscriptionList applicationAPISubscriptions = searchApplicationAPISubscriptions(
				new DevPortalSubscriptionSearchCriteria().apiId(apiId).applicationId(applicationId), username);
		if (CollectionUtils.isEmpty(applicationAPISubscriptions.getList())) {
			result = HasSubscriptionStatus.NOT_SUBSCRIBED;
		} else {
			Subscription subscription = applicationAPISubscriptions.getList().get(0);
			if (BLOCKED_DEVPORTAL_STATUS.contains(subscription.getStatus())) {
				result = HasSubscriptionStatus.SUBSCRIBED_AND_BLOCKED;
			} else
				result = HasSubscriptionStatus.SUBSCRIBED;
		}
		return result;
	}

	@Override
	public Subscription getSubscriptionAPI(String subscriptionId, String username)
			throws SubscriptionOperationException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à récupérer est absent");
		}
		return subscriptionOperationAPI.getApplicationAPISubscription(subscriptionId, username);
	}

	@Override
	public Subscription updateSubscriptionAPI(Subscription applicationAPISubscription, String username)
			throws SubscriptionOperationException {
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
	public void blockAPISubscription(String subscriptionId) throws APIManagerException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à bloquer est absent");
		}
		subscriptionOperationAPI.blockAPISubscription(subscriptionId, SubscriptionStatusEnum.BLOCKED);
	}

	@Override
	public void unblockAPISubscription(String subscriptionId) throws APIManagerException {
		if (StringUtils.isEmpty(subscriptionId)) {
			throw new IllegalArgumentException("L'identifiant de la souscription à débloquer est absent");
		}
		subscriptionOperationAPI.unblockAPISubscription(subscriptionId);
	}

	@Override
	public DocumentContent downloadAPIContent(UUID globalId, UUID mediaId, String username,
			MultiValueMap<String, String> parameters) throws APIManagerException, IOException {
		final var apiInfo = getApiInfo(globalId, mediaId);
		return applicationOperationAPI.getAPIContent(apiInfo.getContext(), apiInfo.getVersion(),
				getDefaultApplicationId(username), username, parameters);
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
		val apiList = apIsService.searchAPI(new APISearchCriteria().globalId(globalId).mediaUuid(mediaId));
		if (apiList.getCount() == 0) {
			throw new APINotFoundException(globalId, mediaId);
		}
		return apiList.getList().get(0);
	}

	@Override
	public ApplicationKey getApplicationKey(String applicationId, String username, EndpointKeyType keyType)
			throws ApplicationOperationException {
		return applicationOperationAPI.getApplicationKeyList(applicationId, username).getList().stream()
				.filter(key -> key.getKeyType() == keyType).findFirst().orElse(null);
	}

	@Override
	public Application getOrCreateDefaultApplication(String username) throws ApplicationOperationException {
		return getOrCreateDefaultApplication(username, applicationProcessor);
	}

	private <R> R getOrCreateDefaultApplication(String username, ApplicationProcessor<R> applicationProcessor)
			throws ApplicationOperationException {
		final var applications = searchApplication(new ApplicationSearchCriteria().name(defaultApplicationName),
				username);
		if (applications == null || CollectionUtils.isEmpty(applications.getList())) {
			final var application = new Application().name(defaultApplicationName)
					.throttlingPolicy(defaultApplicationRequestPolicy);
			final var createdApplication = createApplication(application, username);
			return applicationProcessor.processCreatedApplication(createdApplication);
		} else {
			final var applicationInfo = applications.getList().get(0);
			return applicationProcessor.processExistingApplicationInfo(applicationInfo, username);
		}
	}

	private interface ApplicationProcessor<R> {
		R processCreatedApplication(Application application);

		R processExistingApplicationInfo(ApplicationInfo applicationInfo, String username)
				throws ApplicationOperationException;
	}

	private final ApplicationProcessor<Application> applicationProcessor = new ApplicationProcessor<>() {
		@Override
		public Application processCreatedApplication(Application application) {
			return application;
		}

		@Override
		public Application processExistingApplicationInfo(ApplicationInfo applicationInfo, String username)
				throws ApplicationOperationException {
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

	@Override
	public SubscriptionList searchUserSubscriptions(
			DevPortalSubscriptionSearchCriteria devPortalSubscriptionSearchCriteria, String username)
			throws SubscriptionOperationException {
		if (devPortalSubscriptionSearchCriteria == null) {
			throw new IllegalArgumentException("Les paramètres de recherche sont absents");
		}
		if (StringUtils.isEmpty(devPortalSubscriptionSearchCriteria.getApplicationId())) {
			throw new IllegalArgumentException("ApplicationId est obligatoire");
		}
		if (devPortalSubscriptionSearchCriteria.getLimit() != null
				&& devPortalSubscriptionSearchCriteria.getLimit() > LIMIT_MAX_VALUE) {
			LOGGER.warn(
					"Le nombre de souscriptions application - api dépasse le nombre d'élément maximum autorisé: {} > {}",
					devPortalSubscriptionSearchCriteria.getLimit(), LIMIT_MAX_VALUE);
			devPortalSubscriptionSearchCriteria.setLimit(LIMIT_MAX_VALUE);
		}
		return subscriptionOperationAPI.searchUserSubscriptions(devPortalSubscriptionSearchCriteria, username);
	}

	public void deleteUserSubscriptionsForDatasetAPIs(String username, UUID datasetUuid) throws APIManagerException {
		// 1- Recupération de l'id du rudi_application du owner
		Applications myApps = searchApplication(null, username);
		if (myApps == null) {
			throw new APIManagerException(String.format("No application found for user %s", username));
		}

		Optional<ApplicationInfo> defaultApp = myApps.getList().stream()
				.filter(applicationInfo -> applicationInfo.getName().equals(defaultApplicationName)).findFirst();

		if (defaultApp.isEmpty()) {
			throw new APIManagerException(
					String.format("Default application %s not found for user %s", defaultApplicationName, username));
		}

		String defaultAppId = defaultApp.get().getApplicationId();

		// 2- Recupération des souscriptions du defaultApp du owner
		DevPortalSubscriptionSearchCriteria searchCriteria = new DevPortalSubscriptionSearchCriteria()
				.applicationId(defaultAppId);
		SubscriptionList mySubscriptions = searchUserSubscriptions(searchCriteria, username);

		if (mySubscriptions == null || mySubscriptions.getCount() == 0) {
			// Pas de souscription trouvée, y a rien à supprimer donc comme souscription, on continue sur la suppression du linkedDataset
			return;
		}

		// Gestion de la suppression des souscriptions aux APIs.
		handleDeletionUserSubscriptionsForDatasetAPIs(mySubscriptions, username, datasetUuid);
	}

	@Override
	public void blockUserSubscriptionsForDatasetAPIs(String username, UUID datasetUuid) throws APIManagerException {
		APISearchCriteria searchCriteria = new APISearchCriteria().globalId(datasetUuid)
				.status(APILifecycleStatusState.PUBLISHED);
		APIList apiList = apIsService.searchAPI(searchCriteria);
		if (apiList != null && CollectionUtils.isNotEmpty(apiList.getList())) {
			for (APIInfo apiInfo : apiList.getList()) {
				blockUserSubscriptionsForDatasetAPIs(apiInfo.getId(), null, username);
			}
		}
	}

	private void blockUserSubscriptionsForDatasetAPIs(String apiId, String applicationId, String username)
			throws SubscriptionOperationException {
		List<org.wso2.carbon.apimgt.rest.api.publisher.Subscription> subscriptions = getAllSubscriptions(apiId);
		if (CollectionUtils.isNotEmpty(subscriptions)) {
			for (org.wso2.carbon.apimgt.rest.api.publisher.Subscription subscription : subscriptions) {
				if (isSubscriber(subscription, username) && (applicationId == null
						|| subscription.getApplicationInfo().getApplicationId().equals(applicationId))) {
					subscriptionOperationAPI.blockAPISubscription(subscription.getSubscriptionId(),
							SubscriptionStatusEnum.BLOCKED);
				}
			}
		}
	}

	private boolean isSubscriber(org.wso2.carbon.apimgt.rest.api.publisher.Subscription subscription, String username) {
		return subscription.getApplicationInfo().getSubscriber().equals(username) || subscription.getApplicationInfo()
				.getSubscriber().equals(APIManagerProperties.Domains.addDomainToUsername(username));
	}

	private org.wso2.carbon.apimgt.rest.api.publisher.Subscription lookupSubscription(String apiId,
			String applicationId, String username) {
		org.wso2.carbon.apimgt.rest.api.publisher.Subscription result = null;
		List<org.wso2.carbon.apimgt.rest.api.publisher.Subscription> subscriptions = getAllSubscriptions(apiId);
		if (CollectionUtils.isNotEmpty(subscriptions)) {
			for (org.wso2.carbon.apimgt.rest.api.publisher.Subscription subscription : subscriptions) {
				if (isSubscriber(subscription, username)
						&& subscription.getApplicationInfo().getApplicationId().equals(applicationId)) {
					result = subscription;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Fonction de rollback: Souscrit aux APIs du JDD pour lesquelles on avait réussi la désouscription
	 *
	 * @param subscriptions list des souscriptions ayant été supprimées
	 * @param username      le owner des souscriptions
	 * @throws APISubscriptionException <w
	 */
	private void doRollback(List<Subscription> subscriptions, String username) throws APISubscriptionException {
		for (Subscription subscription : subscriptions) {
			subscribeAPI(subscription, username);
		}
	}

	private void handleDeletionUserSubscriptionsForDatasetAPIs(SubscriptionList mySubscriptions, String username,
			UUID datasetUuid) throws APIManagerException {
		val subscriptionsDeleted = new ArrayList<Subscription>(); // Elle servira pour rollbacker au besoin
		for (Subscription subscription : mySubscriptions.getList()) {
			// RUDI-4264 filtre sur l'état de l'API
			if (APILifecycleStatusState
					.fromValue(subscription.getApiInfo().getLifeCycleStatus()) == APILifecycleStatusState.BLOCKED) {
				// l'api est bloquée, il ne sera pas possible d'en récupérer le détail
				LOGGER.info("L'état {} de l'api {} ne permet pas de vérifier les souscriptions pour l'utilisateur",
						subscription.getApiInfo().getLifeCycleStatus(), subscription.getApiId());
				continue;
			}

			// Suppression de toutes les souscriptions aux médias de ce dataset
			org.wso2.carbon.apimgt.rest.api.publisher.API subscribedAPI = apIsService.getAPI(subscription.getApiId());
			if (subscribedAPI != null) {

				// identifier les API dont le global_id vaut le datasetUuid
				List<org.wso2.carbon.apimgt.rest.api.publisher.@Valid APIInfoAdditionalPropertiesInner> apisDatasetUuid = subscribedAPI
						.getAdditionalProperties().stream()
						.filter(a -> StringUtils.equals(a.getName(), DATASET_UUID_FIELD))
						.filter(a -> StringUtils.equals(a.getValue(), datasetUuid.toString()))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(apisDatasetUuid)) {
					try {
						unsubscribeAPI(subscription.getSubscriptionId(), username);
						subscriptionsDeleted.add(subscription);
					} catch (SubscriptionOperationException exception) {
						doRollback(subscriptionsDeleted, username);
						throw new SubscriptionOperationException(subscription.getSubscriptionId(), username, exception);
					}
				}
			}
		}
	}

}
