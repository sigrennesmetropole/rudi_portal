package org.rudi.facet.cms.impl.configuration;

import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.cms.CmsConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class CmsMagnoliaWebClientConfiguration {

	private final HttpClientHelper httpClientHelper;

	@Bean(name = BeanIds.CMS_HTTP_CLIENT)
	public HttpClient httpClient() throws SSLException {
		return httpClientHelper.createReactorHttpClient(true, false, false);
	}

	@Bean(name = BeanIds.CMS_WEB_CLIENT)
	public WebClient magnoliaWebClient(CmsConfiguration cmsConfiguration,
			@Qualifier(BeanIds.CMS_HTTP_CLIENT) HttpClient httpClient) {
		WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(cmsConfiguration.getUrl());
		if (cmsConfiguration.isAuthentification()) {
			webClientBuilder.defaultHeaders(
					header -> header.setBasicAuth(cmsConfiguration.getUser(), cmsConfiguration.getPassword()));
		}
		webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
		return webClientBuilder.build();
	}
}
