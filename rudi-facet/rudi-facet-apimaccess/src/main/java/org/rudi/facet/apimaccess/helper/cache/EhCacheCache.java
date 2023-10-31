/**
 * RUDI Portail
 */
package org.rudi.facet.apimaccess.helper.cache;

import java.util.concurrent.Callable;

import org.ehcache.Status;
import org.ehcache.core.Ehcache;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
public class EhCacheCache implements Cache {

	private final String name;

	private final Ehcache<Object, Object> cache;

	/**
	 * Create an {@link EhCacheCache} instance.
	 * 
	 * @param ehcache the backing Ehcache instance
	 */
	public EhCacheCache(String name, Ehcache<Object, Object> ehcache) {
		Assert.notNull(ehcache, "Ehcache must not be null");
		Status status = ehcache.getStatus();
		if (!Status.AVAILABLE.equals(status)) {
			throw new IllegalArgumentException(
					"An 'alive' Ehcache is required - current cache is " + status.toString());
		}
		this.cache = ehcache;
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final Ehcache<?, ?> getNativeCache() {
		return this.cache;
	}

	@Override
	@Nullable
	public ValueWrapper get(Object key) {
		Object value = lookup(key);
		return toValueWrapper(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T get(Object key, @Nullable Class<T> type) {
		log.info("EhCache.get {}", key);
		return (T) cache.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T get(Object key, Callable<T> valueLoader) {
		log.info("EhCache.get loader {}", key);
		Object element = lookup(key);
		log.info("EhCache get {}", key);
		if (element != null) {
			return (T) element;
		} else {
			try {
				return loadValue(key, valueLoader);
			} catch (Exception e) {
				log.warn("EhCache failed:" + key, e);
			}
		}
		return null;
	}

	private <T> T loadValue(Object key, Callable<T> valueLoader) {
		log.info("EhCache.loadValue {}", key);
		T value;
		try {
			value = valueLoader.call();
		} catch (Exception ex) {
			throw new ValueRetrievalException(key, valueLoader, ex);
		}
		put(key, value);
		return value;
	}

	@Override
	public void put(Object key, @Nullable Object value) {
		log.info("EhCache.put {}={}", key, value);
		cache.put(key, value);
	}

	@Override
	@Nullable
	public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
		log.info("EhCache.putIfAbsent {}={}", key, value);
		Object existingElement = cache.putIfAbsent(key, value);
		log.info("EhCache.putIfAbsent {}", existingElement != null);
		return toValueWrapper(existingElement);
	}

	@Override
	public void evict(Object key) {
		log.info("EhCache.evict {}", key);
		cache.remove(key);
	}

	@Override
	public boolean evictIfPresent(Object key) {
		log.info("EhCache.evictIfPresent {}", key);
		if (cache.containsKey(key)) {
			log.info("EhCache.evictIfPresent remove", key);
			cache.remove(key);
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		log.info("Clear EhCache...");
		this.cache.clear();
	}

	@Override
	public boolean invalidate() {
		log.info("EhCache.invalidate...");
		boolean notEmpty = (cache.iterator().hasNext());
		log.info("EhCache.invalidate {}", notEmpty);
		cache.clear();
		return notEmpty;
	}

	private Object lookup(Object key) {
		log.info("EhCache.lookup {}", key);
		return cache.get(key);
	}

	@Nullable
	private ValueWrapper toValueWrapper(Object value) {
		return (value != null ? new SimpleValueWrapper(value) : null);
	}

}
