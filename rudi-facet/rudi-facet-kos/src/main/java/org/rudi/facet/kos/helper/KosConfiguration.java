package org.rudi.facet.kos.helper;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KosConfiguration extends WebClientConfig {

	public KosConfiguration(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

	@Bean
	public WebClient kosWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			KosProperties projektProperties) {
		return webClientBuilder.baseUrl(projektProperties.getServiceBaseUrl()).build();
	}
}
