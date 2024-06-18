package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

@Data
public class FooterDescriptionData {
	private String logo;
	private List<SocialNetworkData> socialNetworks;
}

