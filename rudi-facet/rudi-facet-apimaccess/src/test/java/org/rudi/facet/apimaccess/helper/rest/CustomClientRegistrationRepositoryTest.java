package org.rudi.facet.apimaccess.helper.rest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpStatus;
import org.ehcache.Cache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CustomClientRegistrationRepositoryTest {
	public static final String SCOPE = "apim:admin";
	public static MockWebServer mockWebServer;

	@Mock
	private Cache<String, ClientRegistration> cache;
	private CustomClientRegistrationRepository customClientRegistrationRepository;
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();

	@BeforeAll
	static void beforeAll() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@BeforeEach
	void setUp() {
		final int port = mockWebServer.getPort();
		final String wso2Url = String.format("http://localhost:%s", port);
		customClientRegistrationRepository = new CustomClientRegistrationRepository(
				wso2Url + "/oauth2/token",
				wso2Url + "/client-registration/v0.17/register",
				"rest_api_admin",
				null,
				"ZHL_pEV0g4eHhf5S_xZNNIJ5YGka",
				"SxZpbh4Kmbx4jSvcpT0s3PgpHsAa",
				new String[]{ SCOPE },
				cache
		);
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void addClientRegistration_success() throws IOException {
		final String username = "anonymous";
		final String password = "anonymous";
		final ClientAccessKey clientAccessKey = jsonResourceReader.read("wso2/ClientAccessKey.json", ClientAccessKey.class);

		mockWebServer.enqueue(new MockResponse()
				.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(clientAccessKey))
				.addHeader("Content-Type", "application/json")
		);

		final ClientRegistration clientRegistration = customClientRegistrationRepository.addClientRegistration(username, password);

		assertThat(clientRegistration)
				.hasFieldOrPropertyWithValue("registrationId", username)
				.hasFieldOrPropertyWithValue("clientName", username)
				.hasFieldOrPropertyWithValue("clientId", clientAccessKey.getClientId())
				.hasFieldOrPropertyWithValue("clientSecret", clientAccessKey.getClientSecret())
				.hasFieldOrPropertyWithValue("clientAuthenticationMethod", ClientAuthenticationMethod.BASIC)
				.hasFieldOrPropertyWithValue("authorizationGrantType", AuthorizationGrantType.CLIENT_CREDENTIALS)
				.hasFieldOrPropertyWithValue("redirectUriTemplate", null)
		;
		assertThat(clientRegistration.getScopes()).containsExactly(SCOPE);
	}

	@Test
	void addClientRegistration_error500() {
		final String username = "anonymous";
		final String password = "anonymous";

		final String body = "<html>Internal Server Error</html>";
		mockWebServer.enqueue(new MockResponse()
				.setBody(body)
				.addHeader("Content-Type", "application/json")
				.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
		);

		assertThatThrownBy(() -> customClientRegistrationRepository.addClientRegistration(username, password))
				.as("On retrouve le statut HTTP")
				.hasMessageContaining(Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR))
				.as("On retrouve le body renvoyé par WSO2")
				.hasMessageContaining(body)
		;
	}

}