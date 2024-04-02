package org.rudi.facet.cms.impl.configuration;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.cms.bean.CmsAsset;
import org.rudi.facet.cms.bean.CmsCategory;
import org.rudi.facet.cms.exception.CmsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CmsMagnoliaCacheConfig {

	private static final String CONFIG_FILE_PATH = "/cache/ehcache_cms_magnolia_config.xml";

	@Bean(name = BeanIds.EHCACHE_MANAGER)
	public CacheManager ehCacheManager() throws CmsException {
		final URL myUrl = getClass().getResource(CONFIG_FILE_PATH);
		if (myUrl == null) {
			throw new CmsException(
					String.format("Impossible de trouver le fichier de configuration EhCache : %s", CONFIG_FILE_PATH));
		}
		XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
		CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();
		return cacheManager;
	}

	@Bean(name = BeanIds.CMS_ASSET_EHCACHE)
	public Cache<String, CmsAsset> cacheAsset(@Qualifier(BeanIds.EHCACHE_MANAGER) CacheManager cacheManager)
			throws CmsException {
		Cache<String, CmsAsset> cache = cacheManager.getCache(BeanIds.CMS_ASSET_EHCACHE, String.class, CmsAsset.class);
		if (cache == null) {
			throw new CmsException("Erreur lors de la configuration du cache " + BeanIds.CMS_ASSET_EHCACHE);
		}

		return cache;
	}

	@Bean(name = BeanIds.CMS_CATEGORY_EHCACHE)
	public Cache<String, CmsCategory> cacheCategory(@Qualifier(BeanIds.EHCACHE_MANAGER) CacheManager cacheManager)
			throws CmsException {
		Cache<String, CmsCategory> cache = cacheManager.getCache(BeanIds.CMS_CATEGORY_EHCACHE, String.class,
				CmsCategory.class);
		if (cache == null) {
			throw new CmsException("Erreur lors de la configuration du cache " + BeanIds.CMS_CATEGORY_EHCACHE);
		}

		return cache;
	}

	@Bean(name = BeanIds.CMS_RESOURCES_EHCACHE)
	public Cache<String, DocumentContent> cacheResources(@Qualifier(BeanIds.EHCACHE_MANAGER) CacheManager cacheManager)
			throws CmsException {
		Cache<String, DocumentContent> cache = cacheManager.getCache(BeanIds.CMS_RESOURCES_EHCACHE, String.class,
				DocumentContent.class);
		if (cache == null) {
			throw new CmsException("Erreur lors de la configuration du cache " + BeanIds.CMS_RESOURCES_EHCACHE);
		}

		return cache;
	}
}
