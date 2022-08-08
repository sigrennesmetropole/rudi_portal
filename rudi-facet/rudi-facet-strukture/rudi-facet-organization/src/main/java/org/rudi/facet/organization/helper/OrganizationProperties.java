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

	private String organizationsPath = "/organizations";
	private String membersPath = "/organizations/{organizationUuid}/members";

}
