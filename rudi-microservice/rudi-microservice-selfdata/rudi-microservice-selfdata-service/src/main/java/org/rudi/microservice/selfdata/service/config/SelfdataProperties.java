package org.rudi.microservice.selfdata.service.config;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "selfdata")
@Getter
@Setter
public class SelfdataProperties {
	/**
	 * UUID du JDD déchets.
	 */
	private UUID wasteDatasetUuid;
}
