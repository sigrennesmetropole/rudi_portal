package org.rudi.facet.organization.helper;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OrganizationConfiguration extends WebClientConfig {

	public OrganizationConfiguration(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

	@Bean
	public WebClient organizationWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			OrganizationProperties organizationProperties) {
		return webClientBuilder.baseUrl(organizationProperties.getServiceBaseUrl()).build();
	}
}
