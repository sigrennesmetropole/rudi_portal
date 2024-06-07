package org.rudi.microservice.konsult.service.config;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.core.customization.CustomizationDescriptionData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.rudi.microservice.konsult.service.constant.BeanIds.CUSTOMIZATION_CACHE_MANAGER;
import static org.rudi.microservice.konsult.service.constant.BeanIds.CUSTOMIZATION_DATA_CACHE;
import static org.rudi.microservice.konsult.service.constant.BeanIds.CUSTOMIZATION_RESOURCES_CACHE;

@Configuration
@SuppressWarnings({ "java:S2095", "java:S1075" })
@EnableCaching
public class CustomizationCacheConfig {
	private static final String CONFIG_FILE_PATH = "/cache/ehcache_customization_config.xml";

	@Bean(name = CUSTOMIZATION_CACHE_MANAGER)
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

	@Bean(name = CUSTOMIZATION_RESOURCES_CACHE)
	public Cache<String, DocumentContent> customizationCache(
			@Qualifier(CUSTOMIZATION_CACHE_MANAGER) CacheManager cacheManager) throws AppServiceException {
		Cache<String, DocumentContent> cache = cacheManager.getCache(CUSTOMIZATION_RESOURCES_CACHE, String.class,
				DocumentContent.class);
		if (cache == null) {
			throw new AppServiceException("Erreur lors de la configuration du cache customization_resources");
		}

		return cache;
	}

	@Bean(name = CUSTOMIZATION_DATA_CACHE)
	public Cache<String, CustomizationDescriptionData> customizationDataCache(
			@Qualifier(CUSTOMIZATION_CACHE_MANAGER) CacheManager cacheManager) throws AppServiceException {
		Cache<String, CustomizationDescriptionData> cache = cacheManager.getCache(CUSTOMIZATION_DATA_CACHE, String.class,
				CustomizationDescriptionData.class);
		if (cache == null) {
			throw new AppServiceException("Erreur lors de la configuration du cache customization_resources");
		}

		return cache;
	}
}
