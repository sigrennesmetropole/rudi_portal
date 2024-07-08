/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

	public static final String REGISTRATION_ID = "rudi_module";

	public static final String ACTUATOR_URL = "/actuator/**";

	public static final String[] SB_PERMIT_ALL_URL = {
			// URL public
			"/apigateway/v1/application-information", "/apigateway/v1/healthCheck",
			// OAuth2
			"/oauth/**",
			// swagger ui / openapi
			"favicon.ico", "/apigateway/v3/api-docs/**", "/apigateway/swagger-ui/**", "/apigateway/swagger-ui.html",
			"/apigateway/swagger-resources/**", "/apigateway/webjars/**",
			// configuration ?
			"/configuration/ui", "/configuration/security" };
}
