package org.rudi.facet.rva;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RvaConfiguration {

	@Bean
	public WebClient rvaWebClient(RvaProperties rvaProperties) {
		return WebClient.builder().baseUrl(rvaProperties.getUrl()).build();
	}
}
