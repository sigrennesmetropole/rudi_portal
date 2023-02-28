package org.rudi.facet.apimaccess.api;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.rudi.facet.apimaccess.helper.rest.WebClientHelper;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractManagerAPI {

	protected final WebClient webClient;
	protected final APIManagerProperties apiManagerProperties;

	/**
	 * Constructeur avec WebClient construisant les exceptions à partir d'une {@link APIManagerHttpExceptionFactory}.
	 */
	protected AbstractManagerAPI(
			WebClient.Builder webClientBuilder,
			APIManagerHttpExceptionFactory exceptionFactory,
			APIManagerProperties apiManagerProperties
	) {
		this.webClient = buildWebClient(webClientBuilder, exceptionFactory);
		this.apiManagerProperties = apiManagerProperties;
	}

	private static WebClient buildWebClient(WebClient.Builder webClientBuilder, APIManagerHttpExceptionFactory exceptionFactory) {
		return webClientBuilder
				.filter(WebClientHelper.createFilterFrom(exceptionFactory))
				.build();
	}

	public String getServerUrl() {
		return apiManagerProperties.getServerUrl();
	}

	public String getServerGatewayUrl() {
		return apiManagerProperties.getServerGatewayUrl();
	}

	protected String getAdminContext() {
		return apiManagerProperties.getAdminContext();
	}

	protected String getPublisherContext() {
		return apiManagerProperties.getPublisherContext();
	}

	public String getStoreContext() {
		return apiManagerProperties.getStoreContext();
	}

	public String getAdminRegistrationId() {
		return apiManagerProperties.getAdminRegistrationId();
	}

	public String getAdminUsername() {
		return apiManagerProperties.getAdminUsername();
	}

	public String getAdminPassword() {
		return apiManagerProperties.getAdminPassword();
	}

	public WebClient.RequestBodySpec populateRequestWithRegistrationId(HttpMethod httpMethod, String registrationId, String uri) {
		WebClient.RequestBodySpec requestBodySpec = webClient.method(httpMethod)
				.uri(uri)
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(registrationId));
		if (registrationId.equals(getAdminRegistrationId())) {
			return requestBodySpec.headers(httpHeaders -> httpHeaders.setBasicAuth(getAdminUsername(), getAdminPassword()));
		}
		return requestBodySpec;
	}

	public WebClient.RequestBodySpec populateRequestWithRegistrationId(HttpMethod httpMethod, String registrationId, String uri, Function<UriBuilder, URI> uriFunction) {
		WebClient.RequestBodySpec requestBodySpec = webClient.method(httpMethod)
				.uri(uri, uriFunction)
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(registrationId));
		if (registrationId.equals(getAdminRegistrationId())) {
			return requestBodySpec.headers(httpHeaders -> httpHeaders.setBasicAuth(getAdminUsername(), getAdminPassword()));
		}
		return requestBodySpec;
	}

	public WebClient.RequestBodySpec populateRequestWithRegistrationId(HttpMethod httpMethod, String registrationId, String uri, Map<String, ?> uriVariables) {
		WebClient.RequestBodySpec requestBodySpec = webClient.method(httpMethod)
				.uri(uri, uriVariables)
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(registrationId));
		if (registrationId.equals(getAdminRegistrationId())) {
			return requestBodySpec.headers(httpHeaders -> httpHeaders.setBasicAuth(getAdminUsername(), getAdminPassword()));
		}
		return requestBodySpec;
	}

	public WebClient.RequestBodySpec populateRequestWithAdminRegistrationId(HttpMethod httpMethod, String uri, Function<UriBuilder, URI> uriFunction) {
		return populateRequestWithRegistrationId(httpMethod, getAdminRegistrationId(), uri, uriFunction);
	}

	public WebClient.RequestBodySpec populateRequestWithAdminRegistrationId(HttpMethod httpMethod, String uri, Map<String, ?> uriVariables) {
		return populateRequestWithRegistrationId(httpMethod, getAdminRegistrationId(), uri, uriVariables);
	}

	protected String buildAdminURIPath(String queryPath) {
		return buildURIPath(getServerUrl(), getAdminContext(), queryPath);
	}

	protected String buildPublisherURIPath(String queryPath) {
		return buildURIPath(getServerUrl(), getPublisherContext(), queryPath);
	}

	protected String buildDevPortalURIPath(String queryPath) {
		return buildURIPath(getServerUrl(), getStoreContext(), queryPath);
	}

	public String buildAPIAccessUrl(String context, String version) {
		return getServerGatewayUrl() + context + "/" + version;
	}

	public String buildAPIAccessUrl(String context, String version, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromUriString(getServerGatewayUrl())
				.path(context + "/" + version)
				.queryParams(queryParams)
				.build()
				.toUriString();
	}

	private String buildURIPath(String serverURI, String pathContext, String queryPath) {
		return UriComponentsBuilder.fromUriString(serverURI)
				.path(pathContext)
				.path(queryPath)
				.build()
				.toUriString();
	}
}
