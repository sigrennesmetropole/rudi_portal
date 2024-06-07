package org.rudi.microservice.apigateway.facade.config.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Utilisé à la place de {@link org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint}
 * car ce dernier fait apparaître des popin à chaque erreur 401 (cf RUDI-1626).
 *
 * <p>
 * Avec cette classe, on indique plutôt à l'utilisateur qu'il doit s'authentifier en mode "Bearer" plutôt qu'en "Basic".
 * Dans ce cas le navigateur n'affiche pas de popin.
 * </p>
 *
 * @see org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
 */
public class HttpBearerServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
	private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	private static final String HEADER_VALUE = "Bearer";

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
		return Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().set(WWW_AUTHENTICATE, HEADER_VALUE);
		});
	}
}
