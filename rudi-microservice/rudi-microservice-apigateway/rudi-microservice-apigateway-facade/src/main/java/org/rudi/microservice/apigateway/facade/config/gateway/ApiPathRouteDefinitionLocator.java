/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.gateway;

import static java.util.Collections.synchronizedMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.service.api.ApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * https://kambei.dev/blog/it/2022-12-27-centralized-swagger-with-spring-cloud-gateway/
 * https://stackoverflow.com/questions/62845550/swagger-api-documentation-in-spring-api-gateway
 * 
 * @author FNI18300
 * @see https://medium.com/bliblidotcom-techblog/spring-cloud-gateway-dynamic-routes-from-database-dc938c6665de
 * @see InMemoryRouteDefinitionRepository
 * @see PropertiesRouteDefinitionLocator
 */
@Slf4j
public class ApiPathRouteDefinitionLocator extends InMemoryRouteDefinitionRepository {

	private static final List<String> SCHEMES = List.of("https", "http", "ftp", "ftps");

	@Value("${rudi.apigateway.routs.page-size:50}")
	private int pageSize;

	private final ApiService apiService;

	private final Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<String, RouteDefinition>());

	public ApiPathRouteDefinitionLocator(ApiService apiService) {
		super();
		this.apiService = apiService;
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		if (routes.isEmpty()) {
			ApiSearchCriteria searchCriteria = new ApiSearchCriteria();
			int offset = 0;
			Page<Api> apis = null;
			do {
				apis = apiService.searchApis(searchCriteria,
						PageRequest.of(offset / pageSize, pageSize, Sort.by(Sort.Direction.ASC, "id")));
				if (apis != null && apis.hasContent()) {
					apis.forEach(api -> {
						try {
							addRouteDefinition(routes, api);
						} catch (URISyntaxException e) {
							log.warn("Skip api " + api, e);
						}
					});
				}
				offset += pageSize;
			} while (apis != null && apis.hasContent());
		}
		Map<String, RouteDefinition> routesSafeCopy = new LinkedHashMap<>(routes);
		return Flux.fromIterable(routesSafeCopy.values());
	}

	private void addRouteDefinition(Map<String, RouteDefinition> routes, Api api) throws URISyntaxException {
		RouteDefinition routeDefinition = new RouteDefinition();
		routeDefinition.setId(api.getUuid().toString());
		routeDefinition.setUri(new URI(api.getUrl()));

		if (!isValidUri(routeDefinition.getUri())) {
			return;
		}

		List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
		String path = buildPath(api);
		predicateDefinitions.add(new PredicateDefinition("Path=" + path));
		if (CollectionUtils.isNotEmpty(api.getMethods())) {
			api.getMethods().forEach(method -> predicateDefinitions.add(new PredicateDefinition("Method=" + method)));
		}
		routeDefinition.setPredicates(predicateDefinitions);

		List<FilterDefinition> filters = new ArrayList<>();
		// on enlève le chemin par défaut de rudi
		filters.add(new FilterDefinition("StripPrefix=" + computeStrip(path)));
		// et on réécrit le header d'authentification car l'irisa n'aime pas
		filters.add(new FilterDefinition("MapRequestHeader=Authorization,X-Authorization"));
		filters.add(new FilterDefinition("RemoveRequestHeader=Authorization"));
		routeDefinition.setFilters(filters);

		routes.put(routeDefinition.getId(), routeDefinition);
	}

	private boolean isValidUri(URI uri) {
		return StringUtils.isNotEmpty(uri.getScheme()) && SCHEMES.contains(uri.getScheme());
	}

	private int computeStrip(String path) {
		int partCount = path.split("/").length;
		return partCount > 1 ? partCount - 1 : 0;
	}

	private String buildPath(Api api) {
		return ApiGatewayConstants.APIGATEWAY_DATASETS_PATH + api.getGlobalId() + "/" + api.getMediaId() + "/"
				+ api.getContract();
	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return route.flatMap(r -> {
			if (ObjectUtils.isEmpty(r.getId())) {
				return Mono.error(new IllegalArgumentException("id may not be empty"));
			}
			routes.put(r.getId(), r);
			return Mono.empty();
		});
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			if (routes.containsKey(id)) {
				routes.remove(id);
				return Mono.empty();
			}
			return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
		});
	}

	public void reset() {
		log.debug("Reset apis...");
		routes.clear();
		getRouteDefinitions();
		log.debug("Reset apis done.");
	}

	public void publish(UUID apiUuid) {
		log.debug("Publish api {}", apiUuid);
		unpublish(apiUuid);
		try {
			Api api = apiService.getApi(apiUuid);
			addRouteDefinition(routes, api);
		} catch (Exception e) {
			log.warn("Failed to publish", e);
		}
		log.debug("Publish api {} done.", apiUuid);
	}

	public void unpublish(UUID apiUuid) {
		log.debug("unpublish api {}", apiUuid);
		if (apiUuid != null) {
			RouteDefinition routeDefinition = routes.remove(apiUuid.toString());
			if (routeDefinition == null) {
				log.warn("Try to unpublish missing api {}", apiUuid);
			}
		}
		log.debug("unpublish api {} done.", apiUuid);
	}

}
