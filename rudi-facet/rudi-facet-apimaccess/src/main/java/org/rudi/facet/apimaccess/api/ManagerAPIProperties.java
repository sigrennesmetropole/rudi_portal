package org.rudi.facet.apimaccess.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManagerAPIProperties {
	@Value("${apimanager.api.url}")
	private String serverUrl;
	@Value("${apimanager.gateway.url}")
	private String serverGatewayUrl;
	@Value("${apimanager.api.publisher.context:/publisher/v1}")
	private String publisherContext;
	@Value("${apimanager.api.store.context:/store/v1}")
	private String storeContext;
	@Value("${apimanager.oauth2.client.admin.registration.id}")
	private String adminRegistrationId;
	@Value("${apimanager.oauth2.client.admin.username}")
	private String adminUsername;
	@Value("${apimanager.oauth2.client.admin.password}")
	private String adminPassword;

	public String getServerUrl() {
		return serverUrl;
	}

	public String getServerGatewayUrl() {
		return serverGatewayUrl;
	}

	public String getPublisherContext() {
		return publisherContext;
	}

	public String getStoreContext() {
		return storeContext;
	}

	public String getAdminRegistrationId() {
		return adminRegistrationId;
	}

	public String getAdminUsername() {
		return adminUsername;
	}

	public String getAdminPassword() {
		return adminPassword;
	}
}
