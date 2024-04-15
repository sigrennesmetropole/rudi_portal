/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.ehcache.Cache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rudi.common.core.DocumentContent;
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
import org.rudi.facet.cms.impl.utils.ResourceUriRewriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "cms.implementation", havingValue = "magnolia")
@RequiredArgsConstructor
public class MagnoliaServiceImpl implements CmsService {

	public static final String GET_API_URL = "/.rest/delivery/{apps-name}/v1";

	public static final String APPS_NAME_PATH = "apps-name";
	// les uris de la forme : "/dam/jrc:{UUID}/"
	private static final String URI_REGEX = "/dam/jcr:[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/";

	// /.imaging/default/dam/rudi/png.png/jcr:content.png
	private static final String IMAGING_REGEX = "/.imaging/default/dam/[a-zA-Z0-9._%#!%$=+@:-]";

	private static final String RESOURCES_REGEX = "/\\.resources/[a-zA-Z0-9_./ -]*";

	private static final String FILENAME_REGEX = "[A-Za-z0-9_%#!%$=+@: -]+\\.[a-zA-Z0-9]{3,4}";

	private static final String URI_AND_FILENAME_REGEX = URI_REGEX + FILENAME_REGEX;

	public static final String IMAGE1_CSS_QUERY = "img[src~=" + URI_AND_FILENAME_REGEX + "]";
	private static final String IMAGE2_CSS_QUERY = "img[src~=" + IMAGING_REGEX + "]";
	public static final String STYLE_CSS_QUERY = "[style~=background-image: url\\(" + URI_AND_FILENAME_REGEX + "\\);]";
	public static final String ANCHOR_CSS_QUERY = "a[href~=" + URI_AND_FILENAME_REGEX + "]";
	public static final String LINK_CSS_QUERY = "link[href~=" + RESOURCES_REGEX + "]";

	public static final String RESOURCES_CSS_QUERY = IMAGE1_CSS_QUERY + "," + IMAGE2_CSS_QUERY + "," + STYLE_CSS_QUERY
			+ "," + ANCHOR_CSS_QUERY + "," + LINK_CSS_QUERY;

	public static final String SELF_CSS_QUERY = "a[href~=^@self/([^/]+)/([^/]+)/([^/]+)/([^/]+)$]";

	public static final String CATAGORIES_PARAM = "categories";

	private final CmsMagnoliaConfiguration cmsMagnoliaConfiguration;

	private final List<AbstractCmsMagnoliaHandler<?>> cmsMagnoliaHandlers;

	private final CmsCategoryMapper cmsCategoryMapper;

	private final List<ResourceUriRewriter> resourceUriRewriters;

	@Autowired
	@Qualifier(BeanIds.CMS_WEB_CLIENT)
	private WebClient magnoliaWebClient;

	@Autowired
	@Qualifier(BeanIds.CMS_CATEGORY_EHCACHE)
	private Cache<String, CmsCategory> cacheCategories;

	@Autowired
	@Qualifier(BeanIds.CMS_ASSET_EHCACHE)
	private Cache<String, CmsAsset> cacheAssets;

	@Autowired
	@Qualifier(BeanIds.CMS_RESOURCES_EHCACHE)
	private Cache<String, DocumentContent> cacheResources;

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

