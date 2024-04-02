package org.rudi.facet.cms.impl.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanIds {

	public static final String EHCACHE_MANAGER = "cms_magnolia_ehcache_manager";
	public static final String CACHE_MANAGER = "cms_magnolia_cache_manager";

	public static final String CMS_CATEGORY_EHCACHE = "cms_magnolia_category";
	public static final String CMS_ASSET_EHCACHE = "cms_magnolia_asset";
	public static final String CMS_RESOURCES_EHCACHE = "cms_magnolia_resource";

	public static final String CMS_WEB_CLIENT = "cms-web-client";

	public static final String CMS_HTTP_CLIENT = "cms-http-client";
}
