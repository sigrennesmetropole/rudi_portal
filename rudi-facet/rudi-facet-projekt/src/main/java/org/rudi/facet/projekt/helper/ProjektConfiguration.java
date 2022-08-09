package org.rudi.facet.projekt.helper;

import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProjektConfiguration extends WebClientConfig {
	@Bean
	public WebClient projektWebClient(
			@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			ProjektProperties projektProperties
	) {
		return webClientBuilder.baseUrl(projektProperties.getServiceBaseUrl()).build();
	}
}
