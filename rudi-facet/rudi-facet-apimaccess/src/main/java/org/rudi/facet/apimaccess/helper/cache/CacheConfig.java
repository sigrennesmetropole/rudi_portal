package org.rudi.facet.apimaccess.helper.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.net.URL;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CACHE_CLIENT_REGISTRATION;

@Configuration
@SuppressWarnings({ "java:S2095", "java:S1075" })
@EnableCaching
public class CacheConfig {

	private static final String CONFIG_FILE_PATH = "/cache/ehcache_config.xml";

	@Bean(name = API_MACCESS_CACHE_CLIENT_REGISTRATION)
	public Cache<String, ClientRegistration> cache() throws APIManagerException {
		final URL myUrl = getClass().getResource(CONFIG_FILE_PATH);
		if (myUrl == null) {
			throw new APIManagerException(String.format("Impossible de trouver le fichier de configuration EhCache : %s", CONFIG_FILE_PATH));
		}
		XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
		CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();

		Cache<String, ClientRegistration> cache = cacheManager.getCache("client_registration", String.class, ClientRegistration.class);
		if (cache == null) {
			throw new APIManagerException("Erreur lors de la configuration du cache client_registration");
		}

		return cache;
	}
}
