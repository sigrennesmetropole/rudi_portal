/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.impl.configuration.CmsMagnoliaConfiguration;
import org.rudi.facet.cms.impl.model.CmsMagnoliaJCRNode;
import org.rudi.facet.cms.impl.model.CmsMagnoliaPage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractCmsMagnoliaHandler<T extends CmsMagnoliaJCRNode> {

	private final CmsMagnoliaConfiguration cmsMagnoliaConfiguration;

	private final WebClient magnoliaWebClient;

	protected AbstractCmsMagnoliaHandler(CmsMagnoliaConfiguration cmsMagnoliaConfiguration,
			WebClient magnoliaWebClient) {
		super();
		this.cmsMagnoliaConfiguration = cmsMagnoliaConfiguration;
		this.magnoliaWebClient = magnoliaWebClient;
	}

	public abstract CmsAssetType getAssetType();

	public CmsMagnoliaPage<T> searchItems(List<String> categories, List<String> filters, Integer offset, Integer limit,
			String orderBy) {
		String path = cmsMagnoliaConfiguration.getPath(getAssetType());

		return magnoliaWebClient.get()
				.uri(MagnoliaServiceImpl.GET_API_URL,
						uriBuilder -> uriBuilder(uriBuilder, path, categories, filters, offset, limit, orderBy))
				.retrieve().bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<T>>() {
				}).block();
	}

	protected URI uriBuilder(UriBuilder uriBuilder, String path, List<String> categories, List<String> filters,
			Integer offset, Integer limit, String orderBy) {
		uriBuilder.queryParamIfPresent("offset", offset != null ? Optional.of(offset) : Optional.empty())
				.queryParamIfPresent("limit", limit != null && limit > 0 ? Optional.of(limit) : Optional.empty())
				.queryParamIfPresent("orderBy",
						StringUtils.isNotEmpty(orderBy) ? Optional.of(orderBy) : Optional.empty())
				.query(computeSearchFilter(categories));

		if (CollectionUtils.isNotEmpty(filters)) {
			for (String filter : filters) {
				String[] filterParts = filter.split("=");
				if (filterParts.length > 1) {
					uriBuilder.queryParam(filterParts[0], filterParts[1]);
				}
			}
		}

		return uriBuilder.build(path);
	}

	protected String computeSearchFilter(List<String> categories) {
		StringBuilder filterBuilder = new StringBuilder();
		if (CollectionUtils.isNotEmpty(categories)) {
			if (categories.size() == 1) {
				filterBuilder.append("categories=").append(StringUtils.join(categories.toArray()));
			} else {
				filterBuilder.append("categories[in]=").append(StringUtils.join(categories.toArray()));
			}
		}

		return filterBuilder.toString();
	}

	public T findItem(String id) {
		String path = cmsMagnoliaConfiguration.getPath(getAssetType());
		CmsMagnoliaPage<T> page = magnoliaWebClient.get()
				.uri(MagnoliaServiceImpl.GET_API_URL, uriBuilder -> uriBuilder.queryParam("@jcr:uuid", id).build(path))
				.retrieve().bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<T>>() {
				}).block();
		if (page != null && CollectionUtils.isNotEmpty(page.getResults())) {
			return page.getResults().get(0);
		} else {
			return null;
		}
	}
}
