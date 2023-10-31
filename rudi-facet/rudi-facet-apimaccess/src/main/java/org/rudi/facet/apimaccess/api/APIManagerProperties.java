package org.rudi.facet.apimaccess.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
@SuppressWarnings("java:S1075") // Les URL par défaut des paramètres sont obligatoirement en dur dans le code
public class APIManagerProperties {

	@Value("${apimanager.trust-all-certs:false}")
	private boolean trustAllCerts;

	@Value("${apimanager.base-url}")
	private String baseUrl;

	@Value("${apimanager.port:9443}")
	private int port;

	@Value("${apimanager.api.url}")
	private String serverUrl;

	@Value("${apimanager.gateway.url}")
	private String serverGatewayUrl;

	@Value("${apimanager.api.admin.context:/admin/v1}")
	private String adminContext = "/admin/v1";

	@Value("${apimanager.api.admin.api-categories-path:/api-categories}")
	private String apiCategoriesPath = "/api-categories";

	@Value("${apimanager.api.admin.system-scopes-role-aliases-path:/system-scopes/role-aliases}")
	private String systemScopesRoleAliasesPath = "/system-scopes/role-aliases";

	@Value("${apimanager.api.publisher.context:/publisher/v1}")
	private String publisherContext;

	@Value("${apimanager.api.store.context:/store/v1}")
	private String storeContext;

	@Value("${apimanager.oauth2.client.admin.registration.id}")
	private String adminRegistrationId;

	@Value("${apimanager.oauth2.client.registration-v0.17.path:/client-registration/v0.17/register}")
	private String registrationV017Path;

	@Value("${apimanager.oauth2.client.registration-v1.1.path:/api/identity/oauth2/dcr/v1.1/register}")
	private String registrationV11Path;

	@Value("${apimanager.oauth2.client.admin.username}")
	private String adminUsername = "admin";

	@Value("${apimanager.oauth2.client.admin.password}")
	private String adminPassword;

	@Value("${apimanager.oauth2.client.rudi.username:rudi}")
	private String rudiUsername = "rudi";

	@Value("${apimanager.oauth2.client.rudi.password}")
	private String rudiPassword;

	@Value("${apimanager.oauth2.client.anonymous.username:anonymous}")
	private String anonymousUsername = "anonymous";

	@Value("${apimanager.oauth2.client.anonymous.password:anonymous}")
	private String anonymousPassword = "anonymous";

	public static final class Domains {
		public static final String RUDI = "RUDI";
		private static final String SEPARATOR = "/";

		private Domains() {
		}

		public static String removeDomainFromUsername(final String usernameWithDomain) {
			if (StringUtils.isBlank(usernameWithDomain) || usernameWithDomain.length() < 2) {
				return usernameWithDomain;
			}

			final var indexOfSeparator = usernameWithDomain.indexOf(SEPARATOR);
			if (indexOfSeparator == -1) {
				return usernameWithDomain;
			}

			return usernameWithDomain.substring(indexOfSeparator + 1);
		}

		public static String addDomainToUsername(final String username) {
			if (StringUtils.isBlank(username)) {
				return username;
			}
			if (username.contains(SEPARATOR) && username.startsWith(RUDI)) {
				return username;
			}
			return RUDI + SEPARATOR + username;
		}
	}

	public static final class Roles {
		public static final String INTERNAL_SUBSCRIBER = "Internal/subscriber";
		public static final String RUDI_USER = "RUDI/USER";

		private Roles() {
		}
	}

	public static final class Scopes {
		public static final String INTERNAL_SUBSCRIBER = "apim:subscribe,apim:app_update,apim:app_manage,apim:sub_manage,apim:store_settings,apim_analytics:application_analytics:view,apim:sub_alert_manage";

		private Scopes() {
		}
	}

	public boolean isAdminOrAnonymous(String username) {
		return username != null && (username.equals(adminUsername) || username.equals(anonymousUsername));
	}
}
