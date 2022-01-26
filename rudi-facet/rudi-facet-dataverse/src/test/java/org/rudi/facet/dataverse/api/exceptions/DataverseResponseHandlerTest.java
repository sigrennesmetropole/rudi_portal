package org.rudi.facet.dataverse.api.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.model.ApiResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataverseResponseHandlerTest {
	@InjectMocks
	private DataverseResponseHandler dataverseResponseHandler;
	private final static JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();

	@Test
	void handleErrorPersistentIdNotFound() throws IOException {
		final ClientHttpResponse clientHttpResponse = mock(ClientHttpResponse.class);

		when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

		final ApiResponseInfo apiResponseInfo = new ApiResponseInfo();
		apiResponseInfo.setMessage("Dataset with Persistent ID doi:10.5072/FK2/YA1TWR not found.");
		final InputStream body = new ByteArrayInputStream(JSON_RESOURCE_READER.getObjectMapper().writeValueAsBytes(apiResponseInfo));
		when(clientHttpResponse.getBody()).thenReturn(body);

		assertThatThrownBy(() -> dataverseResponseHandler.handleError(clientHttpResponse))
				.isInstanceOf(DataverseAPIException.class)
				.isInstanceOf(DatasetNotFoundException.class)
				.hasMessage("Le Dataset de persistentId=\"doi:10.5072/FK2/YA1TWR\" est introuvable");
	}

	@Test
	void handleErrorPersistentIdNotFoundUnexpectedMessage() throws IOException {
		final ClientHttpResponse clientHttpResponse = mock(ClientHttpResponse.class);

		when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

		final ApiResponseInfo apiResponseInfo = new ApiResponseInfo();
		apiResponseInfo.setMessage("Dataset not found.");
		final InputStream body = new ByteArrayInputStream(JSON_RESOURCE_READER.getObjectMapper().writeValueAsBytes(apiResponseInfo));
		when(clientHttpResponse.getBody()).thenReturn(body);

		assertThatThrownBy(() -> dataverseResponseHandler.handleError(clientHttpResponse))
				.isInstanceOf(DataverseAPIException.class)
				.hasMessage("Error  code returned 404 with message [Dataset not found.]");
	}
}
