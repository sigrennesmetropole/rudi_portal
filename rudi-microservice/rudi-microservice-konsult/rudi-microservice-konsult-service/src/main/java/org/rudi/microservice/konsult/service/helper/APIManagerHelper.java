package org.rudi.microservice.konsult.service.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusState;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.HasSubscriptionStatus;
import org.rudi.facet.apimaccess.constant.APISearchPropertyKey;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.apimaccess.exception.APINotUniqueException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertiesException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertyException;
import org.rudi.facet.apimaccess.helper.api.AdditionalPropertiesHelper;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMediaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class APIManagerHelper {

	private final APIsService apIsService;
	private final ApplicationService applicationService;
	private final MetadataDetailsHelper metadataDetailsHelper;
	private final AdditionalPropertiesHelper additionalPropertiesHelper;
	private final String anonymousUsername;
	private final ACLHelper aclHelper;
	private final OrganizationHelper organizationHelper;
	private final ProjektHelper projektHelper;

	public APIManagerHelper(APIsService apIsService, ApplicationService applicationService,
			MetadataDetailsHelper metadataDetailsHelper,
			@Value("${apimanager.oauth2.client.anonymous.username}") String anonymousUsername, ACLHelper aclHelper,
			OrganizationHelper organizationHelper, ProjektHelper projektHelper,
			AdditionalPropertiesHelper additionalPropertiesHelper) {
		this.apIsService = apIsService;
		this.applicationService = applicationService;
		this.metadataDetailsHelper = metadataDetailsHelper;
		this.additionalPropertiesHelper = additionalPropertiesHelper;
		this.anonymousUsername = anonymousUsername;
		this.aclHelper = aclHelper;
		this.organizationHelper = organizationHelper;
		this.projektHelper = projektHelper;
	}

	public boolean userHasSubscribedToEachMediaOfDataset(UUID globalId, UUID ownerUuid) throws APIManagerException {
		return doForEachMediaOfDataset(globalId, ownerUuid, (apiInfo, username) -> null,
				List.of(HasSubscriptionStatus.NOT_SUBSCRIBED, HasSubscriptionStatus.SUBSCRIBED_AND_BLOCKED));
	}

	public void subscribeToEachMediaOfDataset(UUID globalId, UUID uuidToSubscribe) throws APIManagerException {
		doForEachMediaOfDataset(globalId, uuidToSubscribe,
				(apiInfo, username) -> applicationService.subscribeAPIToDefaultUserApplication(apiInfo.getId(),
						username),
				List.of(HasSubscriptionStatus.NOT_SUBSCRIBED, HasSubscriptionStatus.SUBSCRIBED_AND_BLOCKED));
	}

	private boolean doForEachMediaOfDataset(UUID globalId, UUID ownerUuid, ForEachMediaFunction mediaFunction,
			List<HasSubscriptionStatus> statusFilter) throws APIManagerException {
		final String username = getUsernameToSubscribe(ownerUuid);

		final var searchCriteria = new APISearchCriteria().globalId(globalId).status(APILifecycleStatusState.PUBLISHED);
		final var mediasOfDataset = apIsService.searchAPI(searchCriteria);
		for (final var api : mediasOfDataset.getList()) {
			HasSubscriptionStatus subscriptionStatus = applicationService
					.hasSubscribeAPIToDefaultUserApplication(api.getId(), username);
			if (CollectionUtils.isEmpty(statusFilter) || statusFilter.contains(subscriptionStatus)) {
				final var continueObject = mediaFunction.apply(api, username);
				if (continueObject == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Permet de savoir si un user a souscris à un média
	 *
	 * @param globalId id du jdd
	 * @param mediaId  id du media
	 * @param uuid     uuid dont on veut verifier la souscription
	 * @return true si le user a souscrit
	 * @throws AppServiceException Erreur de récupération de l'api ou de la souscription
	 */
	public HasSubscriptionStatus userHasSubscribeToMetadataMedia(UUID globalId, UUID mediaId, UUID uuid)
			throws AppServiceException {
		final String username = getUsernameToSubscribe(uuid);

		try {
			val apiInfo = getApiInfo(globalId, mediaId);
			return applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), username);
		} catch (APIManagerException e) {
			throw new AppServiceException(String.format(
					"Erreur lors de la récupération de la souscription à l'api globalId = %s et mediaId = %s", globalId,
					mediaId), e);
		}
	}

	private String getUsernameToSubscribe(UUID uuidToUse) {
		val user = aclHelper.getUserByUUID(uuidToUse);
		if (user == null) {
			return uuidToUse.toString();
		} else {
			return user.getLogin();
		}
	}

	@Nonnull
	private APIInfo getApiInfo(@Nullable UUID globalId, UUID mediaId) throws APIManagerException {
		final APIList apiList;
		try {
			apiList = apIsService.searchAPI(new APISearchCriteria().globalId(globalId).mediaUuid(mediaId));
		} catch (APIManagerException e) {
			throw new APIManagerException(
					String.format("Erreur lors de la récupération des apis liées au global_id = %s et media_id = %s",
							globalId, mediaId),
					e);
		}

		if (apiList == null || CollectionUtils.isEmpty(apiList.getList())) {
			throw new APINotFoundException(globalId, mediaId);
		} else if (CollectionUtils.size(apiList.getList()) > 1) {
			throw new APINotUniqueException(globalId, mediaId, apiList.getList().size());
		}

		return apiList.getList().get(0);
	}

	/**
	 * @param metadata JDD
	 * @param media    média du JDD
	 * @return le login de l'utilisateur actuel s'il a souscrit au média, "anonymous" si le média est ouvert, exception sinon
	 * @throws AccessDeniedMetadataMediaException si l'utilisateur n'est pas autorisé à télécharger ce média restreint
	 * @throws AppServiceException                en cas d'erreur avec WSO2
	 */
	public String getLoginAbleToDownloadMedia(Metadata metadata, Media media) throws AppServiceException {

		User ownerToDownload = findOwnerToDownload(metadata.getGlobalId(), media.getMediaId());
		if (ownerToDownload != null) {
			return ownerToDownload.getLogin();
		}

		if (!metadataDetailsHelper.isRestricted(metadata)) {
			log.warn(
					"L'utilisateur connecté n'a pas souscrit au jeu de données {}, "
							+ "on utilise donc le login {} pour télécharger le média {}",
					metadata.getGlobalId(), anonymousUsername, media.getMediaId());
			return anonymousUsername;
		} else {
			// le JDD est restreint et l'utilisateur connecté n'y a pas souscrit => il ne peut pas télécharger le média
			throw new AccessDeniedMetadataMediaException(media.getMediaId(), metadata.getGlobalId());
		}
	}

	/**
	 * @return l'UUID du JDD (global_id) qui contient ce media
	 * @throws MissingAPIPropertiesException si l'API correspondant au média n'a aucune propriétés dans WSO2
	 * @throws MissingAPIPropertyException   si l'API correspondant au média n'a pas la propriété contenant le global_id dans WSO2
	 */
	public UUID getGlobalIdFromMediaId(UUID mediaId) throws APIManagerException {
		final var apiInfo = getApiInfo(null, mediaId);
		final var api = apIsService.getAPI(apiInfo.getId());
		final var additionalProperties = api.getAdditionalProperties();
		if (CollectionUtils.isEmpty(additionalProperties)) {
			throw new MissingAPIPropertiesException(apiInfo.getId());
		}
		final var globalIdProperty = additionalPropertiesHelper.getAdditionalPropertiesListAsMap(additionalProperties)
				.get(APISearchPropertyKey.GLOBAL_ID);
		if (StringUtils.isEmpty(globalIdProperty)) {
			throw new MissingAPIPropertyException(APISearchPropertyKey.GLOBAL_ID, apiInfo.getId());
		}
		return UUID.fromString(globalIdProperty);
	}

	@FunctionalInterface
	private interface ForEachMediaFunction {
		/**
		 * @return null pour arrêter la boucle for-each
		 */
		@Nullable
		Object apply(APIInfo apiInfo, String username) throws APIManagerException;
	}

	public List<User> collectOrganizationsWithAccess(UUID userUuid, UUID globalId) throws AppServiceException {
		List<User> ownersList = new ArrayList<>();
		List<UUID> organizationUuids = organizationHelper.getMyOrganizationsUuids(userUuid);
		for (UUID organizationUuid : organizationUuids) {
			Organization organization = organizationHelper.getOrganization(organizationUuid);
			if (organization == null) {
				throw new AppServiceException(
						"Une organisation devrait exister mais n'est pas récupérable, UUID : " + organizationUuid);
			}
			User organizationUser = aclHelper.getUserByLogin(organization.getUuid().toString());
			if (organizationUser == null) {
				throw new AppServiceException(
						"l'user correspondant à l'organisation " + organizationUuid + " n'existe pas");
			}
			if (projektHelper.hasAccessToDataset(organization.getUuid(), globalId)) {
				ownersList.add(organizationUser);
			}
		}

		return ownersList;
	}

	/**
	 * Recherche le owner potentiel au nom duquel la souscription a été réalisée
	 *
	 * @param metadataId UUID du JDD dont on veut télécharger les médias
	 * @return user or organisation à partir duquel on fait le téléchargement
	 * @throws AppServiceException levée si problème WSO2
	 */
	public User findOwnerToDownload(UUID metadataId, UUID mediaId) throws AppServiceException {

		val authenticatedUser = aclHelper.getAuthenticatedUser();
		if (userHasSubscribed(authenticatedUser, metadataId, mediaId)) {
			return authenticatedUser;
		}

		val potentialOwners = collectOrganizationsWithAccess(authenticatedUser.getUuid(), metadataId);
		if (CollectionUtils.isNotEmpty(potentialOwners)) {
			for (User potentialOwner : potentialOwners) {
				if (userHasSubscribed(potentialOwner, metadataId, mediaId)) {
					return potentialOwner;
				}
			}
		}

		return null;
	}

	/**
	 * Vérifie s'il existe une souscription pour l'user fourni auprès du média du JDD fourni
	 *
	 * @param user     le user testé
	 * @param globalId le global ID du média
	 * @param mediaId  le media ID
	 * @return si il y'a une souscription ou pas
	 */
	private boolean userHasSubscribed(User user, UUID globalId, UUID mediaId) {
		boolean userHasSubscribed = false;

		try {
			userHasSubscribed = !HasSubscriptionStatus.NOT_SUBSCRIBED
					.equals(userHasSubscribeToMetadataMedia(globalId, mediaId, user.getUuid()));
		} catch (AppServiceException e) {
			log.error("Erreur lors de la vérification de la souscription de l'utilisateur " + user.getLogin()
					+ " au média " + mediaId + " du JDD " + globalId, e);
		}

		return userHasSubscribed;
	}

}
