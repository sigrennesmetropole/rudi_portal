package org.rudi.facet.apimaccess.api.registration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * Adaptation pour la V3.2.0 du DTO WSO2 <a href=
 * "https://github.com/wso2-extensions/identity-inbound-auth-oauth/blob/v6.2.28/components/org.wso2.carbon.identity.oauth.dcr.endpoint/src/gen/java/org/wso2/carbon/identity/oauth2/dcr/endpoint/dto/RegistrationRequestDTO.java">RegistrationRequestDTO</a>
 */
@Builder
@Data
public class RegistrationRequestV11 implements RegistrationRequest {
	public static final String CLIENT_NAME = "client_name";
	@JsonProperty(CLIENT_NAME)
	private String clientName;
	@JsonProperty("grant_types")
	private List<String> grantTypes;
	@JsonProperty("ext_param_client_id")
	private String clientId;
	@JsonProperty("ext_param_client_secret")
	private String clientSecret;
	@JsonProperty("redirect_uris")
	private List<String> redirectUris;

}
