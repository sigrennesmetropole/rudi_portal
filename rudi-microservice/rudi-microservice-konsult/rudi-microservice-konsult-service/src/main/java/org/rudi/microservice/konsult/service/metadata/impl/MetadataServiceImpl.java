package org.rudi.microservice.konsult.service.metadata.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.service.APIsService;
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
import org.rudi.microservice.konsult.service.metadata.impl.checker.AbstractAccessToDatasetChecker;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataServiceImpl implements MetadataService {

	private static final Integer DEFAULT_START = 0;
	private static final Integer DEFAULT_RESULTS_NUMBER = 100;
	private final DatasetService datasetService;
	private final ApplicationService applicationService;
	private final APIsService apIsService;
	private final APIManagerHelper apiManagerHelper;
	private final MetadataWithSameThemeFinder metadataWithSameThemeFinder;
	private final List<AbstractAccessToDatasetChecker> accessToDatasetCheckerList;

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
	@Nonnull
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
			if (apIsService.existsApi(metadata.getGlobalId(), media.getMediaId())) {
				this.rewriteMediaUrl(metadata, media);
			}
		}
	}

	/**
	 * Exemple de réécriture :
	 *
	 * <pre>
	 * /medias/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0
	 * </pre>
	 *
	 * <p>
	 * Cette URL doit être configurée dans l'Apache qui va recevoir cette URL pour rediriger vers l'URL du média dans WSO2.
	 * </p>
	 *
	 * <p>
	 * URL WSO2 ciblée par l'exemple précédent :
	 * </p>
	 *
	 * <pre>
	 * https://wso2.open-dev.com:8243/datasets/4ff87569-dafc-45ad-ae5b-fac9a5ccbbb1/dwnl/1.0.0
	 * </pre>
	 */
	private void rewriteMediaUrl(Metadata metadata, Media media) throws APIManagerException {
		final String absoluteUrlString = applicationService.buildAPIAccessUrl(metadata.getGlobalId(),
				media.getMediaId());
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
	public DocumentContent downloadMetadataMedia(UUID globalId, UUID mediaId)
			throws AppServiceException, IOException, GetClientRegistrationException {

		final Metadata metadata = getMetadataById(globalId);
		final Media media = getMetadataMediaById(metadata, mediaId);
		final String loginAbleToDownloadMedia = getLoginAbleToDownloadMedia(metadata, media);
		return downloadMetadataMedia(metadata, media, loginAbleToDownloadMedia, null);
	}

	@Override
	public boolean hasSubscribeToSelfdataDataset(UUID globalId) throws AppServiceException, APIManagerException {
		final var metadata = getMetadataById(globalId);
		UUID ownerUuid = null; // Uuid au à partir duquel on a souscrit (soi-même pour un selfdata)
		for (AbstractAccessToDatasetChecker accessToDatasetChecker : accessToDatasetCheckerList) {
			if (accessToDatasetChecker.accept(metadata)) {
				ownerUuid = accessToDatasetChecker.getOwnerUuidToUse(null);
			}
		}
		return apiManagerHelper.userHasSubscribedToEachMediaOfDataset(globalId, ownerUuid);
	}

	@Override
	public boolean hasSubscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid)
			throws AppServiceException, APIManagerException {
		final var metadata = getMetadataById(globalId);
		UUID ownerUuid = null; // Uuid à partir duquel on a souscrit (soi-même pour un selfdata)
		for (AbstractAccessToDatasetChecker accessToDatasetChecker : accessToDatasetCheckerList) {
			if (accessToDatasetChecker.accept(metadata)) {
				ownerUuid = accessToDatasetChecker.getOwnerUuidToUse(linkedDatasetUuid);
			}
		}
		return apiManagerHelper.userHasSubscribedToEachMediaOfDataset(globalId, ownerUuid);
	}

	@Override
	public void subscribeToSelfdataDataset(UUID globalId) throws APIManagerException, AppServiceException {
		final var metadata = getMetadataById(globalId);
		UUID ownerUuid = null; // Uuid à partir duquel on va souscrire (soi-même pour un selfdata)
		for (AbstractAccessToDatasetChecker accessToDatasetChecker : accessToDatasetCheckerList) {
			if (accessToDatasetChecker.accept(metadata)) {
				accessToDatasetChecker.checkAuthenticatedUserHasAccessToDataset(globalId, Optional.empty());
				ownerUuid = accessToDatasetChecker.getOwnerUuidToUse(null);
			}
		}
		apiManagerHelper.subscribeToEachMediaOfDataset(globalId, ownerUuid);
	}

	@Override
	public void unsubscribeToDataset(UUID globalId, UUID subscriptionOwnerUuid) throws APIManagerException {
		applicationService.deleteUserSubscriptionsForDatasetAPIs(subscriptionOwnerUuid.toString(), globalId);
	}

	@Override
	public void subscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid)
			throws APIManagerException, AppServiceException {
		final var metadata = getMetadataById(globalId);
		UUID ownerUuid = null; // Uuid à partir duquel on va souscrire (soi-même ou une de nos organisations)
		for (AbstractAccessToDatasetChecker accessToDatasetChecker : accessToDatasetCheckerList) {
			if (accessToDatasetChecker.accept(metadata)) {
				accessToDatasetChecker.checkAuthenticatedUserHasAccessToDataset(globalId,
						Optional.of(linkedDatasetUuid));
				ownerUuid = accessToDatasetChecker.getOwnerUuidToUse(linkedDatasetUuid);
			}
		}
		apiManagerHelper.subscribeToEachMediaOfDataset(globalId, ownerUuid);
	}

	private Media getMetadataMediaById(Metadata metadata, UUID mediaId) throws MediaNotFoundException {
		final Optional<Media> optionalMedia = metadata.getAvailableFormats().stream()
				.filter(actualMedia -> actualMedia.getMediaId().equals(mediaId)).findFirst();
		if (optionalMedia.isEmpty()) {
			throw new MediaNotFoundException(mediaId, metadata.getGlobalId());
		}
		return optionalMedia.get();
	}

	/**
	 * Nom d'utilisateur utilisé pour télécharger le média à travers WSO2
	 */
	private String getLoginAbleToDownloadMedia(Metadata metadata, Media media) throws AppServiceException {
		return apiManagerHelper.getLoginAbleToDownloadMedia(metadata, media);
	}

	private DocumentContent downloadMetadataMedia(Metadata metadata, Media media, String loginAbleToDownloadMedia,
			MultiValueMap<String, String> parameters)
			throws UnhandledMediaTypeException, APIManagerExternalServiceException, IOException {
		if (media.getMediaType().equals(Media.MediaTypeEnum.FILE)
				|| media.getMediaType().equals(Media.MediaTypeEnum.SERVICE)) {
			try {
				return applicationService.downloadAPIContent(metadata.getGlobalId(), media.getMediaId(),
						loginAbleToDownloadMedia, parameters);
			} catch (APIManagerException e) {
				throw new APIManagerExternalServiceException(e);
			}
		}

		throw new UnhandledMediaTypeException(media.getMediaType());
	}

	@Override
	public Boolean hasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException {
		return apiManagerHelper.findOwnerToDownload(globalId, mediaId) != null;
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

	@Override
	public List<Metadata> getMetadatasWithSameTheme(UUID globalId, Integer limit) throws AppServiceException {
		final var metadata = getMetadataById(globalId);
		try {
			return metadataWithSameThemeFinder.find(metadata.getDataverseDoi(), limit);
		} catch (DataverseAPIException e) {
			throw new DataverseExternalServiceException(e);
		}
	}

	@Override
	public Integer getNumberOfDatasetsOnTheSameTheme(UUID globalId) throws AppServiceException {
		final var metadata = getMetadataById(globalId);
		try {
			return metadataWithSameThemeFinder.getNumberOfDatasetsOnTheSameTheme(metadata.getDataverseDoi());
		} catch (DataverseAPIException de) {
			throw new DataverseExternalServiceException(de);
		}

	}

	@Override
	public DocumentContent callServiceMetadataMedia(UUID globalId, UUID mediaId, Map<String, String> parameters)
			throws AppServiceException, GetClientRegistrationException, IOException {
		final Metadata metadata = getMetadataById(globalId);
		final Media media = getMetadataMediaById(metadata, mediaId);
		final String loginAbleToDownloadMedia = getLoginAbleToDownloadMedia(metadata, media);
		return downloadMetadataMedia(metadata, media, loginAbleToDownloadMedia, mapToMultiValueMap(parameters));
	}

	private MultiValueMap<String, String> mapToMultiValueMap(Map<String, String> mapToCast) {
		var result = new LinkedMultiValueMap<String, String>();
		for (Map.Entry<String, String> entry : mapToCast.entrySet()) {
			result.add(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
