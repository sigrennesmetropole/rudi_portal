package org.rudi.facet.apimaccess.api.registration;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.apimaccess.exception.APIManagerHttpException;
import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2DynamicClientRegistrationExceptionFactory extends APIManagerHttpExceptionFactory {

	private final ObjectMapper objectMapper;

	@Override
	public APIManagerHttpException createFrom(HttpStatus status, HttpHeaders headers, @Nullable String errorBody) {
		if (status == HttpStatus.BAD_REQUEST && errorBody != null) {
			try {
				final OAuth2DynamicClientRegistrationError error = objectMapper.readValue(errorBody, OAuth2DynamicClientRegistrationError.class);

				final var applicationAlreadyExistException = ApplicationAlreadyExistException.from(errorBody, error);
				if (applicationAlreadyExistException != null) {
					return applicationAlreadyExistException;
				}
			} catch (JsonProcessingException ex) {
				// errorBody is not compatible with OAuth2DynamicClientRegistrationException
			}
		}
		return super.createFrom(status, headers, errorBody);
	}
}
