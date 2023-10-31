package org.rudi.facet.strukture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StruktureConfiguration {

	@Bean
	public WebClient struktureWebClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder webClientBuilder,
			StruktureProperties struktureProperties) {
		return webClientBuilder.baseUrl(struktureProperties.getServiceBaseUrl()).build();
	}

}
