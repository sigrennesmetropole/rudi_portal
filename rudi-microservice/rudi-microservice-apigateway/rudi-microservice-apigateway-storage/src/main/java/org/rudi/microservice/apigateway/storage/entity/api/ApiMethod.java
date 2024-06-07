/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.storage.entity.api;

import org.springframework.http.HttpMethod;

/**
 * @author FNI18300
 *
 */
public enum ApiMethod {

	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

	public HttpMethod convert() {
		return HttpMethod.resolve(name());
	}

}
