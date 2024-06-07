/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class SearchApiException extends ApiGatewayApiException {

	private static final long serialVersionUID = 1L;

	public SearchApiException(WebClientResponseException cause) {
		super(cause);
	}

	public SearchApiException(Throwable cause) {
		super(cause);
	}

}
