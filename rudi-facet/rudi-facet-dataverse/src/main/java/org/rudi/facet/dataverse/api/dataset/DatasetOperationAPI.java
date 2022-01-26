package org.rudi.facet.dataverse.api.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.api.search.AbstractSearchOperationAPI;
import org.rudi.facet.dataverse.bean.DataFile;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetFile;
import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.bean.Identifier;
import org.rudi.facet.dataverse.bean.MessageResponse;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.model.DataverseResponse;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Ensemble des opérations sur les datasets, avec l'API du dataverse
 */
@Component
public class DatasetOperationAPI extends AbstractSearchOperationAPI<SearchDatasetInfo> {

	static final String API_DATASETS_PARAM = "datasets";
	static final String API_DATASETS_PERSISTENT_ID_PARAM = ":persistentId";
	private static final String API_DATASETS_VERSIONS_PARAM = "versions";
	private static final String API_DATASETS_DRAFT_PARAM = ":draft";
	private static final String API_DATASETS_MOVE_PARAM = "move";
	private static final String API_DATASETS_DESTROY_PARAM = "destroy";
	private static final String API_DATAVERSES_PARAM = "dataverses";
	static final String PERSISTENT_ID = "persistentId";

	private static final Logger LOGGER = LoggerFactory.getLogger(DatasetOperationAPI.class);

	@Value("${temporary.directory:${java.io.tmpdir}}")
	private String temporaryDirectory;

	public DatasetOperationAPI(ObjectMapper defaultObjectMapper) {
		super(defaultObjectMapper);
	}

	public DocumentContent getSingleDatasetFile(String datasetPersistentId) throws DataverseAPIException {
		DocumentContent documentContent = null;

		// récupérer l'id du fichier à partir du dataset
		final Dataset dataset = getDataset(datasetPersistentId);
		final List<DatasetFile> datasetFiles = dataset.getLatestVersion().getFiles();

		if (CollectionUtils.isNotEmpty(datasetFiles)) {
			// on considère que le dataset n'a qu'un seul fichier
			DataFile dataFile = datasetFiles.get(0).getDataFile();

			if (dataFile != null && dataFile.getId() != null) {

				// charger le fichier
				File file = getDatasetFile(dataFile.getId());
				documentContent = new DocumentContent(dataFile.getFilename(), dataFile.getContentType(), dataFile.getFilesize(), file);
			}
		}

		return documentContent;
	}

	public File getDatasetFile(String fileId) throws DataverseAPIException {
		// Ex d'URI : http://dv.open-dev.com:8095/api/access/datafile/581
		String url = createUrl("access", "datafile", fileId);

		LOGGER.debug("Téléchargement du fichier - url : {}", url);

		return getRestTemplate().execute(url, HttpMethod.GET,
				clientHttpRequest -> clientHttpRequest.getHeaders().set(API_HEADER_KEY, getApiToken()),
				clientHttpResponse -> {
					String fileName = clientHttpResponse.getHeaders().getContentDisposition().getFilename();
					File tempFile = File.createTempFile("rudi", FilenameUtils.getExtension(fileName),
							new File(temporaryDirectory));
					StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(tempFile));
					return tempFile;
				});

	}

	/**
	 * @throws org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException if Dataset does not exist
	 */
	@Nonnull
	public Dataset getDataset(String persistentId) throws DataverseAPIException {
		String url = createUrl(API_DATASETS_PARAM, API_DATASETS_PERSISTENT_ID_PARAM);
		url = buildGetDatasetUrl(url, persistentId);

		HttpEntity<String> entity = createHttpEntity("");

		ParameterizedTypeReference<DataverseResponse<Dataset>> type = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<DataverseResponse<Dataset>> resp = getRestTemplate().exchange(url, HttpMethod.GET, entity, type);
		return getDataBody(resp);
	}

	@Override
	public SearchElements<SearchDatasetInfo> searchDataset(SearchParams searchParams) throws DataverseAPIException {
		validateSearchParams(searchParams, SearchType.DATASET);
		return super.searchDataset(searchParams);
	}

	public Identifier createDataset(Dataset dataset, String dataverseIdentifier) throws DataverseAPIException {
		String url = createUrl(API_DATAVERSES_PARAM, dataverseIdentifier, API_DATASETS_PARAM);

		HttpEntity<String> entity = createHttpEntity(marshalObject(dataset));
		ParameterizedTypeReference<DataverseResponse<Identifier>> type = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<DataverseResponse<Identifier>> resp = getRestTemplate().exchange(url, HttpMethod.POST, entity,
				type);
		return getDataBody(resp);
	}

	public DatasetVersion updateDataset(DatasetVersion datasetVersion, String persistentId)
			throws DataverseAPIException {
		String url = createUrl(API_DATASETS_PARAM, API_DATASETS_PERSISTENT_ID_PARAM, API_DATASETS_VERSIONS_PARAM,
				API_DATASETS_DRAFT_PARAM);
		url = buildGetDatasetUrl(url, persistentId);

		HttpEntity<String> entity = createHttpEntity(marshalObject(datasetVersion));
		ParameterizedTypeReference<DataverseResponse<DatasetVersion>> type = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<DataverseResponse<DatasetVersion>> resp = getRestTemplate().exchange(url, HttpMethod.PUT, entity,
				type);
		return getDataBody(resp);
	}

	public void moveDataset(String persistentId, String dataverseIdentifier) throws DataverseAPIException {
		String url = createUrl(API_DATASETS_PARAM, API_DATASETS_PERSISTENT_ID_PARAM, API_DATASETS_MOVE_PARAM,
				dataverseIdentifier);
		url = buildGetDatasetUrl(url, persistentId);

		HttpEntity<String> entity = createHttpEntity("");
		ParameterizedTypeReference<DataverseResponse<MessageResponse>> type = new ParameterizedTypeReference<>() {
		};
		getRestTemplate().exchange(url, HttpMethod.POST,
				entity, type);
	}

	public void deleteDataset(String persistentId) throws DataverseAPIException {
		String url = createUrl(API_DATASETS_PARAM, API_DATASETS_PERSISTENT_ID_PARAM, API_DATASETS_DESTROY_PARAM);
		url = buildGetDatasetUrl(url, persistentId);

		HttpEntity<String> entity = createHttpEntity("");
		ParameterizedTypeReference<DataverseResponse<MessageResponse>> type = new ParameterizedTypeReference<>() {
		};
		getRestTemplate().exchange(url, HttpMethod.DELETE,
				entity, type);
	}

	private HttpEntity<String> createHttpEntity(String body) {
		HttpHeaders headers = buildHeadersWithApikey();
		return new HttpEntity<>(body, headers);
	}

	private String buildGetDatasetUrl(String path, String persistentId) {
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(path).queryParam(PERSISTENT_ID,
				persistentId);
		return urlBuilder.build(true).toUriString();
	}
}
