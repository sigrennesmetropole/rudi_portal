package org.rudi.common.facade.config.json;

import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class DefaultJacksonConfig {
	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return new DefaultJackson2ObjectMapperBuilder();
	}
}
