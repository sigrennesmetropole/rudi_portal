package org.rudi.facet.apimaccess.helper.cache;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CACHE_CLIENT_REGISTRATION;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CACHE_MANAGER;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CLIENT_REGISTRATION;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_EHCACHE_MANAGER;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_TEMPLATE_BY_INTERFACE_CONTRACT;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_TEMPLATE_EXISTENCE;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.core.Ehcache;
import org.ehcache.core.EhcacheManager;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataset.bean.InterfaceContract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import freemarker.template.Template;

@Configuration
@SuppressWarnings({ "java:S2095", "java:S1075" })
@EnableCaching
public class CacheConfig {

	private static final String CONFIG_FILE_PATH = "/cache/ehcache_config.xml";

	@Bean(name = API_MACCESS_CACHE_MANAGER)
	public org.springframework.cache.CacheManager cacheManager(
			@Qualifier(API_MACCESS_EHCACHE_MANAGER) CacheManager cacheManager) {
		Map<String, Ehcache<?, ?>> caches = new HashMap<>();
		if (cacheManager instanceof EhcacheManager) {
			caches.put(API_MACCESS_TEMPLATE_EXISTENCE, (Ehcache<?, ?>) cacheManager
					.getCache(API_MACCESS_TEMPLATE_EXISTENCE, InterfaceContract.class, Boolean.class));
			caches.put(API_MACCESS_TEMPLATE_BY_INTERFACE_CONTRACT, (Ehcache<?, ?>) cacheManager
					.getCache(API_MACCESS_TEMPLATE_BY_INTERFACE_CONTRACT, InterfaceContract.class, Template.class));
		}
		return new EhCacheCacheManager((EhcacheManager) cacheManager, caches);
	}

	@Bean(name = API_MACCESS_EHCACHE_MANAGER)
	public CacheManager ehCacheManager() throws APIManagerException {
		final URL myUrl = getClass().getResource(CONFIG_FILE_PATH);
		if (myUrl == null) {
			throw new APIManagerException(
					String.format("Impossible de trouver le fichier de configuration EhCache : %s", CONFIG_FILE_PATH));
		}
		XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
		CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();
		return cacheManager;
	}

	@Bean(name = API_MACCESS_CACHE_CLIENT_REGISTRATION)
	public Cache<String, ClientRegistration> cache(@Qualifier(API_MACCESS_EHCACHE_MANAGER) CacheManager cacheManager)
			throws APIManagerException {
		Cache<String, ClientRegistration> cache = cacheManager.getCache(API_MACCESS_CLIENT_REGISTRATION, String.class,
				ClientRegistration.class);
		if (cache == null) {
			throw new APIManagerException("Erreur lors de la configuration du cache client_registration");
		}

		return cache;
	}
}
