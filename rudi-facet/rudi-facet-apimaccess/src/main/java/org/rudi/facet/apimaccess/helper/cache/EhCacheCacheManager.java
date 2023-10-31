package org.rudi.facet.apimaccess.helper.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.ehcache.Status;
import org.ehcache.core.Ehcache;
import org.ehcache.core.EhcacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EhCacheCacheManager extends AbstractTransactionSupportingCacheManager {

	private EhcacheManager cacheManager;

	private Map<String, Ehcache<?, ?>> caches = new HashMap<>();

	/**
	 * Create a new EhCacheCacheManager for the given backing EhCache CacheManager.
	 * 
	 * @param cacheManager the backing EhCache {@link net.sf.ehcache.CacheManager}
	 */
	public EhCacheCacheManager(EhcacheManager cacheManager, Map<String, Ehcache<?, ?>> caches) {
		this.cacheManager = cacheManager;
		if (MapUtils.isNotEmpty(caches)) {
			this.caches.putAll(caches);
		}
	}

	/**
	 * Return the backing EhCache {@link net.sf.ehcache.CacheManager}.
	 */
	protected EhcacheManager getCacheManager() {
		return this.cacheManager;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Collection<Cache> loadCaches() {
		Assert.state(getCacheManager() != null, "No CacheManager set");

		Status status = cacheManager.getStatus();
		if (!Status.AVAILABLE.equals(status)) {
			throw new IllegalStateException(
					"An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
		}

		Collection<Cache> externalCaches = new LinkedHashSet<>(caches.size());
		for (Map.Entry<String, Ehcache<?, ?>> entry : caches.entrySet()) {
			log.info("loadCaches: {}", entry.getKey());
			externalCaches.add(new EhCacheCache(entry.getKey(), (Ehcache) entry.getValue()));
		}
		return externalCaches;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Cache getMissingCache(String name) {
		Assert.state(getCacheManager() != null, "No CacheManager set");
		log.info("getMissingCache: {}", name);
		return new EhCacheCache(name, (Ehcache) caches.get(name));
	}

}
