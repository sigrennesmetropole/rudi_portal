package org.rudi.facet.projekt.helper;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProjektConfiguration extends WebClientConfig {

	public ProjektConfiguration(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

	@Bean
	public WebClient projektWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			ProjektProperties projektProperties) {
		return webClientBuilder.baseUrl(projektProperties.getServiceBaseUrl()).build();
	}

	@Bean
	public WebClient ownerInfoWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			ProjektProperties projektProperties) {
		return webClientBuilder.baseUrl(projektProperties.getServiceBaseUrl()).build();
	}
}
