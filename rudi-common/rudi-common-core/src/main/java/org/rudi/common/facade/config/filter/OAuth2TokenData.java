/**
 * RUDI Portail
 */
package org.rudi.common.facade.config.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author FNI18300
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
