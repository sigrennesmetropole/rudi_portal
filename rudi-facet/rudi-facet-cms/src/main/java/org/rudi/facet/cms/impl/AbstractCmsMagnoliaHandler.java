/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.impl.configuration.CmsMagnoliaConfiguration;
import org.rudi.facet.cms.impl.model.CmsMagnoliaJCRNode;
import org.rudi.facet.cms.impl.model.CmsMagnoliaNode;
import org.rudi.facet.cms.impl.model.CmsMagnoliaPage;
import org.rudi.facet.cms.impl.model.CmsRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import lombok.Getter;

/**
 * @author FNI18300
 */
public abstract class AbstractCmsMagnoliaHandler<T extends CmsMagnoliaJCRNode> {

	@Getter
	private final CmsMagnoliaConfiguration cmsMagnoliaConfiguration;

	@Getter
	private final WebClient magnoliaWebClient;

	protected AbstractCmsMagnoliaHandler(CmsMagnoliaConfiguration cmsMagnoliaConfiguration,
			WebClient magnoliaWebClient) {
		super();
		this.cmsMagnoliaConfiguration = cmsMagnoliaConfiguration;
		this.magnoliaWebClient = magnoliaWebClient;
	}

	public abstract CmsAssetType getAssetType();

	public CmsMagnoliaPage<T> searchItems(CmsRequest cmsRequest, Integer offset, Integer limit, String orderBy) {
		if (StringUtils.isNotEmpty(cmsRequest.getQuery())) {
			// si on passe un paramètre de type query (recherche full text)
			return searchQueryItems(cmsRequest, offset, limit, orderBy);
		} else {
			// dans tous les autres cas
			return searchClassicItems(cmsRequest, offset, limit, orderBy);
		}
	}

