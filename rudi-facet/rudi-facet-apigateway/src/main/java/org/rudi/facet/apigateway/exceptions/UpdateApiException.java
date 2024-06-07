/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class UpdateApiException extends ApiGatewayApiException {

	private static final long serialVersionUID = 1L;

	public UpdateApiException(WebClientResponseException cause) {
		super(cause);
	}

	public UpdateApiException(Throwable cause) {
		super(cause);
	}

}
