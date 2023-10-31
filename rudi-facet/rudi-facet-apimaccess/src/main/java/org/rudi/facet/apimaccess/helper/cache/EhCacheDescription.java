package org.rudi.facet.apimaccess.helper.cache;

import lombok.Data;

@Data
public class EhCacheDescription<K, V> {

	private String name;

	private Class<K> keyClass;

	private Class<V> valueClass;
}
