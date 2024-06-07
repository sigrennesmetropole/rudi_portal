/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class GetApiException extends ApiGatewayApiException {

	private static final long serialVersionUID = 1L;

	public GetApiException(WebClientResponseException cause) {
		super(cause);
	}

	public GetApiException(Throwable cause) {
		super(cause);
	}

}
