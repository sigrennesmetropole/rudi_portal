package org.rudi.facet.apimaccess.api.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://docs.wso2.com/display/IS510/apidocs/OAuth2-dynamic-client-registration/#!/models#Application">Documentation WSO2</a>
 */
@Data
@Accessors(chain = true)
public class Application implements ClientRegistrationResponse {
	@JsonProperty("client_id")
	private String clientId;
	@JsonProperty("client_secret")
	private String clientSecret;
	@JsonProperty("client_secret_expires_at")
	private String clientSecretExpiresAt;
	@JsonProperty("redirect_uris")
	private List<String> redirectUris = new ArrayList<>();
	@JsonProperty("client_name")
	private String clientName;
}
