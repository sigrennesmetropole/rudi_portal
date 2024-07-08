/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.security.oauth2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class OAuth2TokenData {

	private List<String> scope;

	private boolean active;

	private Long exp;

	private List<String> authorities;

	private String jti;

	@JsonProperty("client_id")
	private String clientId;
}
