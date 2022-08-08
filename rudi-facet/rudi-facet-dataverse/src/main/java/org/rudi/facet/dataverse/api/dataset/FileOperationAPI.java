package org.rudi.facet.dataverse.api.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.api.AbstractDataverseAPI;
import org.rudi.facet.dataverse.api.exceptions.CannotReplaceUnpublishedFileException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.api.exceptions.DuplicateFileContentException;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

import static org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI.API_DATASETS_PARAM;
import static org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI.API_DATASETS_PERSISTENT_ID_PARAM;
import static org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI.PERSISTENT_ID;

@Component
@Slf4j
public class FileOperationAPI extends AbstractDataverseAPI {
	private final DatasetOperationAPI datasetOperationAPI;

	public FileOperationAPI(DatasetOperationAPI datasetOperationAPI, ObjectMapper objectMapper) {
		super(objectMapper);
		this.datasetOperationAPI = datasetOperationAPI;
	}

	public void setSingleDatasetFile(String mediaPersistentId, File file) throws DataverseAPIException {

		final Dataset mediaDataset = getDataset(mediaPersistentId);
		final List<DatasetFile> alreadyExistingFiles = mediaDataset.getLatestVersion().getFiles();
		final boolean fileAlreadyExists = CollectionUtils.isNotEmpty(alreadyExistingFiles);

		if (fileAlreadyExists) {
			checkFilesCount(mediaPersistentId, alreadyExistingFiles);
			final DatasetFile existingFile = alreadyExistingFiles.get(0);
			replaceDatasetFile(mediaPersistentId, existingFile.getDataFile().getId(), file);
		} else {
			addDatasetFile(mediaPersistentId, file);
		}
	}

	private void checkFilesCount(String mediaPersistentId, List<DatasetFile> alreadyExistingFiles) {
		final int alreadyExistingFilesCount = alreadyExistingFiles.size();
		if (alreadyExistingFilesCount > 1) {
			log.warn(String.format(
					"Le Dataset média %s ne devrait contenir qu'un seul fichier pourtant il en contient %s",
					mediaPersistentId,
					alreadyExistingFilesCount
			));
		}
	}

	@Nonnull
	private Dataset getDataset(String mediaPersistentId) throws DataverseAPIException {
		return datasetOperationAPI.getDataset(mediaPersistentId);
	}

	void replaceDatasetFile(String mediaPersistentId, String fileId, File file) throws DataverseAPIException {
		try {
			final String uri = UriComponentsBuilder
					.fromHttpUrl(createUrl("files", fileId, "replace"))
					.toUriString();
			postFile(uri, file, "{ \"forceReplace\": \"true\" }");
		} catch (final CannotReplaceUnpublishedFileException e) {
			final String apiUrl = createUrl();
			final String datasetUri = UriComponentsBuilder.fromUriString(apiUrl)
					.replacePath("dataset.xhtml")
					.queryParam(PERSISTENT_ID, mediaPersistentId)
					.toUriString();

			throw new NotImplementedException(String.format(
					"Le remplacement de fichier d'un Dataset au statut Draft n'est possible que dans la version 5.4 de Dataverse mais pas la version 5.1.1. Pour remplacer le fichier, veuillez d'abord supprimer manuellement tous les fichiers du Dataset (à cette URL \"%s\"), puis retenter l'opération.",
					datasetUri
			), e);
		} catch (final DuplicateFileContentException e) {
			log.info("On a demandé de remplacer le fichier {} du Dataset {} mais comme son contenu n'a pas changé, on ignore l'erreur Dataverse : " + e.getMessage(),
					fileId, mediaPersistentId);
		}
	}

	private void addDatasetFile(String mediaPersistentId, File file) throws DataverseAPIException {
		final String uri = UriComponentsBuilder
				.fromHttpUrl(createUrl(API_DATASETS_PARAM, API_DATASETS_PERSISTENT_ID_PARAM, "add"))
				.queryParam(PERSISTENT_ID, mediaPersistentId)
				.toUriString();
		postFile(uri, file);
	}

	private void postFile(String uri, File file, String... jsonData) throws DataverseAPIException {

		boolean forceReplaceMedia = jsonData.length > 0; //Si on a forcé les types dans l'appel du Post

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set(API_HEADER_KEY, getApiToken());

		final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));
		// On n'ajoute pas le fichier JSON "jsonData" car pour le moment aucune information supplémentaire n'est exploitée
		if(forceReplaceMedia) {
			body.add("jsonData", jsonData[0]);
		}

		final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		getRestTemplate().postForEntity(uri, requestEntity, Void.class);
	}
}
