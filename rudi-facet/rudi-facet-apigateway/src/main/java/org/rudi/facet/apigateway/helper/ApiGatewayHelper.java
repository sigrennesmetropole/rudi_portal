package org.rudi.facet.apigateway.helper;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.util.MonoUtils;
import org.rudi.facet.apigateway.exceptions.CreateApiException;
import org.rudi.facet.apigateway.exceptions.DeleteApiException;
import org.rudi.facet.apigateway.exceptions.GetApiException;
import org.rudi.facet.apigateway.exceptions.SearchApiException;
import org.rudi.facet.apigateway.exceptions.SearchThrottlingException;
import org.rudi.facet.apigateway.exceptions.UpdateApiException;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.core.bean.PagedApiList;
import org.rudi.microservice.apigateway.core.bean.PagedThrottlingList;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiGatewayHelper {

	@Qualifier("apigatewayWebClient")
	private final WebClient apigatewayWebClient;
	private final ApiGatewayProperties apigatewayProperties;

	public Page<Api> searchApis(ApiSearchCriteria searchCriteria, Pageable page) throws SearchApiException {
		Mono<PagedApiList> mono = apigatewayWebClient.get().uri(uriBuilder -> uriBuilder
				.path(apigatewayProperties.getApisPath())
				.queryParamIfPresent("apiId", Optional.ofNullable(searchCriteria.getApiId()))
				.queryParamIfPresent("globalId", Optional.ofNullable(searchCriteria.getGlobalId()))
				.queryParamIfPresent("confidentiality", Optional.ofNullable(searchCriteria.getConfidentiality()))
				.queryParamIfPresent("mediaId", Optional.ofNullable(searchCriteria.getMediaId()))
				.queryParamIfPresent("nodeProvideId", Optional.ofNullable(searchCriteria.getNodeProviderId()))
				.queryParamIfPresent("providerId", Optional.ofNullable(searchCriteria.getProviderId()))
				.queryParamIfPresent("producerId", Optional.ofNullable(searchCriteria.getProducerId()))
				.queryParamIfPresent("url", Optional.ofNullable(searchCriteria.getUrl()))
				.queryParamIfPresent("offset", Optional.ofNullable(convertPageOffset(page)))
				.queryParamIfPresent("limit", Optional.ofNullable(convertPageSize(page)))
				.queryParamIfPresent("order", Optional.ofNullable(convertSort(page.getSort()))).build()).retrieve()
				.bodyToMono(PagedApiList.class);
		PagedApiList pagedApiList = MonoUtils.blockOrThrow(mono, SearchApiException.class);
		if (pagedApiList != null && CollectionUtils.isNotEmpty(pagedApiList.getElements())) {
			return new PageImpl<>(pagedApiList.getElements(), page, pagedApiList.getTotal());
		} else {
			return null;
		}
	}

	public Api getApiById(UUID globalId, UUID mediaId) throws GetApiException {
		if (globalId == null || mediaId == null) {
			throw new IllegalArgumentException("globalId and mediaId are required");
		}
		ApiSearchCriteria apiSearchCriteria = new ApiSearchCriteria().globalId(globalId).mediaId(mediaId);
		try {
			Page<Api> apis = searchApis(apiSearchCriteria, PageRequest.of(0, 1));
			if (apis != null && CollectionUtils.isNotEmpty(apis.getContent())) {
				return apis.getContent().get(0);
			}
			return null;
		} catch (SearchApiException e) {
			throw new GetApiException((WebClientResponseException) e.getCause());
		}
	}

	public Api createApi(Api api) throws CreateApiException {
		Mono<Api> mono = apigatewayWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(apigatewayProperties.getApisPath()).build())
				.body(Mono.just(api), Api.class).retrieve().bodyToMono(Api.class);
		return MonoUtils.blockOrThrow(mono, CreateApiException.class);
	}

	public Api updateApi(Api api) throws UpdateApiException {
		Mono<Api> mono = apigatewayWebClient.put()
				.uri(uriBuilder -> uriBuilder.path(apigatewayProperties.getApisPath()).build())
				.body(Mono.just(api), Api.class).retrieve().bodyToMono(Api.class);
		return MonoUtils.blockOrThrow(mono, UpdateApiException.class);
	}

	public void deleteApi(UUID globalId, UUID mediaId) throws GetApiException {
		Api api = getApiById(globalId, mediaId);
		if (api != null) {
			deleteApi(api.getApiId());
		}
	}

	public void deleteApi(UUID apiId) throws GetApiException {
		Mono<Void> mono = apigatewayWebClient.delete()
				.uri(uriBuilder -> uriBuilder.path(apigatewayProperties.getApisDeletePath()).build(apiId)).retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, DeleteApiException.class);
	}

	public Page<Throttling> searchThrottlings(ThrottlingSearchCriteria searchCriteria, Pageable page)
			throws SearchThrottlingException {
		Mono<PagedThrottlingList> mono = apigatewayWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(apigatewayProperties.getThrottlingsPath())
						.queryParamIfPresent("active", Optional.ofNullable(searchCriteria.getActive()))
						.queryParamIfPresent("code", Optional.ofNullable(searchCriteria.getCode()))
						.queryParamIfPresent("offset", Optional.ofNullable(convertPageOffset(page)))
						.queryParamIfPresent("limit", Optional.ofNullable(convertPageSize(page)))
						.queryParamIfPresent("order", Optional.ofNullable(convertSort(page.getSort()))).build())
				.retrieve().bodyToMono(PagedThrottlingList.class);
		PagedThrottlingList pagedList = MonoUtils.blockOrThrow(mono, SearchThrottlingException.class);
		if (pagedList != null) {
			return new PageImpl<>(pagedList.getElements(), page, pagedList.getTotal());
		} else {
			return null;
		}
	}

	protected Integer convertPageOffset(Pageable page) {
		if (page.isUnpaged()) {
			return null;
		} else {
			return page.getPageSize() * page.getPageNumber();
		}
	}

	protected Integer convertPageSize(Pageable page) {
		if (page.isUnpaged()) {
			return null;
		} else {
			return page.getPageSize();
		}
	}

	protected String convertSort(Sort sort) {
		StringBuilder buffer = new StringBuilder();
		if (sort != null) {
			sort.get().forEach(order -> {
				if (buffer.length() > 0) {
					buffer.append(',');
				}
				if (order.isDescending()) {
					buffer.append('-');
				}
				buffer.append(order.getProperty());
			});
		}
		return buffer.length() > 0 ? buffer.toString() : null;
	}
}
