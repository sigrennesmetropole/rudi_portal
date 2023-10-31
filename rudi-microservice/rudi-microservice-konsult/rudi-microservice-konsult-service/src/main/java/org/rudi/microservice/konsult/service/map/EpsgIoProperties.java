package org.rudi.microservice.konsult.service.map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.epsgio")
@Getter
@Setter
public class EpsgIoProperties {

	/**
	 * URL du web service de EPSG.io
	 */
	private String epsgIoUrl = "https://epsg.io/";
}
