/**
 * 
 */
package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import java.security.Principal;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.facet.selfdata.helper.SelfdataHelper;
import org.rudi.microservice.apigateway.facade.config.gateway.exception.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * https://stackoverflow.com/questions/74451046/spring-cloud-gateway-how-to-programatically-get-the-matched-route-information
 * 
 * 
 * @author FNI18300
 *
 */
@Slf4j
public class SelfdataGlobalFilter extends AbstractMetadataGlobalFilter {

	private static final String HEADER_SELFDATA_TOKEN = "X-SELFDATA-TOKEN";

	public SelfdataGlobalFilter(DatasetService datasetService, ProjektHelper projektHelper, ACLHelper aclHelper,
			SelfdataHelper selfdataHelper, MetadataDetailsHelper metadataDetailsHelper) {
		super(datasetService, projektHelper, aclHelper, selfdataHelper, metadataDetailsHelper);
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// controle de la validité de l'url (est-ce bien une url d'accès à un media ?
		if (accept(exchange)) {
			// exctraction du globalId et mediaId
			try {
				Pair<UUID, UUID> datasetIdentifiers = extractDatasetIdentifiers(exchange);
				// controle d'accès ...
				Pair<Boolean, Boolean> access = extractAccess(exchange, datasetIdentifiers);
				if (Boolean.FALSE.equals(access.getRight())) {
					// pas selfdatz
					return chain.filter(exchange);
				} else {
					return onSelfdata(exchange, chain, datasetIdentifiers);
				}
			} catch (Exception e) {
				// rejet de l'accès
				onFilterError(exchange, e);
				return exchange.getResponse().setComplete();
			}
		} else {
			return chain.filter(exchange);
		}
	}

	private Mono<Void> onSelfdata(ServerWebExchange exchange, GatewayFilterChain chain,
			Pair<UUID, UUID> datasetIdentifiers) {
		log.info("try to add token to selfdata for {}", datasetIdentifiers);
		// on est sur un jdd selfdata
		return exchange.getPrincipal().map(Principal::getName).onErrorStop()
				.flatMap(login -> getSelfdataHelper().getMatchingToken(datasetIdentifiers.getLeft(), login))
				.flatMap(a -> chain(a, exchange, chain, "(S=" + datasetIdentifiers.getRight() + ")")).then()
				.doOnError(e -> onFinalError(e, exchange, chain, ""));

	}

	protected Mono<Void> chain(UUID token, ServerWebExchange exchange, GatewayFilterChain chain, String origin) {
		if (token == null) {
			onFilterError(exchange,
					new UnauthorizedException("Not authorized " + origin + " " + exchange.getPrincipal().toString()));
			return exchange.getResponse().setComplete();
		} else {
			log.info("Add selfdata header");
			ServerHttpRequest request = exchange.getRequest().mutate()
					.headers(i -> i.add(HEADER_SELFDATA_TOKEN, token.toString())).build();

			return chain.filter(exchange.mutate().request(request).build());
		}
	}

	@Override
	public int getOrder() {
		return 1;
	}

}
