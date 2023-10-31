package org.rudi.facet.apimaccess.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryParameterKey {

	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";
	public static final String QUERY = "query";
	public static final String SORT_BY = "sortBy";
	public static final String SORT_ORDER = "sortOrder";
	public static final String API_ID = "apiId";
	public static final String API_DEFINITION = "apiDefinition";
	public static final String OPENAPI_VERSION = "openAPIVersion";
	public static final String APPLICATION_ID = "applicationId";
	public static final String SUBSCRIPTION_ID = "subscriptionId";
	public static final String KEYMAPPING_ID = "keyMappingId";
	public static final String POLICY_LEVEL = "policyLevel";
	public static final String POLICY_NAME = "policyName";
	public static final String ACTION = "action";
	public static final String BLOCK_STATE = "blockState";

	public static final int LIMIT_MAX_VALUE = 100;
	public static final String DEFAULT_API_VERSION = "1.0.0";
}
