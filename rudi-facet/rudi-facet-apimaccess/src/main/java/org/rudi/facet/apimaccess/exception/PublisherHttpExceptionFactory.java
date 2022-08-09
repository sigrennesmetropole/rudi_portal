package org.rudi.facet.apimaccess.exception;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.wso2.carbon.apimgt.rest.api.publisher.Error;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PublisherHttpExceptionFactory extends APIManagerHttpExceptionFactory {

	private final ObjectMapper objectMapper;

	@Override
	public APIManagerHttpException createFrom(HttpStatus status, HttpHeaders headers, @Nullable String errorBody) {
		if (status.is4xxClientError() && errorBody != null) {
			try {
				final var error = objectMapper.readValue(errorBody, Error.class);
				return new APIManagerHttpException(status, error.getDescription());
			} catch (JsonProcessingException ex) {
				// errorBody is not compatible with Error
			}
		}
		return super.createFrom(status, headers, errorBody);
	}
}
