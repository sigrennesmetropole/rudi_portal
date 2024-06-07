package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import java.net.URI;
import java.util.regex.Pattern;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class RerouteToRequestUrlFilter implements GlobalFilter, Ordered {

	public static final int ROUTE_TO_URL_FILTER_ORDER = 90000;

	private static final String SCHEME_REGEX = "[a-zA-Z]([a-zA-Z]|\\d|\\+|\\.|-)*:.*";

	static final Pattern schemePattern = Pattern.compile(SCHEME_REGEX);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		if (route == null) {
			return chain.filter(exchange);
		}
		log.trace("RerouteToRequestUrlFilter start");
		URI uri = exchange.getRequest().getURI();
		boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
		URI routeUri = route.getUri();

		if (hasAnotherScheme(routeUri)) {
			// this is a special url, save scheme to special attribute
			// replace routeUri with schemeSpecificPart
			exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR, routeUri.getScheme());
			routeUri = URI.create(routeUri.getSchemeSpecificPart());
		}

		if ("lb".equalsIgnoreCase(routeUri.getScheme()) && routeUri.getHost() == null) {
			// Load balanced URIs should always have a host. If the host is null it is
			// most
			// likely because the host name was invalid (for example included an
			// underscore)
			throw new IllegalStateException("Invalid host: " + routeUri.toString());
		}

		URI mergedUrl = UriComponentsBuilder.fromUri(uri)
				// .uri(routeUri)
				.scheme(routeUri.getScheme()).host(routeUri.getHost()).port(routeUri.getPort()).path(routeUri.getPath())
				.build(encoded).toUri();
		exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, mergedUrl);
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return ROUTE_TO_URL_FILTER_ORDER;
	}

	static boolean hasAnotherScheme(URI uri) {
		return schemePattern.matcher(uri.getSchemeSpecificPart()).matches() && uri.getHost() == null
				&& uri.getRawPath() == null;
	}

}
