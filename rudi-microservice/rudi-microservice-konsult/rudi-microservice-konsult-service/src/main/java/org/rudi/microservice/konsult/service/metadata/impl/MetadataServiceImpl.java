package org.rudi.microservice.konsult.service.metadata.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.rest.ClientAccessKey;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacets;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.konsult.service.exception.APIManagerExternalServiceException;
import org.rudi.microservice.konsult.service.exception.DataverseExternalServiceException;
import org.rudi.microservice.konsult.service.exception.MediaNotFoundException;
import org.rudi.microservice.konsult.service.exception.MetadataNotFoundException;
import org.rudi.microservice.konsult.service.exception.UnhandledMediaTypeException;
import org.rudi.microservice.konsult.service.helper.APIManagerHelper;
import org.rudi.microservice.konsult.service.metadata.MetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataServiceImpl implements MetadataService {

	private static final Integer DEFAULT_START = 0;
	private static final Integer DEFAULT_RESULTS_NUMBER = 100;
	private final DatasetService datasetService;
	private final ApplicationService applicationService;
	private final ACLHelper aclHelper;
	private final APIManagerHelper apiManagerHelper;
	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	@Value("${apimanager.oauth2.client.anonymous.username}")
	private String anonymousUsername;

	@Override
	public MetadataList searchMetadatas(DatasetSearchCriteria datasetSearchCriteria) throws DataverseAPIException {
		return searchMetadataWithFacets(datasetSearchCriteria, Collections.emptyList()).getMetadataList();
	}

	@Override
	public MetadataFacets searchMetadatasFacets(List<String> facets) throws DataverseAPIException {
		// récupération des métadonnées sans filtre, avec un seul élément
		// si on met limit = 0, le dataverse va mettre la valeur limit = 10 par défaut
		DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().limit(1).offset(0);
		return searchMetadataWithFacets(datasetSearchCriteria, facets).getFacets();
	}

	@Override
	public Metadata getMetadataById(UUID globalId) throws AppServiceException {
		if (globalId == null) {
			throw new MissingParameterException("L'identifiant du jeu de donnée est absent");
		}
		try {
			final Metadata metadata = datasetService.getDataset(globalId);
			rewriteMediaUrls(metadata);
			return metadata;
		} catch (DatasetNotFoundException e) {
			throw new MetadataNotFoundException(e);
		} catch (DataverseAPIException e) {
			throw new DataverseExternalServiceException(e);
		} catch (APIManagerException e) {
			throw new APIManagerExternalServiceException(e);
		}
	}

	private void rewriteMediaUrls(Metadata metadata) throws APIManagerException {
		for (final Media media : metadata.getAvailableFormats()) {
			if(applicationService.hasApi(metadata.getGlobalId(), media.getMediaId())) {
				this.rewriteMediaUrl(metadata, media);
			}
		}
	}

	/**
	 * Exemple de réécriture :
	 *
	 * <pre>/medias/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0</pre>
	 *
	 * <p>
	 * Cette URL doit être configurée dans l'Apache qui va recevoir cette URL pour rediriger vers l'URL du média dans
	 * WSO2.
	 * </p>
	 *
	 * <p>URL WSO2 ciblée par l'exemple précédent :</p>
	 *
	 * <pre>https://wso2.open-dev.com:8243/datasets/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0</pre>
	 */
	private void rewriteMediaUrl(Metadata metadata, Media media) throws APIManagerException {
		final String absoluteUrlString = applicationService.buildAPIAccessUrl(metadata.getGlobalId(), media.getMediaId());
		final String path = getUrlPath(absoluteUrlString);
		final String rewrittenPath = path.replace("/datasets/", "/medias/");
		media.getConnector().setUrl(rewrittenPath);
	}

	@Nonnull
	private String getUrlPath(String absoluteUrlString) {
		final URL absoluteUrl;
		try {
			absoluteUrl = new URL(absoluteUrlString);
			return absoluteUrl.getPath();
		} catch (MalformedURLException e) {
			log.error("Invalid media URL {}", absoluteUrlString);
			return StringUtils.EMPTY;
		}
	}

	@Override
	public DocumentContent downloadMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException {

		final Metadata metadata = getMetadataById(globalId);
		final Media media = getMetadataMediaById(metadata, mediaId);
		final String authorizedUsername = getAuthorizedUsername(metadata, media);
		return downloadMetadataMedia(metadata, media, authorizedUsername);
	}

	private Media getMetadataMediaById(Metadata metadata, UUID mediaId) throws MediaNotFoundException {
		final Optional<Media> optionalMedia = metadata.getAvailableFormats().stream()
				.filter(actualMedia -> actualMedia.getMediaId().equals(mediaId)).findFirst();
		if (optionalMedia.isEmpty()) {
			throw new MediaNotFoundException(mediaId, metadata.getGlobalId());
		}
		return optionalMedia.get();
	}

	private String getAuthorizedUsername(Metadata metadata, Media media) throws AppServiceException {
		// nom d'utilisateur utilisé pour faire les requêtes wso2
		return apiManagerHelper.checkUsernameAbleToDownloadMedia(metadata, media);
	}

	private DocumentContent downloadMetadataMedia(Metadata metadata, Media media, String authorizedUsername) throws UnhandledMediaTypeException, APIManagerExternalServiceException {
		// si l'utilisateur est anonymous, il faut récupérer ses client id et client secret s'ils ne sont pas déjà dans le cache
		if (authorizedUsername.equals(anonymousUsername) && customClientRegistrationRepository.findByRegistrationId(anonymousUsername).block() == null) {
			ClientKey clientKey = aclHelper.getClientKeyByLogin(anonymousUsername);
			customClientRegistrationRepository.addClientRegistration(anonymousUsername,
					new ClientAccessKey().setClientId(clientKey.getClientId()).setClientSecret(clientKey.getClientSecret()));
		}

		if (media.getMediaType().equals(Media.MediaTypeEnum.FILE)) {
			try {
				return applicationService.downloadAPIContent(metadata.getGlobalId(), media.getMediaId(), authorizedUsername);
			} catch (APIManagerException e) {
				throw new APIManagerExternalServiceException(e);
			}
		}

		throw new UnhandledMediaTypeException(media.getMediaType());
	}

	@Override
	public Boolean hasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException {
		return apiManagerHelper.userHasSubscribeToMetadataMedia(globalId, mediaId);
	}

	private MetadataListFacets searchMetadataWithFacets(DatasetSearchCriteria datasetSearchCriteria,
			List<String> facets) throws DataverseAPIException {
		if (datasetSearchCriteria.getOffset() == null) {
			datasetSearchCriteria.setOffset(DEFAULT_START);
		}
		if (datasetSearchCriteria.getLimit() == null) {
			datasetSearchCriteria.setLimit(DEFAULT_RESULTS_NUMBER);
		}
		return datasetService.searchDatasets(datasetSearchCriteria, facets);
	}
}
