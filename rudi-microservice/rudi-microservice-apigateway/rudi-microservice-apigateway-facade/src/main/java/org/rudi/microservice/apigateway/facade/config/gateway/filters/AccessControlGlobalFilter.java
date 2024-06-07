/**
 * 
 */
package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import java.security.Principal;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.UserSearchCriteria;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.facet.selfdata.helper.SelfdataHelper;
import org.rudi.microservice.apigateway.facade.config.gateway.exception.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * https://stackoverflow.com/questions/74451046/spring-cloud-gateway-how-to-programatically-get-the-matched-route-information
 * 
 * 
 * @author FNI18300
 *
 */
public class AccessControlGlobalFilter extends AbstractMetadataGlobalFilter {

	public AccessControlGlobalFilter(DatasetService datasetService, ProjektHelper projektHelper, ACLHelper aclHelper,
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
				if (Boolean.FALSE.equals(access.getLeft()) && Boolean.FALSE.equals(access.getRight())) {
					return chain.filter(exchange);
				} else if (Boolean.TRUE.equals(access.getLeft()) && Boolean.FALSE.equals(access.getRight())) {
					return onRestricted(exchange, chain, datasetIdentifiers);
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

	protected Mono<Void> onRestricted(ServerWebExchange exchange, GatewayFilterChain chain,
			Pair<UUID, UUID> datasetIdentifiers) {
		// on est sur un jdd restreint
		return exchange.getPrincipal().map(Principal::getName).onErrorStop()
				.flatMap(login -> getAclHelper().getMonoUsers(UserSearchCriteria.builder().login(login).build()))
				.map(this::mapUserToUuid).switchIfEmpty(Mono.error(new UnauthorizedException("Invalid user (R)")))
				.flatMap(userUuid -> getProjektHelper().hasMonoAccessToDataset(userUuid, datasetIdentifiers.getLeft()))
				.flatMap(a -> chain(a, exchange, chain, "(R)")).then()
				.doOnError(e -> onFinalError(e, exchange, chain, ""));
	}

	protected Mono<Void> onSelfdata(ServerWebExchange exchange, GatewayFilterChain chain,
			Pair<UUID, UUID> datasetIdentifiers) {
		// on est sur un jdd selfdata
		return exchange.getPrincipal().map(Principal::getName).onErrorStop()
				.flatMap(login -> getAclHelper().getMonoUsers(UserSearchCriteria.builder().login(login).build()))
				.map(this::mapUserToUuid).switchIfEmpty(Mono.error(new UnauthorizedException("Invalid user (S)")))
				.flatMap(userUuid -> getSelfdataHelper().hasMonoMatchingToDataset(userUuid,
						datasetIdentifiers.getLeft()))
				.flatMap(a -> chain(a, exchange, chain, "(S)")).then()
				.doOnError(e -> onFinalError(e, exchange, chain, ""));
	}

	protected Mono<Void> chain(Boolean access, ServerWebExchange exchange, GatewayFilterChain chain, String origin) {
		if (Boolean.FALSE.equals(access)) {
			onFilterError(exchange,
					new UnauthorizedException("Not authorized " + origin + " " + exchange.getPrincipal().toString()));
			return exchange.getResponse().setComplete();
		} else {
			return chain.filter(exchange);
		}
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
