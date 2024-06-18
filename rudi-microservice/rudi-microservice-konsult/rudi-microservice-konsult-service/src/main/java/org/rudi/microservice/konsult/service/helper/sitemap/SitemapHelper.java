/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.service.helper.sitemap;

import static org.rudi.microservice.konsult.service.constant.BeanIds.SITEMAP_DATA_CACHE;
import static org.rudi.microservice.konsult.service.constant.BeanIds.SITEMAP_RESOURCES_CACHE;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections4.CollectionUtils;
import org.ehcache.Cache;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.resources.ResourcesHelper;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.rudi.microservice.konsult.core.sitemap.SitemapEntryData;
import org.rudi.microservice.konsult.service.sitemap.UrlListComputer;
import org.sitemaps.schemas.sitemap.TUrl;
import org.sitemaps.schemas.sitemap.Urlset;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@Component
@Slf4j
public class SitemapHelper extends ResourcesHelper {

	private static final String CACHE_SITEMAP_DATA_KEY = "sitemap-data";

	@Getter
	@Value("${sitemap.generated.filename:sitemap.xml}")
	private String sitemapGeneratedFilename;

	@Getter(AccessLevel.PUBLIC)
	@Value("${sitemap.base-package:sitemap}")
	private String basePackage;

	@Getter(AccessLevel.PUBLIC)
	@Value("${sitemap.base-directory:}")
	private String baseDirectory;

	@Getter(AccessLevel.PROTECTED)
	private final Cache<String, DocumentContent> cache;

	private final Cache<String, SitemapDescriptionData> sitemapCache;

	@Value("${sitemap.config.filename:sitemap.json}")
	private String sitemapConfigFilename;

	private final ObjectMapper objectMapper;

	private final List<UrlListComputer> urlListComputers;

	SitemapHelper(@Qualifier(SITEMAP_RESOURCES_CACHE) Cache<String, DocumentContent> cache,
			@Qualifier(SITEMAP_DATA_CACHE) Cache<String, SitemapDescriptionData> sitemapCache,
			ObjectMapper objectMapper, List<UrlListComputer> urlListComputers) {
		this.cache = cache;
		this.sitemapCache = sitemapCache;
		this.objectMapper = objectMapper;
		this.urlListComputers = urlListComputers;
		fillResourceMapping("sitemap", sitemapGeneratedFilename);
	}

	protected SitemapDescriptionData loadSitemapDescription() throws IOException {
		SitemapDescriptionData result = null;
		File f = new File(baseDirectory, sitemapConfigFilename);
		if (f.exists() && f.isFile()) {
			try (JsonParser p = objectMapper.createParser(f)) {
				result = p.readValueAs(SitemapDescriptionData.class);
			}
		} else {
			try (JsonParser p = objectMapper.createParser(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(basePackage + "/" + sitemapConfigFilename))) {
				result = p.readValueAs(SitemapDescriptionData.class);
			}
		}
		return result;
	}

	@Override
	protected DocumentContent getDocumentContent(String resourceName) throws IOException {
		// On vérifie l'existence du fichier avant de le créer
		if (Thread.currentThread().getContextClassLoader().getResource(getBasePackage() + resourceName) != null) {
			return DocumentContent.fromResourcePath(getBasePackage() + resourceName);
		}
		return null;
	}

	public SitemapDescriptionData getSitemapDescriptionData() {
		if (!sitemapCache.containsKey(CACHE_SITEMAP_DATA_KEY)) {
			try {
				sitemapCache.put(CACHE_SITEMAP_DATA_KEY, loadSitemapDescription());
			} catch (Exception e) {
				log.error("Failed to load sitemap", e);
			}
		}
		return sitemapCache.get(CACHE_SITEMAP_DATA_KEY);
	}

	public Urlset buildUrlset(SitemapDescriptionData sitemapDescriptionData) {
		Urlset urlset = new Urlset();

		for (SitemapEntryData sitemapEntryData : CollectionUtils.union(
				Collections.singletonList(sitemapDescriptionData.getStaticSitemapEntries()),
				sitemapDescriptionData.getSitemapEntries())) {

			// Parcours de la liste des KeyFigureComputer pour trouver celui qui correspond au KeyFigure
			for (UrlListComputer computer : urlListComputers) {
				if (computer.accept(sitemapEntryData.getType())) {
					try {
						List<TUrl> staticUrlList;
						staticUrlList = computer.compute(sitemapEntryData);
						urlset.getUrl().addAll(staticUrlList);
					} catch (AppServiceException e) {
						log.error("Erreur lors de la construction de la liste d'URL de type {}",
								sitemapEntryData.getType(), e);
					}
				}
			}
		}
		return urlset;
	}

	public void storeSitemapFile(Urlset urlset) throws AppServiceException {
		String dir = getBaseDirectory();
		String filename = getSitemapGeneratedFilename();
		try {
			File f = new File(dir, filename);
			JAXBContext context = JAXBContext.newInstance(Urlset.class);
			Marshaller mar = context.createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			f.getParentFile().mkdirs();
			mar.marshal(urlset, f);
			log.info("Sitemap généré dans {}", f.getAbsolutePath());

			// forcer la mise à jour du cache après regénération
			getCache().clear();
		} catch (Exception e) {
			throw new AppServiceException(
					String.format("Erreur lors de la generation du fichier de sitemap %s dans %s", filename, dir), e);
		}
	}

}
