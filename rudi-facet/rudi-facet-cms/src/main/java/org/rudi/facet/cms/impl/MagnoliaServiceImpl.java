/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl;

import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.rudi.facet.cms.CmsService;
import org.rudi.facet.cms.bean.CmsAsset;
import org.rudi.facet.cms.bean.CmsAssetType;
import org.rudi.facet.cms.bean.CmsCategory;
import org.rudi.facet.cms.exception.CmsException;
import org.rudi.facet.cms.impl.configuration.BeanIds;
import org.rudi.facet.cms.impl.configuration.CmsMagnoliaConfiguration;
import org.rudi.facet.cms.impl.mapper.CmsCategoryMapper;
import org.rudi.facet.cms.impl.model.CmsMagnoliaCategory;
import org.rudi.facet.cms.impl.model.CmsMagnoliaNode;
import org.rudi.facet.cms.impl.model.CmsMagnoliaPage;
import org.rudi.facet.cms.impl.model.CmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "cms.implementation", havingValue = "magnolia")
@RequiredArgsConstructor
public class MagnoliaServiceImpl implements CmsService {

	public static final String GET_API_URL = "/.rest/delivery/{apps-name}/v1";

	public static final String APPS_NAME_PATH = "apps-name";

	public static final String CATAGORIES_PARAM = "categories";

	private final CmsMagnoliaConfiguration cmsMagnoliaConfiguration;

	private final List<AbstractCmsMagnoliaHandler<?>> cmsMagnoliaHandlers;

	private final CmsCategoryMapper cmsCategoryMapper;

	@Autowired
	@Qualifier(BeanIds.CMS_WEB_CLIENT)
	private WebClient magnoliaWebClient;

	@Autowired
	@Qualifier(BeanIds.CMS_CATEGORY_EHCACHE)
	private Cache<String, CmsCategory> cacheCategories;

	@Autowired
	@Qualifier(BeanIds.CMS_ASSET_EHCACHE)
	private Cache<String, CmsAsset> cacheAssets;

	/**
	 * <ul>
	 * <li>http://ren1vml0158:9090/.rest/delivery/categories/v1</li>
	 * <li>http://ren1vml0158:9090/.rest/delivery/terms/v1</li>
	 * <li>http://ren1vml0158:9090/.rest/delivery/news/v1</li>
	 * <li>http://ren1vml0158:9090/.rest/delivery/projectvalues/v1</li>
	 * </ul>
	 * 
	 * Mise en cache des catégories afin de permettre la correspondance entre uuid de catégorie et nom L'idée de d'utiliser les APIs
	 */
	@Override
	public CmsCategory getCategory(String path) throws CmsException {
		CmsCategory category = cacheCategories.get(path);
		if (category == null) {
			getCategories();
			return cacheCategories.get(path);
		} else {
			return category;
		}
	}

	@Override
	public List<CmsCategory> getCategories() throws CmsException {
		CmsMagnoliaPage<CmsMagnoliaCategory> cmsMagnoliaCategories = magnoliaWebClient.get()
				.uri(buildGetURL(), uriBuilder -> uriBuilder.build(CATAGORIES_PARAM)).retrieve()
				.bodyToMono(new ParameterizedTypeReference<CmsMagnoliaPage<CmsMagnoliaCategory>>() {
				}).block();
		List<CmsCategory> categories = null;
		if (cmsMagnoliaCategories != null) {
			categories = cmsCategoryMapper.convertItems(cmsMagnoliaCategories.getResults());
			if (CollectionUtils.isNotEmpty(categories)) {
				categories.forEach(c -> cacheCategories.put(c.getPath(), c));
			}
		}
		return categories;
	}

	@Override
	public CmsAsset renderAsset(CmsAssetType assetType, String assetId, String assetTemplate, Locale locale)
			throws CmsException {
		CmsAsset result = cacheAssets.get(computeAssetKeyCache(assetType, assetId, assetTemplate));
		if (result == null) {
			AbstractCmsMagnoliaHandler<?> abstractCmsMagnoliaHandler = lookupHandler(assetType);
			if (abstractCmsMagnoliaHandler != null) {
				CmsMagnoliaNode node = abstractCmsMagnoliaHandler.findItem(assetId);
				if (node != null) {
					result = downloadAsset(assetType, assetTemplate, locale, assetId);
				}
			}
		}
		return result;
	}