	/**
	 * @param resourcePath path Magnolia vers la ressource
	 * @return une Ressource sous la forme de DocumentContent
	 * @throws CmsException exception lancée par le CMS
	 */
	@Override
	public DocumentContent downloadResource(String resourcePath) throws CmsException {
		DocumentContent documentContent = null;

		// Si le cache contient déjà la ressource, on la renvoie directement.
		if (cacheResources.containsKey(resourcePath)) {
			documentContent = cacheResources.get(resourcePath);

			// Remise à zéro du stream de la ressource.
			documentContent.closeStream();

			if (documentContent.getFile().exists() && documentContent.getFile().isFile()) {
				return documentContent;
			}
		}

		log.info("Load cms resources {}", resourcePath);

		int lastIndexBeforeExtension = resourcePath.lastIndexOf(".");
		String fileExtension = resourcePath.substring(lastIndexBeforeExtension);
		// Le +1 sert à échapper le "/"
		String fileName = resourcePath.replaceAll("[.:/-]", "_") + resourcePath.substring(lastIndexBeforeExtension);

		// appel à magnolia
		Flux<DataBuffer> flux = magnoliaWebClient.get().uri(resourcePath).retrieve().bodyToFlux(DataBuffer.class);
		try {
			File outputFile = createOutputFile(fileExtension);
			DataBufferUtils.write(flux, Path.of(outputFile.getPath()), WRITE).block();

			// Création du documentContent
			if (outputFile.exists() && outputFile.isFile()) {
				String type = computeMimeType(fileName, outputFile);
				documentContent = new DocumentContent(fileName, type, outputFile);

				log.info("Cache cms resources {} / {}", fileName, type);

				// mise en cache
				cacheResources.put(resourcePath, documentContent);
			}
		} catch (IOException e) {
			throw new CmsException("Failed to download ressources:" + resourcePath, e);
		}

		return documentContent;
	}

	private String computeMimeType(String fileName, File file) throws IOException {
		String type = Files.probeContentType(Path.of(fileName));
		if (!isValidMimeType(type)) {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			type = fileNameMap.getContentTypeFor(fileName);
		}
		if (!isValidMimeType(type)) {
			Tika tika = new Tika();
			type = tika.detect(file);
		}
		if (!isValidMimeType(type)) {
			Tika tika = new Tika();
			type = tika.detect(fileName);
		}
		return type;
	}

	private boolean isValidMimeType(String type) {
		return !(type == null || "application/octet-stream".equals(type));
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
			File outputFile = createOutputFile(cmsMagnoliaConfiguration.getTemporaryFileExtension());
			DataBufferUtils.write(flux, Path.of(outputFile.getPath()), WRITE).block();
			Document document = Jsoup.parse(outputFile, "UTF-8");
			Element element = document.selectFirst("div." + cmsMagnoliaConfiguration.getCssSelector(assetType));

			if (element != null) {
				element = replaceResourcesLinks(element); // remplacer les liens des images par des appels à konsult
				element = replaceSelfLinks(element); // remplacer @self par /cms/detail
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

	protected Element replaceResourcesLinks(Element element) {
		Elements resources = element.select(MagnoliaServiceImpl.RESOURCES_CSS_QUERY);

		if (!resources.isEmpty()) {
			List<String> regexes = List.of(URI_AND_FILENAME_REGEX, RESOURCES_REGEX, IMAGING_REGEX);
			resources.forEach(e -> resourceUriRewriters.forEach(rewriter -> {
				if (rewriter.accept(e)) {
					rewriter.compute(e, regexes, cmsMagnoliaConfiguration.getFrontOfficeResourcesRoute());
				}
			}));
		}
		return element;
	}

	protected Element replaceSelfLinks(Element element) {
		// recherche de tous les liens de la forme @self/{type}/{id}/{template}/{titre} et remplacement de leur attributs
		element.select(MagnoliaServiceImpl.SELF_CSS_QUERY).replaceAll(a -> a.attr("href",
				StringUtils.replace(a.attr("href"), "@self", cmsMagnoliaConfiguration.getFrontOfficeRoute())));
		return element;
	}

	protected String convertTemplateName(String template) {
		if (StringUtils.isNotEmpty(template)) {
			return template.replace("@", "/");
		} else {
			return template;
		}
	}

	protected File createOutputFile(String fileExtension) throws IOException {
		File generateFile = null;
		if (StringUtils.isNotEmpty(cmsMagnoliaConfiguration.getTemporaryDirectory())) {
			generateFile = File.createTempFile(cmsMagnoliaConfiguration.getTemporaryFilePrefix(), fileExtension,
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
