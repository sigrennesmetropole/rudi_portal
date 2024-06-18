package org.rudi.microservice.konsult.service.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanIds {
	public static final String CUSTOMIZATION_CACHE_MANAGER = "customizationCacheManager";
	public static final String CUSTOMIZATION_RESOURCES_CACHE = "customizationResources";
	public static final String CUSTOMIZATION_DATA_CACHE = "customizationData";

	public static final String SITEMAP_CACHE_MANAGER = "sitemapCacheManager";
	public static final String SITEMAP_RESOURCES_CACHE = "sitemapResources";
	public static final String SITEMAP_DATA_CACHE = "sitemapData";
}
