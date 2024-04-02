package org.rudi.facet.apimaccess.helper.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.ehcache.Cache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.ApimaccessSpringBootTest;
import org.rudi.facet.apimaccess.api.registration.Application;
import org.rudi.facet.apimaccess.api.registration.OAuth2DynamicClientRegistrationExceptionFactory;
import org.rudi.facet.apimaccess.api.registration.OAuth2DynamicClientRegistrationOperationAPI;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Disabled
@ApimaccessSpringBootTest
class CustomMemoryClientRegistrationRepositoryUT {

	public static final String SCOPE = "apim:admin";

	public static MockWebServer mockWebServer;

	@Mock
	private Cache<String, ClientRegistration> cache;

	@Mock
	private APIManagerProperties properties;

	@Autowired
	private HttpClientHelper httpClientHelper;

	private CustomMemoryClientRegistrationRepository customMemoryClientRegistrationRepository;

	private final APIManagerProperties APIManagerProperties = new APIManagerProperties();

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
		final var tokenUri = wso2Url + "/oauth2/token";

		final var adminRegistrationId = "rest_api_admin";
		final var adminClientId = "lYIEBuZiVjPDcvbJzKgFHQPmJk8a";
		final var adminClientSecret = "zDeef8__2r058SRgoHMrveenQc0a";

		when(properties.getBaseUrl()).thenReturn("http://localhost");
		when(properties.getPort()).thenReturn(port);

		final var defaultScopes = new String[] { SCOPE };
		final var objectMapper = new ObjectMapper();
		final var clientRegistrationExceptionFactory = new OAuth2DynamicClientRegistrationExceptionFactory(
				objectMapper);

		final var oauth2DynamicClientRegistrationOperationApi = new OAuth2DynamicClientRegistrationOperationAPI(
				properties, clientRegistrationExceptionFactory, httpClientHelper);
		final var clientRegisterForAdmin = new ClientRegistererForAdmin(tokenUri, null, adminRegistrationId,
				adminClientId, adminClientSecret, oauth2DynamicClientRegistrationOperationApi);

		final var clientRegisterForRudiAndAnonymous = new ClientRegistererForRudiAndAnonymous(tokenUri, defaultScopes,
				oauth2DynamicClientRegistrationOperationApi, false);

		final var oAuth2DynamicClientRegistrationOperationAPI = new OAuth2DynamicClientRegistrationOperationAPI(
				properties, clientRegistrationExceptionFactory, httpClientHelper);
		final var clientRegisterForUsers = new ClientRegistererForUsers(tokenUri, defaultScopes,
				oAuth2DynamicClientRegistrationOperationAPI);

		customMemoryClientRegistrationRepository = new CustomMemoryClientRegistrationRepository(cache,
				APIManagerProperties, clientRegisterForAdmin, clientRegisterForRudiAndAnonymous,
				clientRegisterForUsers);
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void addClientRegistration_anonymous_success()
			throws IOException, BuildClientRegistrationException, GetClientRegistrationException {
		final String username = "anonymous";
		final String password = "anonymous";
		final Application clientAccessKey = jsonResourceReader.read("wso2/Application_anonymous.json",
				Application.class);

		mockWebServer.enqueue(
				new MockResponse().setBody(jsonResourceReader.getObjectMapper().writeValueAsString(clientAccessKey))
						.addHeader("Content-Type", "application/json"));

		when(properties.getRegistrationV11Path()).thenReturn("v1.1/register");

		final ClientRegistration clientRegistration = customMemoryClientRegistrationRepository.register(username,
				password);

		assertThat(clientRegistration).hasFieldOrPropertyWithValue("registrationId", username)
				.hasFieldOrPropertyWithValue("clientName", username)
				.hasFieldOrPropertyWithValue("clientId", clientAccessKey.getClientId())
				.hasFieldOrPropertyWithValue("clientSecret", clientAccessKey.getClientSecret())
				.hasFieldOrPropertyWithValue("clientAuthenticationMethod",
						ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.hasFieldOrPropertyWithValue("authorizationGrantType", AuthorizationGrantType.CLIENT_CREDENTIALS)
				.hasFieldOrPropertyWithValue("redirectUriTemplate", null);
		assertThat(clientRegistration.getScopes()).containsExactly(SCOPE);
	}

	@Test
	void addClientRegistration_user_success()
			throws IOException, BuildClientRegistrationException, GetClientRegistrationException {
		final String username = "robert.palmer";
		final String password = "roby@rocks!";
		final Application application = jsonResourceReader.read("wso2/Application.json", Application.class);

		mockWebServer.enqueue(
				new MockResponse().setBody(jsonResourceReader.getObjectMapper().writeValueAsString(application))
						.addHeader("Content-Type", "application/json"));

		when(properties.getRegistrationV11Path()).thenReturn("v1.1/register");

		final ClientRegistration clientRegistration = customMemoryClientRegistrationRepository.register(username,
				password);

		assertThat(clientRegistration).hasFieldOrPropertyWithValue("registrationId", username)
				.hasFieldOrPropertyWithValue("clientName", username)
				.hasFieldOrPropertyWithValue("clientId", application.getClientId())
				.hasFieldOrPropertyWithValue("clientSecret", application.getClientSecret())
				.hasFieldOrPropertyWithValue("clientAuthenticationMethod",
						ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.hasFieldOrPropertyWithValue("authorizationGrantType", AuthorizationGrantType.CLIENT_CREDENTIALS)
				.hasFieldOrPropertyWithValue("redirectUriTemplate", null);
		assertThat(clientRegistration.getScopes()).containsExactly(SCOPE);
	}

	@Test
	void addClientRegistration_anonymous_error500() {
		final String username = "anonymous";
		final String password = "anonymous";

		final String body = "<html>Internal Server Error</html>";
		mockWebServer.enqueue(new MockResponse().setBody(body).addHeader("Content-Type", "application/json")
				.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR));

		when(properties.getRegistrationV11Path()).thenReturn("v1.1/register");

		assertThatThrownBy(() -> customMemoryClientRegistrationRepository.register(username, password))
				.as("On retrouve le username dans le message d'erreur").hasMessageContaining(username)
				.as("On retrouve le body renvoyé par WSO2")
				.hasRootCauseMessage("HTTP 500 INTERNAL_SERVER_ERROR received from API Manager with body : %s", body);
	}

}
