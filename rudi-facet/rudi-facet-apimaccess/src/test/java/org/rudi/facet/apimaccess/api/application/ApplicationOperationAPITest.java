package org.rudi.facet.apimaccess.api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.apimaccess.api.ManagerAPIProperties;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.ApplicationKeys;
import org.rudi.facet.apimaccess.bean.ApplicationToken;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.exception.APIEndpointException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationOperationAPITest {
	private static MockWebServer mockWebServer;
	private final WebClient webClient = WebClient.create();
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private ApplicationOperationAPI applicationOperationAPI;
	@Mock
	private ManagerAPIProperties managerAPIProperties;

	@BeforeAll
	static void beforeAll() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockWebServer.shutdown();
	}

	@BeforeEach
	void setUp() {
		applicationOperationAPI = new ApplicationOperationAPI(webClient, managerAPIProperties, System.getProperty("java.io.tmpdir"));
//		applicationOperationAPI = new ApplicationOperationAPI();
	}

	@Test
	void getAPIContent_HTTP_OK() throws APIManagerException, IOException {
		final String context = "/datasets/659ad57a-d2b0-442c-af4d-9b57ede31224/dwnl";
		final String version = "1.0.0";
		final String applicationId = "265523a7-080e-4085-9975-bc3ed523c80c";
		final String username = "anonymous";

		final ApplicationKeys applicationKeys = new ApplicationKeys()
				.count(1)
				.addListItem(new ApplicationKey()
						.keyMappingId("ce0cd2e3-869e-4a3e-a07a-f659803df264")
						.keyType(EndpointKeyType.PRODUCTION)
						.consumerSecret("f3fk3ChLYeO6NWsJql9cXRyBhWwa")
				);
		final ApplicationToken applicationToken = new ApplicationToken();
		final String jsonDocumentContent = "{}";

		when(managerAPIProperties.getServerUrl()).thenReturn("http://localhost:" + mockWebServer.getPort());
		when(managerAPIProperties.getServerGatewayUrl()).thenReturn("http://localhost:" + mockWebServer.getPort());
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(applicationKeys))
		);
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(applicationToken))
		);
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonDocumentContent)
		);

		final DocumentContent apiContent = applicationOperationAPI.getAPIContent(context, version, applicationId, username);

		RudiAssertions.assertThat(apiContent)
				.hasContentType(MediaType.APPLICATION_JSON_VALUE)
				.hasFileContent(jsonDocumentContent);
	}

	@Test
	void getAPIContent_HTTP_KO() throws JsonProcessingException {
		final String context = "/datasets/659ad57a-d2b0-442c-af4d-9b57ede31224/dwnl";
		final String version = "1.0.0";
		final String applicationId = "265523a7-080e-4085-9975-bc3ed523c80c";
		final String username = "anonymous";

		final ApplicationKeys applicationKeys = new ApplicationKeys()
				.count(1)
				.addListItem(new ApplicationKey()
						.keyMappingId("ce0cd2e3-869e-4a3e-a07a-f659803df264")
						.keyType(EndpointKeyType.PRODUCTION)
						.consumerSecret("f3fk3ChLYeO6NWsJql9cXRyBhWwa")
				);
		final ApplicationToken applicationToken = new ApplicationToken();
		final String jsonDocumentContent = "{\n" +
				"  \"error\": \"Unknown dataset: cimetieres-sur-rennes-metropole\"\n" +
				"}";

		when(managerAPIProperties.getServerUrl()).thenReturn("http://localhost:" + mockWebServer.getPort());
		when(managerAPIProperties.getServerGatewayUrl()).thenReturn("http://localhost:" + mockWebServer.getPort());
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(applicationKeys))
		);
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(applicationToken))
		);
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(404)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(jsonDocumentContent)
		);

		assertThatThrownBy(() -> applicationOperationAPI.getAPIContent(context, version, applicationId, username))
				.isInstanceOf(APIEndpointException.class)
				.hasMessage("HTTP 404 NOT_FOUND reçu du endpoint de l'API http://localhost:%s/datasets/659ad57a-d2b0-442c-af4d-9b57ede31224/dwnl/1.0.0. Si l'erreur HTTP renvoyée par WSO2 n'est pas reproduite en interrogeant directement le endpoint alors WSO2 est la cause du problème. Il peut être nécessaire d'exécuter le script re-publish-all-apis.sh sur la machine hébergeant WSO2 (cf RUDI-1938).", mockWebServer.getPort())
		;
	}
}