	/**
	 * @param cmsRequest la requete query
	 * @param offset     l'offset
	 * @param limit      le nombre d'éléments
	 * @param orderBy    le tri
	 * @return
	 */
	protected CmsMagnoliaPage<T> searchClassicItems(final CmsRequest cmsRequest, Integer offset, Integer limit,
			String orderBy) {
		String path = cmsMagnoliaConfiguration.getPath(getAssetType());

		return magnoliaWebClient.get()
				.uri(MagnoliaServiceImpl.GET_API_URL,
						uriBuilder -> uriBuilder(uriBuilder, path, cmsRequest, offset, limit, orderBy))
				.retrieve().bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<T>>() {
				}).block();
	}

	protected CmsMagnoliaPage<T> searchQueryItems(CmsRequest cmsRequest, Integer offset, Integer limit,
			String orderBy) {
		CmsMagnoliaPage<T> result = new CmsMagnoliaPage<>();
		String path = getCmsMagnoliaConfiguration().getPath(getAssetType());

		// une première requet en mode "full text" avec la langue
		CmsRequest qCmsRequest = CmsRequest.builder().nodeType("mgnl:contentNode").query(cmsRequest.getQuery())
				.filters(List.of(cmsRequest.getLocale() != null ? "lang=" + cmsRequest.getLocale().getLanguage()
						: StringUtils.EMPTY))
				.build();
		CmsMagnoliaPage<CmsMagnoliaNode> items = getMagnoliaWebClient().get()
				.uri(MagnoliaServiceImpl.GET_API_URL,
						uriBuilder -> uriBuilder(uriBuilder, path, qCmsRequest, 0,
								cmsMagnoliaConfiguration.getFullTextLimit(), orderBy))
				.retrieve().bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<CmsMagnoliaNode>>() {
				}).block();
		if (items != null && items.getTotal() > 0) {
			// collect des paths pertinent
			List<String> newsPaths = collectNewPaths(items);
			result.setTotal(newsPaths.size());
			result.setResults(new ArrayList<>());
			// recherce des news
			List<T> news = collectNews(newsPaths, path, cmsRequest.getCategories(), cmsRequest.getSanitizedFilters(),
					offset, limit);
			if (offset > 0) {
				result.setResults(news.subList(offset, Math.min(offset + limit, newsPaths.size())));
			} else {
				result.setResults(news);
			}
		}
		return result;
	}

	protected List<T> collectNews(List<String> newsPaths, String path, List<String> categories, List<String> filters,
			Integer offset, Integer limit) {
		List<T> news = new ArrayList<>();

		for (String newsPath : newsPaths) {
			List<String> lFilters = new ArrayList<>(filters);
			lFilters.add("@path[eq]=" + newsPath);
			CmsRequest iCmsRequest = CmsRequest.builder().categories(categories).filters(lFilters).build();
			CmsMagnoliaPage<T> newsItems = getMagnoliaWebClient().get()
					.uri(MagnoliaServiceImpl.GET_API_URL,
							uriBuilder -> uriBuilder(uriBuilder, path, iCmsRequest, null, null, null))
					.retrieve().bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<T>>() {
					}).block();
			if (newsItems != null && CollectionUtils.isNotEmpty(newsItems.getResults())) {
				news.addAll(newsItems.getResults());
			}
			if (news.size() >= offset + limit) {
				break;
			}
		}
		return news;
	}

	protected List<String> collectNewPaths(CmsMagnoliaPage<CmsMagnoliaNode> items) {
		List<String> newsPaths = new ArrayList<>();
		items.getResults().forEach(item -> {
			int index = item.getPath().indexOf('/', "rudi/".length() + 1);
			if (index > 0) {
				String usedPath = item.getPath().substring(0, index);
				if (!newsPaths.contains(usedPath)) {
					newsPaths.add(usedPath);
				}
			}
		});
		return newsPaths;
	}

	protected URI uriBuilder(UriBuilder uriBuilder, String path, CmsRequest cmsRequest, Integer offset, Integer limit,
			String orderBy) {
		uriBuilder.queryParamIfPresent("offset", offset != null ? Optional.of(offset) : Optional.empty())
				.queryParamIfPresent("nodeTypes",
						cmsRequest.getNodeType() != null ? Optional.of(cmsRequest.getNodeType()) : Optional.empty())
				.queryParamIfPresent("limit", limit != null && limit > 0 ? Optional.of(limit) : Optional.empty())
				.queryParamIfPresent("q",
						cmsRequest.getQuery() != null ? Optional.of(cmsRequest.getQuery()) : Optional.empty())
				.queryParamIfPresent("orderBy",
						StringUtils.isNotEmpty(orderBy) ? Optional.of(convertOrderBy(orderBy)) : Optional.empty())
				.query(computeSearchFilter(cmsRequest.getCategories()));

		if (CollectionUtils.isNotEmpty(cmsRequest.getSanitizedFilters())) {
			for (String filter : cmsRequest.getSanitizedFilters()) {
				String[] filterParts = filter.split("=");
				if (filterParts.length > 1) {
					uriBuilder.queryParam(filterParts[0], filterParts[1]);
				}
			}
		}

		return uriBuilder.build(path);
	}

	protected URI uriItemBuilder(UriBuilder uriBuilder, String path) {
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

	protected String convertOrderBy(String orderBy) {
		StringBuilder orderByBuilder = new StringBuilder();
		if (StringUtils.isEmpty(orderBy)) {
			return orderByBuilder.toString();
		}

		String[] orders = orderBy.split(",");
		for (String order : orders) {
			if (order.startsWith("-")) {
				orderByBuilder.append(order.substring(1)).append(" desc");
			} else {
				orderByBuilder.append(order).append(" asc");
			}
			orderByBuilder.append(",");
		}
		orderByBuilder.deleteCharAt(orderByBuilder.length() - 1);
		return orderByBuilder.toString();
	}

	protected Pair<String, String> lookupPair(List<Pair<String, String>> pairs, String property) {
		Pair<String, String> result = null;
		if (CollectionUtils.isNotEmpty(pairs)) {
			result = pairs.stream().filter(p -> p.getLeft().equalsIgnoreCase(property)).findFirst().orElse(null);
		}
		return result;
	}

	protected List<Triple<String, String, String>> convertFilters(List<String> filters) {
		if (CollectionUtils.isEmpty(filters)) {
			return List.of();
		}
		List<Triple<String, String, String>> result = new ArrayList<>();
		for (String filter : filters) {
			String[] parts = filter.split("=");
			if (parts.length == 2) {
				int indexofOpenBracket = parts[0].indexOf('[');
				int indexofCloseBracket = parts[0].indexOf(']');
				if (indexofOpenBracket >= 0 && indexofCloseBracket > indexofOpenBracket) {
					result.add(Triple.of(parts[0].substring(0, indexofOpenBracket),
							parts[0].substring(indexofOpenBracket + 1, indexofCloseBracket - 1), parts[1]));
				} else {
					result.add(Triple.of(parts[0], "eq", parts[1]));
				}
			}
		}
		return result;
	}

	protected Triple<String, String, String> lookupTriple(List<Triple<String, String, String>> triples,
			String property) {
		Triple<String, String, String> result = null;
		if (CollectionUtils.isNotEmpty(triples)) {
			result = triples.stream().filter(p -> p.getLeft().equalsIgnoreCase(property)).findFirst().orElse(null);
		}
		return result;
	}
}
