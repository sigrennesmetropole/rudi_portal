package org.rudi.facet.dataverse.api.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.AbstractDataverseAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.SearchItemInfo;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.model.DataverseResponse;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.EnumSet;

public abstract class AbstractSearchOperationAPI<T extends SearchItemInfo> extends AbstractDataverseAPI {

	public AbstractSearchOperationAPI(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	public SearchElements<T> searchDataset(SearchParams searchParams) throws DataverseAPIException {
		String url = createUrl("search");
		url = buildSearchUrl(url, searchParams);

		ParameterizedTypeReference<DataverseResponse<SearchElements<T>>> type = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<DataverseResponse<SearchElements<T>>> resp = getRestTemplate().exchange(url, HttpMethod.GET, createHttpEntity(), type);
		return getDataBody(resp);
	}

	protected void validateSearchParams(SearchParams searchParams, SearchType expected) {
		if (CollectionUtils.isEmpty(searchParams.getType()) || searchParams.getType().size() != 1 || !searchParams.getType().contains(expected)) {
			throw new IllegalArgumentException(String.format("Search must be configured to search only  %ss", expected.name()));
		}
	}

	private HttpEntity<String> createHttpEntity() {
		HttpHeaders headers = buildHeadersWithApikey();
		return new HttpEntity<>("", headers);
	}

	private String buildSearchUrl(String path, SearchParams searchParams) throws DataverseAPIException {

		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(path).queryParam("q", searchParams.getQ());
		EnumSet<SearchType> types = searchParams.getType();
		if (!CollectionUtils.isEmpty(types)) {
			urlBuilder.queryParam("type", types.toArray());
		}
		if (!StringUtils.isEmpty(searchParams.getSubtree())) {
			urlBuilder.queryParam("subtree", searchParams.getSubtree());
		}
		if (!CollectionUtils.isEmpty(searchParams.getFilterQuery())) {
			urlBuilder.queryParam("fq", searchParams.getFilterQuery());
		}
		if (searchParams.getSortBy() != null) {
			urlBuilder.queryParam("sort", searchParams.getSortBy());
		}
		if (searchParams.getSortOrder() != null) {
			urlBuilder.queryParam("order", searchParams.getSortOrder());
		}
		if (searchParams.getPerPage() != null && searchParams.getPerPage() != 0) {
			urlBuilder.queryParam("per_page", searchParams.getPerPage());
		}
		if (searchParams.getStart() != null && searchParams.getStart() != 0) {
			urlBuilder.queryParam("start", searchParams.getStart());
		}
		if (BooleanUtils.isTrue(searchParams.getShowFacets())) {
			urlBuilder.queryParam("show_facets", true);
		}
		if (BooleanUtils.isTrue(searchParams.getShowRelevance())) {
			urlBuilder.queryParam("show_relevance", true);
		}
		if (CollectionUtils.isNotEmpty(searchParams.getMetadatafields())) {
			urlBuilder.queryParam("metadata_fields", searchParams.getMetadatafields());
		}
		try {
			return urlBuilder.build(false).toUriString();
		} catch (IllegalArgumentException e) {
			throw new DataverseAPIException(e);
		}
	}
}
