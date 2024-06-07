/**
 * 
 */
package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import org.rudi.microservice.apigateway.facade.config.gateway.ApiGatewayConstants;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;

/**
 * https://stackoverflow.com/questions/74451046/spring-cloud-gateway-how-to-programatically-get-the-matched-route-information
 * 
 * 
 * @author FNI18300
 *
 */
public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered {

	protected String formatMessage(ServerWebExchange exchange) {
		return exchange.getLogPrefix() + " " + exchange.getRequest().getMethodValue() + " "
				+ exchange.getRequest().getURI();
	}

	/**
	 * 
	 * @param exchange le webexchange en cours
	 * @return le login du user
	 */
	protected String getAuthenticatedUser(ServerWebExchange exchange) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			return authentication.getPrincipal().toString();
		} else {
			return ApiGatewayConstants.APIGATEWAY_UNKNOWN_LOGIN;
		}
	}
}
