package org.rudi.facet.apimaccess.api;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractManagerAPI {

    protected final WebClient webClient;
    private final ManagerAPIProperties managerAPIProperties;

    public AbstractManagerAPI(
            WebClient webClient,
            ManagerAPIProperties managerAPIProperties
    ) {
        this.webClient = webClient;
        this.managerAPIProperties = managerAPIProperties;
    }

    public String getServerUrl() {
        return managerAPIProperties.getServerUrl();
    }

    public String getServerGatewayUrl() {
        return managerAPIProperties.getServerGatewayUrl();
    }

    public String getPublisherContext() {
        return managerAPIProperties.getPublisherContext();
    }

    public String getStoreContext() {
        return managerAPIProperties.getStoreContext();
    }

    public String getAdminRegistrationId() {
        return managerAPIProperties.getAdminRegistrationId();
    }

    public String getAdminUsername() {
        return managerAPIProperties.getAdminUsername();
    }

    public String getAdminPassword() {
        return managerAPIProperties.getAdminPassword();
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

    public String buildPublisherURIPath(String queryPath) {
        return buildURIPath(getServerUrl(), getPublisherContext(), queryPath);
    }

    public String buildDevPortalURIPath(String queryPath) {
        return buildURIPath(getServerUrl(), getStoreContext(), queryPath);
    }

    public String buildAPIAccessUrl(String context, String version) {
        return getServerGatewayUrl() + context + "/" + version;
    }

    private String buildURIPath(String serverURI, String pathContext, String queryPath) {
        return UriComponentsBuilder.fromUriString(serverURI)
                .path(pathContext)
                .path(queryPath)
                .build()
                .toUriString();
    }
}
