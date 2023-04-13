package org.rudi.facet.organization.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
