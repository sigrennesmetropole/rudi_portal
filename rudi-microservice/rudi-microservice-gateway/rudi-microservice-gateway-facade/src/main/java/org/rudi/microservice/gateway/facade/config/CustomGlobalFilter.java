/**
 * 
 */
package org.rudi.microservice.gateway.facade.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author FNI18300
 *
 */
public class CustomGlobalFilter implements GlobalFilter, Ordered {

	public CustomGlobalFilter() {
		super();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
