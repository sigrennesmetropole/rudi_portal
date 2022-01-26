package org.rudi.facet.apimaccess.helper.rest;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_WEBCLIENT;

@Component
public class WebClientHelper {

    @Autowired
    private CustomClientRegistrationRepository customClientRegistrationRepository;

    @SneakyThrows
    @Bean(name = API_MACCESS_WEBCLIENT)
    WebClient webClient() {
        ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(customClientRegistrationRepository);

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        ClientCredentialsReactiveOAuth2AuthorizedClientProvider clientCredentialsReactiveOAuth2AuthorizedClientProvider =
                new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();
        WebClientReactiveClientCredentialsTokenResponseClient webClientReactiveClientCredentialsTokenResponseClient =
                new WebClientReactiveClientCredentialsTokenResponseClient();
        webClientReactiveClientCredentialsTokenResponseClient.setWebClient(
                WebClient.builder()
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .build());
        clientCredentialsReactiveOAuth2AuthorizedClientProvider.setAccessTokenResponseClient(webClientReactiveClientCredentialsTokenResponseClient);

        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                customClientRegistrationRepository, reactiveOAuth2AuthorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(clientCredentialsReactiveOAuth2AuthorizedClientProvider);

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager);

        return WebClient.builder().filter(oauthFilter)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
