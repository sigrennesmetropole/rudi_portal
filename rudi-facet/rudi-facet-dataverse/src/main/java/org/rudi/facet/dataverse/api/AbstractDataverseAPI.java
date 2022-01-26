package org.rudi.facet.dataverse.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.helper.RestTemplateHelper;
import org.rudi.facet.dataverse.model.DataverseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RequiredArgsConstructor
public abstract class AbstractDataverseAPI {

	@Getter
	@Value("${dataverse.api.token}")
	private String apiToken;

	@Getter
	@Value("${dataverse.api.url}")
	private String serverUrl;

	protected static final String API_HEADER_KEY = "X-Dataverse-key";

	private final ObjectMapper objectMapper;

	private RestTemplate restTemplate;

	private RestTemplateHelper restTemplateHelper;

	@Autowired
	public final void setRestTemplateHelper(RestTemplateHelper restTemplateHelper) {
		this.restTemplateHelper = restTemplateHelper;
	}

	protected RestTemplate getRestTemplate() throws DataverseAPIException {
		if (restTemplate == null) {
			restTemplate = restTemplateHelper.buildRestTemplate();
		}
		return restTemplate;
	}

	protected HttpHeaders buildHeadersWithApikey() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(API_HEADER_KEY, apiToken);
		return headers;
	}

	protected String createUrl(String... pathComponents) {
		return serverUrl + "/" + StringUtils.join(pathComponents, "/");
	}

	protected String marshalObject(Object object) throws DataverseAPIException {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new DataverseAPIException(e);
		}
	}

	protected <T> T getDataBody(ResponseEntity<DataverseResponse<T>> responseEntity) {
		DataverseResponse<T> dataverseResponse = responseEntity.getBody();
		return dataverseResponse != null ? dataverseResponse.getData() : null;
	}

}
