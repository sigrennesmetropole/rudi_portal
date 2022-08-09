package org.rudi.facet.apimaccess.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class APIManagerHttpExceptionFactoryTest {

	private final APIManagerHttpExceptionFactory exceptionFactory = new APIManagerHttpExceptionFactory();

	/**
	 * Se produit, par exemple, lorsque WSO2 n'arrive plus à se connecter à ACL pour vérifier les utilisateurs
	 */
	@Test
	void createFromHttp401() {
		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.WWW_AUTHENTICATE, "realm user=\"robert.palmer\"");
		final var exception = exceptionFactory.createFrom(HttpStatus.UNAUTHORIZED, headers, null);
		assertThat(exception).isInstanceOf(UnauthorizedException.class)
				.hasMessageContaining("401");
	}
}
