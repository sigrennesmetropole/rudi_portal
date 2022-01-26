/**
 * RUDI Portail
 */
package org.rudi.microservice.gateway.facade.config;

import java.util.List;

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

	private String clientId;
}
