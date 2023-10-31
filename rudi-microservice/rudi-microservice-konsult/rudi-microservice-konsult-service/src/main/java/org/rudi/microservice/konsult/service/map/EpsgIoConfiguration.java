package org.rudi.microservice.konsult.service.map;

import org.rudi.facet.rva.RvaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EpsgIoConfiguration {

	@Bean
	public WebClient epsgIoWebClient(EpsgIoProperties epsgIoProperties) {
		return WebClient.builder()
				.baseUrl(epsgIoProperties.getEpsgIoUrl())
				.build();
	}
}
