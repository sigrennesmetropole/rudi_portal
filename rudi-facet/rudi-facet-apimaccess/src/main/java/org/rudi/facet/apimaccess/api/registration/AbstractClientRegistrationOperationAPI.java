package org.rudi.facet.apimaccess.api.registration;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.WebClientHelper;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@RequiredArgsConstructor
public abstract class AbstractClientRegistrationOperationAPI<S extends ClientRegistrationResponse> {

	protected final APIManagerProperties properties;

	protected final APIManagerHttpExceptionFactory clientRegistrationExceptionFactory;

	protected final HttpClientHelper httpClientHelper;

	protected abstract String getRegistrationPath();

	protected WebClient webClient;

	protected abstract boolean isTrustAllCerts();

	/**
	 * @return L'enregistrement (registration) existant pour cet utilisateur. Si aucun enregistrement n'existe, une BuildClientRegistrationException est
	 *         lancée.
	 * @throws GetClientRegistrationException Si aucun enregistrement n'existe ou si un problème survient (il est impossible de distinguer dans quel cas
	 *                                        on se trouve)
	 */
	@Nonnull
	public S getRegistration(String username, String password) throws SSLException, GetClientRegistrationException {
		final var webClient = buildWebClient();
		final var mono = webClient.get()
				.uri(uriBuilder -> uriBuilder.port(properties.getPort()).path(getRegistrationPath())
						.queryParam(RegistrationRequestV11.CLIENT_NAME, username).build())
				.headers(httpHeaders -> httpHeaders.setBasicAuth(username, password)).exchange()
				.flatMap((ClientResponse clientResponse) -> clientResponse
						.bodyToMono(getClientRegistrationResponseClass()));
		return MonoUtils.blockOrThrow(mono, e -> new GetClientRegistrationException(username, e));
	}

	@Nonnull
	private WebClient buildWebClient() throws SSLException {
		if (webClient == null) {
			HttpClient httpClient = httpClientHelper.createReactorHttpClient(isTrustAllCerts(), false, false);

			webClient = WebClient.builder().baseUrl(properties.getBaseUrl())
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.filter(WebClientHelper.createFilterFrom(clientRegistrationExceptionFactory)).build();
		}
		return webClient;
	}

	public S register(String username, String password, RegistrationRequest requestPayload)
			throws SSLException, BuildClientRegistrationException {
		final var webClient = buildWebClient();
		final var mono = webClient.post()
				.uri(uriBuilder -> uriBuilder.port(properties.getPort()).path(getRegistrationPath()).build())
				.headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(requestPayload), requestPayload.getClass())
				.exchange().flatMap((ClientResponse clientResponse) -> clientResponse
						.bodyToMono(getClientRegistrationResponseClass()));
		return MonoUtils.blockOrThrow(mono, e -> new BuildClientRegistrationException(username, e));
	}

	protected abstract Class<S> getClientRegistrationResponseClass();

}
