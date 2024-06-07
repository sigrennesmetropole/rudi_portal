/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class SearchThrottlingException extends ApiGatewayApiException {

	private static final long serialVersionUID = 1L;

	public SearchThrottlingException(WebClientResponseException cause) {
		super(cause);
	}

	public SearchThrottlingException(Throwable cause) {
		super(cause);
	}

}
