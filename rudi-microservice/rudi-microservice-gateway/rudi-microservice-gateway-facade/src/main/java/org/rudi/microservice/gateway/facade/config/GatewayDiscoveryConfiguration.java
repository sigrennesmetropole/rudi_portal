package org.rudi.microservice.gateway.facade.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author FNI18300
 *
 */
@Configuration
public class GatewayDiscoveryConfiguration {

	@Bean
	public GlobalFilter defaultFilters() {
		return new CustomGlobalFilter();
	}
}
