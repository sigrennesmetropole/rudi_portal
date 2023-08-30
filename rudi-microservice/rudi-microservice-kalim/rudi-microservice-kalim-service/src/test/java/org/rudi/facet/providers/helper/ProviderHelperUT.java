package org.rudi.facet.providers.helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.bean.ProviderPageResult;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProviderHelperUT {
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	public MockWebServer mockWebServer;
	private ProviderHelper providerHelper;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		final String providersServiceUrl = String.format("http://localhost:%s", mockWebServer.getPort());
		providerHelper = new ProviderHelper(
				"/search",
				"/get",
				"/{providerUuid}/nodes/{nodeUuid}",
				providersServiceUrl,
				UUID.fromString("5596b5b2-b227-4c74-a9a1-719e7c1008c7"),
				WebClient.builder()
		);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	private ProviderPageResult createPageResultFrom(List<Provider> providers) {
		final ProviderPageResult pageResult = new ProviderPageResult();
		for (final Provider provider : providers) {
			pageResult.addElementsItem(provider);
		}
		return pageResult;
	}

	@Test
	void getProviderByUUID() throws JsonProcessingException, InterruptedException {
		final Provider searchedProvider = new Provider().uuid(UUID.randomUUID());

		mockWebServer.enqueue(new MockResponse()
				.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(searchedProvider))
				.addHeader("Content-Type", "application/json")
		);

		final Provider providerByUUID = providerHelper.getProviderByUUID(searchedProvider.getUuid());
		assertThat(providerByUUID).isEqualTo(searchedProvider);

		final RecordedRequest providersRequest = mockWebServer.takeRequest();
		assertThat(providersRequest).hasFieldOrPropertyWithValue("path", "/get/" + searchedProvider.getUuid());
	}

	@Test
	void getProviderByNodeProviderUUID() throws JsonProcessingException, InterruptedException {
		final NodeProvider searchedNode = new NodeProvider().uuid(UUID.randomUUID());
		final Provider searchedProvider = new Provider().addNodeProvidersItem(searchedNode);

		final NodeProvider notSearchedNode = new NodeProvider().uuid(UUID.randomUUID());
		final Provider notSearchedProvider = new Provider().addNodeProvidersItem(notSearchedNode);

		final List<Provider> providers = Arrays.asList(
				searchedProvider,
				notSearchedProvider
		);

		final ProviderPageResult providersPageResult = createPageResultFrom(providers);

		mockWebServer.enqueue(new MockResponse()
				.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(providersPageResult))
				.addHeader("Content-Type", "application/json")
		);

		final Provider providerByNodeProviderUUID = providerHelper.requireProviderByNodeProviderUUID(searchedNode.getUuid());

		assertThat(providerByNodeProviderUUID).isEqualTo(searchedProvider);

		final RecordedRequest providersRequest = mockWebServer.takeRequest();
		assertThat(providersRequest).hasFieldOrPropertyWithValue("path", "/search?full=true&limit=1&nodeProviderUuid=" + searchedNode.getUuid());
	}

	@Test
	void getNodeProviderByUUID() throws JsonProcessingException, InterruptedException {
		final NodeProvider searchedNode = new NodeProvider().uuid(UUID.randomUUID());
		final Provider searchedProvider = new Provider().addNodeProvidersItem(searchedNode);

		final NodeProvider notSearchedNode = new NodeProvider().uuid(UUID.randomUUID());
		final Provider notSearchedProvider = new Provider().addNodeProvidersItem(notSearchedNode);

		final List<Provider> providers = Arrays.asList(
				searchedProvider,
				notSearchedProvider
		);

		final ProviderPageResult providersPageResult = createPageResultFrom(providers);

		mockWebServer.enqueue(new MockResponse()
				.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(providersPageResult))
				.addHeader("Content-Type", "application/json")
		);

		final NodeProvider nodeProviderByUUID = providerHelper.requireNodeProviderByUUID(searchedNode.getUuid());

		assertThat(nodeProviderByUUID).isEqualTo(searchedNode);

		final RecordedRequest providersRequest = mockWebServer.takeRequest();
		assertThat(providersRequest).hasFieldOrPropertyWithValue("path", "/search?full=true&limit=1&nodeProviderUuid=" + searchedNode.getUuid());
	}

	@Test
	void getAllProviders() throws JsonProcessingException, InterruptedException {
		final NodeProvider node1 = new NodeProvider().uuid(UUID.randomUUID());
		final Provider node1Provider = new Provider().addNodeProvidersItem(node1);

		final NodeProvider node2 = new NodeProvider().uuid(UUID.randomUUID());
		final Provider node2Provider = new Provider().addNodeProvidersItem(node2);

		final List<Provider> providers = Arrays.asList(
				node1Provider,
				node2Provider
		);

		final ProviderPageResult providersPageResult = createPageResultFrom(providers);

		mockWebServer.enqueue(new MockResponse()
				.setBody(jsonResourceReader.getObjectMapper().writeValueAsString(providersPageResult))
				.addHeader("Content-Type", "application/json")
		);

		final Mono<List<Provider>> allProvidersMono = providerHelper.getAllProviders();

		StepVerifier
				.create(allProvidersMono)
				.assertNext(allProviders ->
						assertThat(allProviders).containsExactly(node1Provider, node2Provider))
				.verifyComplete();

		final RecordedRequest providersRequest = mockWebServer.takeRequest();
		assertThat(providersRequest).hasFieldOrPropertyWithValue("path", "/search?full=true");
	}

	@Test
	void patchNode() throws InterruptedException, IOException {
		final NodeProvider node = new NodeProvider()
				.uuid(UUID.fromString("6e4d9581-4ff0-4549-bf3e-f87ff8ca5ca2"))
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 19, 15, 29, 25));
		final UUID providerUuid = UUID.fromString("322f4713-cabe-4455-b8dd-0dd6d310d23c");

		final String body = jsonResourceReader.getObjectMapper().writeValueAsString(node);
		mockWebServer.enqueue(new MockResponse()
				.setBody(body)
				.addHeader("Content-Type", "application/json")
		);

		final Mono<NodeProvider> nodeProviderMono = providerHelper.patchNode(providerUuid, node.getUuid(), node.getLastHarvestingDate());

		StepVerifier
				.create(nodeProviderMono)
				.assertNext(nodeProvider ->
						assertThat(nodeProvider).isEqualTo(node))
				.verifyComplete();

		final RecordedRequest request = mockWebServer.takeRequest();
		RudiAssertions.assertThat(request)
				.hasMethod(HttpMethod.PATCH)
				.hasPath("/322f4713-cabe-4455-b8dd-0dd6d310d23c/nodes/6e4d9581-4ff0-4549-bf3e-f87ff8ca5ca2?lastHarvestingDate=2021-08-19T15:29:25");
	}
}
