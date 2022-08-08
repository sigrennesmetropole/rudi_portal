package org.rudi.facet.dataverse.api.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.model.ApiResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.stream.Collectors;

@Slf4j
public class DataverseResponseHandler extends DefaultResponseErrorHandler {
	private static final MessageFormat PERSISTENT_ID_NOT_FOUND = new MessageFormat("Dataset with Persistent ID {0} not found.");
	private final ObjectMapper objectMapper = new ObjectMapper();

	@SneakyThrows
	@Override
	public void handleError(@Nonnull ClientHttpResponse clientHttpResponse) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()))) {
			ApiResponseInfo apiResponseInfo;
			String httpBodyResponse = reader.lines().collect(Collectors.joining(""));
			try {
				apiResponseInfo = objectMapper.readValue(httpBodyResponse, ApiResponseInfo.class);
			} catch (JsonParseException | JsonMappingException e) {
				throw new DataverseAPIException(httpBodyResponse);
			}

			final HttpStatus statusCode = clientHttpResponse.getStatusCode();

			final String responseMessage = apiResponseInfo.getMessage();
			final String exceptionMessage = String.format("Error  code returned %d with message [%s]", statusCode.value(),
					responseMessage);

			if (StringUtils.contains(responseMessage, "You cannot replace an unpublished file")) {
				throw new CannotReplaceUnpublishedFileException(exceptionMessage, apiResponseInfo);
			}
			if (StringUtils.contains(responseMessage, "You may not replace a file with a file that has duplicate content.")) {
				throw new DuplicateFileContentException(exceptionMessage, apiResponseInfo);
			}
			if (statusCode == HttpStatus.NOT_FOUND) {
				try {
					final Object[] parsedMessage = PERSISTENT_ID_NOT_FOUND.parse(responseMessage);
					final String persistentId = (String) parsedMessage[0];
					throw DatasetNotFoundException.fromPersistentId(persistentId);
				} catch (ParseException e) {
					log.error("Unexpected 404 error message from Dataverse : {}", responseMessage);
				}
			}

			throw new DataverseAPIException(exceptionMessage, apiResponseInfo);
		}
	}
}
