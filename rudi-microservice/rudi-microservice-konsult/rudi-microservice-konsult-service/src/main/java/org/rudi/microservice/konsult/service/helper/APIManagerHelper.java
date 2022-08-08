package org.rudi.microservice.konsult.service.helper;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.APIInfo;
import org.rudi.facet.apimaccess.bean.APIList;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.constant.APISearchPropertyKey;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APINotFoundException;
import org.rudi.facet.apimaccess.exception.APINotUniqueException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertiesException;
import org.rudi.facet.apimaccess.exception.MissingAPIPropertyException;
import org.rudi.facet.apimaccess.helper.rest.ClientAccessKey;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.microservice.konsult.service.exception.AccessDeniedMetadataMediaException;
import org.rudi.microservice.konsult.service.exception.ClientKeyNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		val authenticatedUser = utilContextHelper.getAuthenticatedUser();
		final String username = authenticatedUser.getLogin();
		val clientKey = aclHelper.getClientKeyByLogin(username);
		if (clientKey == null) {
			throw new ClientKeyNotFoundException(username);
		}

		customClientRegistrationRepository.addClientRegistration(username,
				new ClientAccessKey().setClientId(clientKey.getClientId()).setClientSecret(clientKey.getClientSecret()));

		try {
			val apiInfo = getApiInfo(globalId, mediaId);
			return applicationService.hasSubscribeAPIToDefaultUserApplication(apiInfo.getId(), username);
		} catch (APIManagerException e) {
			throw new AppServiceException(
					String.format("Erreur lors de la récupération de la souscription à l'api globalId = %s et mediaId = %s", globalId, mediaId), e);
		}
	}

	@Nonnull
	private APIInfo getApiInfo(@Nullable UUID globalId, UUID mediaId) throws APIManagerException {
		final APIList apiList;
		try {
			apiList = apIsService.searchAPI(new APISearchCriteria().globalId(globalId).mediaUuid(mediaId));
		} catch (APIManagerException e) {
			throw new APIManagerException(
					String.format("Erreur lors de la récupération des apis liées au global_id = %s et media_id = %s", globalId, mediaId), e);
		}

		final var list = apiList.getList();
		if (CollectionUtils.isEmpty(list)) {
			throw new APINotFoundException(globalId, mediaId);
		} else {
			final var size = CollectionUtils.size(list);
			if (size > 1) {
				throw new APINotUniqueException(globalId, mediaId, size);
			}
		}

		return list.get(0);
	}

	/**
	 * @param metadata JDD
	 * @param media    média du JDD
	 * @return le login de l'utilisateur actuel s'il a souscrit au média, "anonymous" si le média est ouvert, exception sinon
	 * @throws AccessDeniedMetadataMediaException si l'utilisateur n'est pas autorisé à télécharger ce média restreint
	 * @throws AppServiceException                en cas d'erreur avec WSO2
	 */
	public String getLoginAbleToDownloadMedia(Metadata metadata, Media media) throws AppServiceException {
		final String authenticatedUserLogin = utilContextHelper.getAuthenticatedUser().getLogin();

		if (userHasSubscribeToMetadataMedia(metadata.getGlobalId(), media.getMediaId())) {
			return authenticatedUserLogin;
		} else if (!metadataDetailsHelper.isRestricted(metadata)) {
			log.warn("L'utilisateur {} n'a pas souscrit au jeu de données {}, on utilise donc le login {} pour télécharger le média {}", authenticatedUserLogin, metadata.getGlobalId(), anonymousUsername, media.getMediaId());
			return anonymousUsername;
		} else { // le JDD est restreint et l'utilisateur n'y a pas souscrit => il ne peut pas télécharger le média
			throw new AccessDeniedMetadataMediaException(authenticatedUserLogin, metadata.getGlobalId(), media.getMediaId());
		}
	}

	/**
	 * @return l'UUID du JDD (global_id) qui contient ce media
	 * @throws MissingAPIPropertiesException si l'API correspondant au média n'a aucune propriétés dans WSO2
	 * @throws MissingAPIPropertyException si l'API correspondant au média n'a pas la propriété contenant le global_id dans WSO2
	 */
	public UUID getGlobalIdFromMediaId(UUID mediaId) throws APIManagerException {
		final var apiInfo = getApiInfo(null, mediaId);
		final var api = apIsService.getAPI(apiInfo.getId());
		final var additionalProperties = api.getAdditionalProperties();
		if (additionalProperties == null) {
			throw new MissingAPIPropertiesException(apiInfo.getId());
		}
		final var globalIdProperty = additionalProperties.get(APISearchPropertyKey.GLOBAL_ID);
		if (StringUtils.isEmpty(globalIdProperty)) {
			throw new MissingAPIPropertyException(APISearchPropertyKey.GLOBAL_ID, apiInfo.getId());
		}
		return UUID.fromString(globalIdProperty);
	}
}
