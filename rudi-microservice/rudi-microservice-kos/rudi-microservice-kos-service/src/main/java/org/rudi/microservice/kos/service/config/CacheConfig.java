package org.rudi.microservice.kos.service.config;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.rudi.microservice.kos.service.constant.BeanIds.CONCEPT_ICONS_CACHE;
import static org.rudi.microservice.kos.service.constant.BeanIds.CONCEPT_ICONS_CACHE_MANAGER;

@Configuration
@SuppressWarnings({ "java:S2095", "java:S1075" })
@EnableCaching
public class CacheConfig {
	private static final String CONFIG_FILE_PATH = "/cache/ehcache_kos_config.xml";

	@Bean(name = CONCEPT_ICONS_CACHE_MANAGER)
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

	@Bean(name = CONCEPT_ICONS_CACHE)
	public Cache<String, DocumentContent> cache(@Qualifier(CONCEPT_ICONS_CACHE_MANAGER) CacheManager cacheManager)
			throws AppServiceException {
		Cache<String, DocumentContent> cache = cacheManager.getCache(CONCEPT_ICONS_CACHE, String.class,
				DocumentContent.class);
		if (cache == null) {
			throw new AppServiceException("Erreur lors de la configuration du cache concept_icons");
		}

		return cache;
	}

}
