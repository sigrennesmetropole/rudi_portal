/**
 * 
 */
package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserPageResult;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.facet.selfdata.helper.SelfdataHelper;
import org.rudi.microservice.apigateway.facade.config.gateway.ApiGatewayConstants;
import org.rudi.microservice.apigateway.facade.config.gateway.exception.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
public abstract class AbstractMetadataGlobalFilter extends AbstractGlobalFilter {

	@Getter(value = AccessLevel.PROTECTED)
	private final DatasetService datasetService;

	@Getter(value = AccessLevel.PROTECTED)
	private final ProjektHelper projektHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final ACLHelper aclHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final SelfdataHelper selfdataHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final MetadataDetailsHelper metadataDetailsHelper;

	/**
	 * 
	 * @param e        l'exception
	 * @param exchange le web exchange
	 * @param chain    la chaine
	 * @param message  le message
	 * @return
	 */
	protected Mono<Void> onFinalError(Throwable e, ServerWebExchange exchange, GatewayFilterChain chain,
			String message) {
		onFilterError(exchange, new UnauthorizedException(message + " " + (e != null ? e.getMessage() : "n/a")));
		return exchange.getResponse().setComplete();
	}

	protected UUID mapUserToUuid(UserPageResult pageResult) {
		if (pageResult.getTotal() > 0) {
			User user = pageResult.getElements().get(0);
			return user.getUuid();
		} else {
			return null;
		}
	}

	protected String mapUserToLogin(UserPageResult pageResult) {
		if (pageResult.getTotal() > 0) {
			User user = pageResult.getElements().get(0);
			return user.getLogin();
		} else {
			return null;
		}
	}

	protected void onFilterError(ServerWebExchange exchange, Exception e) {
		log.error("Fraudulent acces to " + formatMessage(exchange), e);
		if (e instanceof UnauthorizedException) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		} else {
			exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (!exchange.getResponse().isCommitted()) {
			exchange.getResponse().getHeaders().add(ApiGatewayConstants.APIGATEWAY_ERROR_HEADER_NAME,
					"Not allowed to access to this Media");
		}
	}

	protected boolean accept(ServerWebExchange exchange) {
		return exchange.getRequest().getPath().toString().startsWith(ApiGatewayConstants.APIGATEWAY_DATASETS_PATH);
	}

	protected Pair<UUID, UUID> extractDatasetIdentifiers(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		UUID globalId = UUID.fromString(request.getPath()
				.subPath(ApiGatewayConstants.GLOBAL_ID_INDEX, ApiGatewayConstants.GLOBAL_ID_INDEX + 1).toString());
		UUID mediaId = UUID.fromString(request.getPath()
				.subPath(ApiGatewayConstants.MEDIA_ID_INDEX, ApiGatewayConstants.MEDIA_ID_INDEX + 1).toString());
		return Pair.of(globalId, mediaId);
	}

	/**
	 * 
	 * @param exchange           le flux d'échange
	 * @param datasetIdentifiers les identifiants
	 * @return les niveaux d'accès
	 * @throws DataverseAPIException
	 * @throws UnauthorizedException
	 */
	protected Pair<Boolean, Boolean> extractAccess(ServerWebExchange exchange, Pair<UUID, UUID> datasetIdentifiers)
			throws DataverseAPIException, UnauthorizedException {
		Metadata metadata = datasetService.getDataset(datasetIdentifiers.getLeft());
		boolean isRestricted = metadataDetailsHelper.isRestricted(metadata);
		boolean isSelfDdata = metadataDetailsHelper.isSelfdata(metadata);
		long hasMedia = metadata.getAvailableFormats().stream().map(Media::getMediaId)
				.filter(mediaId -> mediaId.equals(datasetIdentifiers.getRight())).count();

		if (hasMedia == 0) {
			// le media n'est associé au jdd
			throw new UnauthorizedException("Incoherent call");
		}
		return Pair.of(isRestricted, isSelfDdata);
	}

}
