package org.rudi.facet.apimaccess.api.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.rudi.facet.apimaccess.exception.APIManagerHttpException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2DynamicClientRegistrationExceptionFactoryUT {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final OAuth2DynamicClientRegistrationExceptionFactory factory = new OAuth2DynamicClientRegistrationExceptionFactory(objectMapper);

	@Test
	void createFromApplicationAlreadyExistException() {
		final var errorBody = "{\"error\":\"invalid_client_metadata\",\"error_description\":\"Application with the name robert.palmer already exist in the system\"}";
		final var e = factory.createFrom(HttpStatus.BAD_REQUEST, new HttpHeaders(), errorBody);
		assertThat(e)
				.isInstanceOf(ApplicationAlreadyExistException.class)
				.hasFieldOrPropertyWithValue("clientName", "robert.palmer")
		;
	}

	@Test
	void createFromUnknown() {
		final var errorBody = "{\"error\":\"unknown\",\"error_description\":\"Unknown description\"}";
		final var e = factory.createFrom(HttpStatus.BAD_REQUEST, new HttpHeaders(), errorBody);
		assertThat(e)
				.isInstanceOf(APIManagerHttpException.class)
		;
	}
}
