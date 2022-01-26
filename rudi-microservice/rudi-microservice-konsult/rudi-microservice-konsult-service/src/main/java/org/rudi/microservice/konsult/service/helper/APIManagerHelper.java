package org.rudi.microservice.konsult.service.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.APIInfo;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.rest.ClientAccessKey;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMedia;
import org.rudi.microservice.konsult.service.exception.UnKnownClientKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class APIManagerHelper {

	private final APIsService apIsService;
	private final ApplicationService applicationService;
	private final MetadataDetailsHelper metadataDetailsHelper;
	private final String anonymousUsername;
	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	private final UtilContextHelper utilContextHelper;
	private final ACLHelper aclHelper;

	public APIManagerHelper(
			APIsService apIsService, ApplicationService applicationService,
			MetadataDetailsHelper metadataDetailsHelper,
			@Value("${apimanager.oauth2.client.anonymous.username}") String anonymousUsername,
			CustomClientRegistrationRepository customClientRegistrationRepository,
			UtilContextHelper utilContextHelper,
			ACLHelper aclHelper
	) {
		this.apIsService = apIsService;
		this.applicationService = applicationService;
		this.metadataDetailsHelper = metadataDetailsHelper;
		this.anonymousUsername = anonymousUsername;
		this.customClientRegistrationRepository = customClientRegistrationRepository;
		this.utilContextHelper = utilContextHelper;
		this.aclHelper = aclHelper;
	}

	/**
	 * Permet de savoir si un user a souscris à un média
	 *
	 * @param globalId id du jdd
	 * @param mediaId  id du media
	 * @throws AppServiceException Erreur de récupération de l'api ou de la souscription
	 * @return true si le user a souscrit
	 */
	public boolean userHasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException {
		final AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		final String username = authenticatedUser.getLogin();
		final ClientKey clientKey = aclHelper.getClientKeyByLogin(username);
		if (clientKey == null) {
			throw new UnKnownClientKeyException();
		}

		customClientRegistrationRepository.addClientRegistration(username,
				new ClientAccessKey().setClientId(clientKey.getClientId()).setClientSecret(clientKey.getClientSecret()));

		final APIInfo apiInfo = getApiInfo(globalId, mediaId);

		try {
			return applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), username);
		} catch (APIManagerException e) {
			throw new AppServiceException(
					String.format("Erreur lors de la récupération de la souscription à l'api id = %s", apiInfo.getId()), e);
		}
	}

	private APIInfo getApiInfo(UUID globalId, UUID mediaId) throws AppServiceException {
		final APIList apiList;
		try {
			apiList = apIsService.searchAPI(new APISearchCriteria().globalId(globalId).mediaUuid(mediaId));
		} catch (APIManagerException e) {
			throw new AppServiceException(
					String.format("Erreur lors de la récupération des apis liées au global_id = %s et media_id = %s", globalId, mediaId), e);
		}

		if (CollectionUtils.isEmpty(apiList.getList())) {
			throw new AppServiceException(String.format("Aucune API retrouvé pour le global_id = %s et media_id = %s", globalId, mediaId));
		}

		return apiList.getList().get(0);
	}

	/**
	 * Récupération du username habilité à télécharger un jdd si il en a l'accès
	 *
	 * @param metadata metadata
	 * @param media    media
	 * @return username
	 * @throws AppServiceException Accès non autorisé au jdd
	 */
	public String checkUsernameAbleToDownloadMedia(Metadata metadata, Media media) throws AppServiceException {
		final String actualUsername = utilContextHelper.getAuthenticatedUser().getLogin();

		// nom d'utilisateur utilisé pour télécharger un jdd
		String usernameAbleToDownload;
		if (BooleanUtils.isTrue(userHasSubscribeToMetadataMedia(metadata.getGlobalId(), media.getMediaId()))) {
			usernameAbleToDownload = actualUsername;
		} else if (!metadataDetailsHelper.isRestricted(metadata)) {
			log.warn("L'utilisateur n'a pas souscrit au jeu de données, passage en mode anonyme");
			usernameAbleToDownload = anonymousUsername;
		} else { // si le user utilisé est anonymous et que le jdd est restreint, ca veut dire que le user ne peut pas accéder au jdd
			throw new AccessDeniedMetadataMedia(metadata.getGlobalId(), media.getMediaId());
		}

		return usernameAbleToDownload;
	}
}
