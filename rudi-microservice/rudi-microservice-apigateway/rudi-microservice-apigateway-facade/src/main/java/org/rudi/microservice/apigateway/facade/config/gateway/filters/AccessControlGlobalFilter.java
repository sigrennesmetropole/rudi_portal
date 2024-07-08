/**
 * 
 */
package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import java.security.Principal;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.rudi.facet.acl.bean.ProjectKeystore;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserPageResult;
import org.rudi.facet.acl.bean.UserType;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.ProjectKeystoreSearchCriteria;
import org.rudi.facet.acl.helper.UserSearchCriteria;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.facet.selfdata.helper.SelfdataHelper;
import org.rudi.microservice.apigateway.facade.config.gateway.exception.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.domain.Pageable;
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
				.flatMap(this::extractUser).switchIfEmpty(Mono.error(new UnauthorizedException("Invalid user (R)")))
				.flatMap(user -> hasAccessToDataset(user, datasetIdentifiers))
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

	protected Mono<User> extractUser(UserPageResult userPageResult) {
		if (userPageResult != null && CollectionUtils.isNotEmpty(userPageResult.getElements())) {
			return Mono.just(userPageResult.getElements().get(0));
		} else {
			return Mono.empty();
		}
	}

	protected Mono<Boolean> hasAccessToDataset(User user, Pair<UUID, UUID> datasetIdentifiers) {
		Mono<Boolean> result = null;

		if (user.getType() == UserType.API) {
			ProjectKeystoreSearchCriteria searchCriteria = ProjectKeystoreSearchCriteria.builder()
					.clientId(user.getLogin()).build();
			// on essaye de récupérer le keystore (donc l'uuid du projet)
			result = getAclHelper().searchMonoProjectKeystores(searchCriteria, Pageable.ofSize(1)).map(keystores -> {
				if (CollectionUtils.isNotEmpty(keystores.getElements())) {
					return keystores.getElements().get(0);
				} else {
					return null;
				}
			}).switchIfEmpty(Mono.error(new UnauthorizedException("Invalid user (R)")))
					.map(ProjectKeystore::getProjectUuid).flatMap(projectUuid -> getProjektHelper()
							.hasMonoAccessToDataset(projectUuid, datasetIdentifiers.getLeft()));

		} else {
			result = getProjektHelper().hasMonoAccessToDataset(user.getUuid(), datasetIdentifiers.getLeft());
		}
		return result;
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
