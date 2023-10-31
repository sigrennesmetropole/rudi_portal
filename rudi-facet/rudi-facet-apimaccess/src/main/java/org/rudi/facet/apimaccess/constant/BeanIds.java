package org.rudi.facet.apimaccess.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanIds {

	public static final String API_MACCESS_WEBCLIENT = "apimaccess_webclient";
	public static final String API_MACCESS_SEARCH_MAPPER = "apimaccess_search_mapper";
	public static final String API_MACCESS_CACHE_CLIENT_REGISTRATION = "apimaccess_cache_client_registration";

	public static final String API_MACCESS_EHCACHE_MANAGER = "apimaccess_ehcache_manager";
	public static final String API_MACCESS_CACHE_MANAGER = "apimaccess_cache_manager";

	public static final String API_MACCESS_TEMPLATE_BY_INTERFACE_CONTRACT = "templatesByInterfaceContract";
	public static final String API_MACCESS_TEMPLATE_EXISTENCE = "templatesExistence";
	public static final String API_MACCESS_CLIENT_REGISTRATION = "client_registration";
}
