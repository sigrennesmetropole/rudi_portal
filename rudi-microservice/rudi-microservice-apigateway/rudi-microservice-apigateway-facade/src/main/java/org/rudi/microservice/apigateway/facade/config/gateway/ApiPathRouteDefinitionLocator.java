/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.gateway;

import static java.util.Collections.synchronizedMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiMethod;
import org.rudi.microservice.apigateway.core.bean.ApiParameter;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.facade.config.gateway.filters.DatasetDecryptGatewayFilterFactory;
import org.rudi.microservice.apigateway.service.api.ApiService;
import org.rudi.microservice.apigateway.service.encryption.EncryptionService;
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

	@Value("${rudi.apigateway.routes.page-size:50}")
	private int pageSize;

	@Value("${rudi.apigateway.encryption.key-urls:http[s]?://.*/(konsult|apigateway)/v1/encryption-key.*}")
	private String encryptionKeyUrlPattern;

	private final ApiService apiService;

	private final EncryptionService encryptionService;

	private final Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<String, RouteDefinition>());

	public ApiPathRouteDefinitionLocator(ApiService apiService, EncryptionService encryptionService) {
		super();
		this.apiService = apiService;
		this.encryptionService = encryptionService;
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

	protected void addRouteDefinition(Map<String, RouteDefinition> routes, Api api) throws URISyntaxException {
		RouteDefinition routeDefinition = new RouteDefinition();
		routeDefinition.setId(api.getUuid().toString());
		routeDefinition.setUri(new URI(api.getUrl()));

		if (!isValidUri(routeDefinition.getUri())) {
			// reject uri
			log.warn("Reject invalid uri {}", routeDefinition.getUri());
			return;
		}

		String path = preparePredicate(routeDefinition, api);

		List<FilterDefinition> filters = new ArrayList<>();
		handleDefaultFilters(filters, path);

		if (api.getParameters().stream()
				.anyMatch(e -> e.getName().equals(DatasetDecryptGatewayFilterFactory.ENCRYPTED_PROPERTY))) {
			handleCryptedDatasetFilter(filters, api);
		}
		routeDefinition.setFilters(filters);

		routes.put(routeDefinition.getId(), routeDefinition);
	}

	protected void handleDefaultFilters(List<FilterDefinition> filters, String path) {
		// on enlève le chemin par défaut de rudi
		filters.add(new FilterDefinition("StripPrefix=" + computeStrip(path)));
		// et on réécrit le header d'authentification car l'irisa n'aime pas
		filters.add(new FilterDefinition("MapRequestHeader=Authorization,X-Authorization"));
		filters.add(new FilterDefinition("RemoveRequestHeader=Authorization"));
	}

	protected String preparePredicate(RouteDefinition routeDefinition, Api api) {
		List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
		String path = buildPath(api);
		predicateDefinitions.add(new PredicateDefinition("Path=" + path));
		if (CollectionUtils.isNotEmpty(api.getMethods())) {
			String methodValues = api.getMethods().stream().map(ApiMethod::name).collect(Collectors.joining(","));
			predicateDefinitions.add(new PredicateDefinition("Method=" + methodValues));
		}
		routeDefinition.setPredicates(predicateDefinitions);
		return path;
	}

	protected void handleCryptedDatasetFilter(List<FilterDefinition> filters, Api api) {
		boolean handle = false;
		// cibler les paramètres (encrypted et nsplus)
		Optional<ApiParameter> publicKeyUrlApiParameter = api.getParameters().stream()
				.filter(e -> e.getName().equals(DatasetDecryptGatewayFilterFactory.PUBLIC_KEY_URL_PROPERTY))
				.findFirst();
		if (publicKeyUrlApiParameter.isPresent()
				&& publicKeyUrlApiParameter.get().getValue().matches(encryptionKeyUrlPattern)) {
			log.info("Accept crypted dataset with url {}", publicKeyUrlApiParameter.get().getValue());
			handle = true;
		}

		// ce paramètre est pour gérer les aspects historique de rudi (d'où le camel case ou snake case...)
		Optional<ApiParameter> publicKeyPartialContentApiParameter = api.getParameters().stream()
				.filter(e -> e.getName().equals(DatasetDecryptGatewayFilterFactory.PUBLIC_KEY_PARTIAL_CONTENT_PROPERTY)
						|| e.getName().equals(
								DatasetDecryptGatewayFilterFactory.PUBLIC_KEY_PARTIAL_CONTENT_CAMEL_CASE_PROPERTY))
				.findFirst();
		if (!handle && publicKeyPartialContentApiParameter.isPresent()) {
			try {
				PublicKey defaultPublicKey = encryptionService.getPublicEncryptionKey(null);
				byte[] publicKeyByte = defaultPublicKey.getEncoded();
				// Base64 encoded string
				String publicKeyString = Base64.getEncoder().encodeToString(publicKeyByte);
				if (publicKeyString.startsWith(publicKeyPartialContentApiParameter.get().getValue())) {
					log.info("Accept crypted dataset with key cut");
					handle = true;
				}
			} catch (Exception e) {
				log.warn("Failed to get public key to check key cut", e);
			}
		}

		if (handle) {
			log.info("Crypted dataset {} use portal key", api.getApiId());
			// ajout du type mime dans le context
			String newMimeType = api.getParameters().stream()
					.filter(e -> e.getName().equals(DatasetDecryptGatewayFilterFactory.MIME_TYPE_PROPERTY)).findFirst()
					.map(ApiParameter::getValue).orElse("");
			filters.add(new FilterDefinition(String.format("DatasetDecrypt=%s", newMimeType)));
		} else {
			log.info("Crypted dataset {} does not use portal key", api.getApiId());
		}
	}

	protected boolean isValidUri(URI uri) {
		return StringUtils.isNotEmpty(uri.getScheme()) && SCHEMES.contains(uri.getScheme());
	}

	protected int computeStrip(String path) {
		int partCount = path.split("/").length;
		return partCount > 1 ? partCount - 1 : 0;
	}

	protected String buildPath(Api api) {
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
