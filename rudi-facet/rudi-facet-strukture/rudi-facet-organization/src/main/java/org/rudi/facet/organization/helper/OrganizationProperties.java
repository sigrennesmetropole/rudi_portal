package org.rudi.facet.organization.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.strukture.organization")
@Getter
@Setter
@SuppressWarnings("java:S1075")
public class OrganizationProperties {

	/**
	 * URL de base pour appeler le micro-service Strukture via Load Balancer
	 */
	private String serviceBaseUrl = "lb://RUDI-STRUKTURE/strukture/v1/";

	private String organizationsPath = "/organizations";

	private String membersPath = "/organizations/{organizationUuid}/members";

}
