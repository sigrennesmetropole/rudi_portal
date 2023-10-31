package org.rudi.facet.selfdata.helper;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SelfdataConfiguration extends WebClientConfig {

	public SelfdataConfiguration(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

	@Bean
	public WebClient selfdataWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			SelfdataProperties informationRequestProperties) {
		return webClientBuilder.baseUrl(informationRequestProperties.getBaseUrl()).build();
	}
}