	@Override
	public List<CmsAsset> renderAssets(CmsAssetType assetType, String assetTemplate, CmsRequest request, Integer offset,
			Integer limit, String order) throws CmsException {
		final List<CmsAsset> result = new ArrayList<>();
		String defaultCategory = cmsMagnoliaConfiguration.getDefaultCategory(assetType);
		List<String> categories = new ArrayList<>();
		if (request.getCategories() != null) {
			categories.addAll(request.getCategories());
		}
		if (!categories.contains(defaultCategory)) {
			categories.add(defaultCategory);
		}
		AbstractCmsMagnoliaHandler<?> abstractCmsMagnoliaHandler = lookupHandler(assetType);
		if (abstractCmsMagnoliaHandler != null) {
			CmsMagnoliaPage<?> page = abstractCmsMagnoliaHandler.searchItems(convertCategories(categories),
					request.getFilters(), offset, limit, order);
			if (page.getTotal() > 0 && CollectionUtils.isNotEmpty(page.getResults())) {
				page.getResults().forEach(item -> {
					CmsAsset asset = cacheAssets.get(computeAssetKeyCache(assetType, item.getId(), assetTemplate));
					if (asset == null) {
						asset = downloadAsset(assetType, assetTemplate, request.getLocale(), item.getId());
					}
					if (asset != null) {
						asset.setCreationDate(item.getCreated());
						asset.setUpdateDate(item.getLastModified());
						result.add(asset);
					}
				});
			}
		}
		return result;
	}

	protected List<String> convertCategories(List<String> categories) {
		if (CollectionUtils.isNotEmpty(categories)) {
			return categories.stream().map(c -> {
				try {
					return getCategory(c) != null ? getCategory(c).getId() : StringUtils.EMPTY;
				} catch (CmsException e) {
					return StringUtils.EMPTY;
				}
			}).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
		} else {
			return List.of();
		}
	}

	protected CmsAsset downloadAsset(CmsAssetType assetType, String assetTemplate, Locale locale, String id) {
		CmsAsset result = null;
		Flux<DataBuffer> flux = magnoliaWebClient.get()
				.uri("rudi/" + convertTemplateName(assetTemplate) + ".html",
						uriBuilder -> uriBuilder.queryParam("id", id)
								.queryParamIfPresent("lang",
										locale != null ? Optional.of(locale.getLanguage()) : Optional.empty())
								.build())
				.retrieve().bodyToFlux(DataBuffer.class);

		try {
			File outputFile = createOutputFile();
			DataBufferUtils.write(flux, Path.of(outputFile.getPath()), WRITE).block();
			Document document = Jsoup.parse(outputFile, "UTF-8");
			Element element = document.selectFirst("div." + cmsMagnoliaConfiguration.getCssSelector(assetType));
			if (element != null) {
				result = new CmsAsset();
				result.setId(id);
				result.setContent(element.outerHtml());
				cacheAssets.put(computeAssetKeyCache(assetType, id, assetTemplate), result);
			}
		} catch (Exception e) {
			log.warn("failed to download " + assetTemplate + " id=" + id, e);
		}

		return result;
	}

	protected String convertTemplateName(String template) {
		if (StringUtils.isNotEmpty(template)) {
			return template.replace("@", "/");
		} else {
			return template;
		}
	}

	protected File createOutputFile() throws IOException {
		File generateFile = null;
		if (StringUtils.isNotEmpty(cmsMagnoliaConfiguration.getTemporaryDirectory())) {
			generateFile = File.createTempFile(cmsMagnoliaConfiguration.getTemporaryFilePrefix(),
					cmsMagnoliaConfiguration.getTemporaryFileExtension(),
					new File(cmsMagnoliaConfiguration.getTemporaryDirectory()));
		} else {
			generateFile = File.createTempFile(cmsMagnoliaConfiguration.getTemporaryFilePrefix(),
					cmsMagnoliaConfiguration.getTemporaryFileExtension());
		}
		if (log.isDebugEnabled()) {
			log.debug("Temporary generation file:{}", generateFile);
		}
		return generateFile;
	}

	protected String buildGetURL() {
		return GET_API_URL;
	}

	protected AbstractCmsMagnoliaHandler<? extends CmsMagnoliaNode> lookupHandler(CmsAssetType assetType) {
		return cmsMagnoliaHandlers.stream().filter(h -> h.getAssetType() == assetType).findFirst().orElse(null);
	}

	protected String computeAssetKeyCache(CmsAssetType assetType, String assetId, String assetTemplate) {
		return assetType.name() + "@" + assetId + "@" + assetTemplate;
	}

}
