package org.rudi.facet.kmedia.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.dataset.FileOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.bean.Identifier;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaDatasetList;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.bean.MediaSearchCriteria;
import org.rudi.facet.kmedia.helper.dataset.metadatablock.MediaDatasetBlockHelper;
import org.rudi.facet.kmedia.helper.search.mapper.MediaSearchCriteriaMapper;
import org.rudi.facet.kmedia.helper.search.mapper.MediaSearchElementMapper;
import org.rudi.facet.kmedia.service.MediaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

	private static final String SEARCH_CRITERIA_MISSING_MESSAGE = "MediaSearchCriteria missing";
	private static final String MEDIA_AUTHOR_IDENTIFIER_MISSING_MESSAGE = "Media Author Identifier missing";
	private static final String MEDIA_KIND_OF_DATA_MISSING_MESSAGE = "Media Kind of data missing";

	private static final Integer DEFAULT_START = 0;
	private static final Integer DEFAULT_RESULTS_NUMBER = 100;

	@Getter
	@Value("${dataverse.api.rudi.media.data.alias}")
	private String mediaDataAlias;

	private final DatasetOperationAPI datasetOperationAPI;
	private final FileOperationAPI fileOperationAPI;
	private final MediaDatasetBlockHelper mediaDatasetBlockHelper;
	private final MediaSearchCriteriaMapper mediaSearchCriteriaMapper;
	private final MediaSearchElementMapper mediaSearchElementMapper;

	@Override
	@Nullable
	public DocumentContent getMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException {
		return getSingleMediaFile(
				new MediaSearchCriteria()
						.kindOfData(kindOfData)
						.mediaAuthorAffiliation(mediaAuthorAffiliation)
						.mediaAuthorIdentifier(mediaAuthorIdentifier)
						.limit(1)
						.offset(0));
	}

	@Nullable
	private DocumentContent getSingleMediaFile(MediaSearchCriteria mediaSearchCriteria) throws DataverseAPIException {

		DocumentContent documentContent = null;
		String mediaPersistentId;

		if (mediaSearchCriteria == null) {
			throw new IllegalArgumentException(SEARCH_CRITERIA_MISSING_MESSAGE);
		} else if (mediaSearchCriteria.getMediaAuthorIdentifier() == null) {
			throw new IllegalArgumentException(MEDIA_AUTHOR_IDENTIFIER_MISSING_MESSAGE);
		} else if (mediaSearchCriteria.getKindOfData() == null) {
			throw new IllegalArgumentException(MEDIA_KIND_OF_DATA_MISSING_MESSAGE);
		}

		// rechercher le dataset en fonction de l'UUID de l'auteur, du type de donnée et du type d'auteur (fournisseur ou producteur)
		SearchParams searchParams = mediaSearchCriteriaMapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteria);
		SearchElements<SearchDatasetInfo> searchElements = datasetOperationAPI.searchDataset(searchParams);

		if (searchElements != null && searchElements.getTotal() > 0) {
			mediaPersistentId = searchElements.getItems().get(0).getGlobalId();
			// télécharger le fichier en fonction du persistentId du dataset
			documentContent = datasetOperationAPI.getSingleDatasetFile(mediaPersistentId);
		}

		return documentContent;
	}

	@Override
	public void setMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData, File media) throws DataverseAPIException {
		final Identifier mediaDatasetId = getOrCreateMediaDatasetFor(mediaAuthorAffiliation, mediaAuthorIdentifier, kindOfData);
		fileOperationAPI.setSingleDatasetFile(mediaDatasetId.getPersistentId(), media);
	}

	private Identifier getOrCreateMediaDatasetFor(MediaOrigin mediaOrigin, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException {
		final MediaDataset existingMediaDataset = getMediaDatasetFor(mediaAuthorIdentifier, kindOfData);

		final Identifier mediaDatasetId;
		if (existingMediaDataset == null) {
			mediaDatasetId = createMediaDatasetFor(mediaOrigin, mediaAuthorIdentifier, kindOfData);
		} else {
			mediaDatasetId = getIdentifier(existingMediaDataset);
		}
		return mediaDatasetId;
	}

	/**
	 * @param mediaAuthorIdentifier l'auteur dont on cherche le média associé
	 * @param kindOfData            type du média demandé
	 * @return le DataSet du média pour cet auteur, <code>null</code> s'il n'existe aucun média du type demandé pour cet auteur
	 */
	@Nullable
	private MediaDataset getMediaDatasetFor(UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException {
		final MediaSearchCriteria mediaSearchCriteria = new MediaSearchCriteria()
				.kindOfData(kindOfData)
				.mediaAuthorAffiliation(MediaOrigin.PROVIDER)
				.mediaAuthorIdentifier(mediaAuthorIdentifier)
				.offset(0)
				.limit(1);
		final MediaDatasetList mediaDatasetList = searchMedia(mediaSearchCriteria);
		return CollectionUtils.isNotEmpty(mediaDatasetList.getItems()) ? mediaDatasetList.getItems().get(0) : null;
	}

	private Identifier createMediaDatasetFor(MediaOrigin mediaOrigin, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException {
		final MediaDataset mediaDatasetToCreate = new MediaDataset();
		mediaDatasetToCreate.setKindOfData(kindOfData);
		mediaDatasetToCreate.setAuthorAffiliation(mediaOrigin);
		mediaDatasetToCreate.setAuthorIdentifier(mediaAuthorIdentifier);
		final String authorName = mediaOrigin + " " + mediaAuthorIdentifier;
		mediaDatasetToCreate.setAuthorName(authorName);
		mediaDatasetToCreate.setTitle("Logo du " + authorName);
		return createMedia(mediaDatasetToCreate);
	}

	private static Identifier getIdentifier(MediaDataset mediaDataset) {
		return new Identifier().persistentId(mediaDataset.getDataverseDoi());
	}

	/**
	 * Recherche les médias en fonction de critères
	 *
	 * @param mediaSearchCriteria critères de rechercher et de tri des médias
	 * @return liste de medias
	 * @throws DataverseAPIException en cas d'erreur avec l'API Dataverse
	 */
	private MediaDatasetList searchMedia(MediaSearchCriteria mediaSearchCriteria) throws DataverseAPIException {

		if (mediaSearchCriteria == null) {
			throw new IllegalArgumentException(SEARCH_CRITERIA_MISSING_MESSAGE);
		}
		if (mediaSearchCriteria.getOffset() == null) {
			mediaSearchCriteria.setOffset(DEFAULT_START);
		}
		if (mediaSearchCriteria.getLimit() == null || mediaSearchCriteria.getLimit() > 100) {
			mediaSearchCriteria.setLimit(DEFAULT_RESULTS_NUMBER);
		}

		SearchParams searchParams = mediaSearchCriteriaMapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteria);
		return mediaSearchElementMapper.toMediaDatasetList(datasetOperationAPI.searchDataset(searchParams));
	}

	/**
	 * Crée dans le dataverse Rudi Media un dataset dont les propriétés sont celles de l'objet MediaDataset
	 *
	 * @param mediaDataset le media à créer
	 * @return le persistentId du media créé (identifiant généré par le dataverse)
	 * @throws DataverseAPIException en cas d'erreur avec l'API Dataverse
	 */
	private Identifier createMedia(MediaDataset mediaDataset) throws DataverseAPIException {

		// Création du dataset dans le dataverse Rudi Media
		DatasetVersion datasetVersion = new DatasetVersion()
				.metadataBlocks(mediaDatasetBlockHelper.mediaDatasetToDatasetMetadataBlock(mediaDataset))
				.files(new ArrayList<>());
		Dataset dataset = new Dataset().datasetVersion(datasetVersion);

		return datasetOperationAPI.createDataset(dataset, mediaDataAlias);
	}

	/**
	 * Supprime un media en fonction de son identifiant
	 *
	 * @param mediaId l'identifiant du média dans le dataverse
	 * @throws DataverseAPIException en cas d'erreur avec l'API Dataverse
	 */
	private void deleteMedia(Identifier mediaId) throws DataverseAPIException {
		datasetOperationAPI.deleteDataset(mediaId.getPersistentId());
	}

	@Override
	public void deleteMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException {
		final MediaDataset mediaDataset = getMediaDatasetFor(mediaAuthorIdentifier, kindOfData);
		if (mediaDataset != null) {
			deleteMedia(getIdentifier(mediaDataset));
		}
	}

}
