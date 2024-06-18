package org.rudi.microservice.konsult.service.config;

import static org.rudi.microservice.konsult.service.constant.BeanIds.SITEMAP_CACHE_MANAGER;
import static org.rudi.microservice.konsult.service.constant.BeanIds.SITEMAP_DATA_CACHE;
import static org.rudi.microservice.konsult.service.constant.BeanIds.SITEMAP_RESOURCES_CACHE;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.sitemap.SitemapDescriptionData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings({ "java:S2095", "java:S1075" })
@EnableCaching
public class SitemapCacheConfig {
	private static final String CONFIG_FILE_PATH = "/cache/ehcache_sitemap_config.xml";

	@Bean(name = SITEMAP_CACHE_MANAGER)
	public org.ehcache.CacheManager ehCacheManager() throws AppServiceException {
		final URL myUrl = getClass().getResource(CONFIG_FILE_PATH);
		if (myUrl == null) {
			throw new AppServiceException(
					String.format("Impossible de trouver le fichier de configuration EhCache : %s", CONFIG_FILE_PATH));
		}
		XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
		org.ehcache.CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();
		return cacheManager;
	}

	@Bean(name = SITEMAP_RESOURCES_CACHE)
	public Cache<String, DocumentContent> sitemapCache(@Qualifier(SITEMAP_CACHE_MANAGER) CacheManager cacheManager)
			throws AppServiceException {
		Cache<String, DocumentContent> cache = cacheManager.getCache(SITEMAP_RESOURCES_CACHE, String.class,
				DocumentContent.class);
		if (cache == null) {
			throw new AppServiceException("Erreur lors de la configuration du cache sitemap");
		}

		return cache;
	}

	@Bean(name = SITEMAP_DATA_CACHE)
	public Cache<String, SitemapDescriptionData> sitemapDataCache(
			@Qualifier(SITEMAP_CACHE_MANAGER) CacheManager cacheManager) throws AppServiceException {
		Cache<String, SitemapDescriptionData> cache = cacheManager.getCache(SITEMAP_DATA_CACHE, String.class,
				SitemapDescriptionData.class);
		if (cache == null) {
			throw new AppServiceException("Erreur lors de la configuration du cache sitemap_resources");
		}

		return cache;
	}
}
