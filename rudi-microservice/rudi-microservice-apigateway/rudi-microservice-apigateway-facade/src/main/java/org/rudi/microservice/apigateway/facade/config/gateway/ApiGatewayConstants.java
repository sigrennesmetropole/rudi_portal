/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.gateway;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiGatewayConstants {

	public static final String APIGATEWAY_DATASETS_PATH = "/apigateway/datasets/";

	public static final int GLOBAL_ID_INDEX = 5;

	public static final int MEDIA_ID_INDEX = 7;

	public static final String APIGATEWAY_UNKNOWN_LOGIN = "unknown";

	public static final String APIGATEWAY_ERROR_HEADER_NAME = "X-Error-Message";

	public static final String PROJECTKEY_STORE_UUID = "projectKeyStoreUuid";
}
