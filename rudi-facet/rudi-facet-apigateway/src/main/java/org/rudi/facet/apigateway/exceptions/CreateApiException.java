/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class CreateApiException extends ApiGatewayApiException {

	private static final long serialVersionUID = 1L;

	public CreateApiException(Throwable cause) {
		super(cause);
	}

	public CreateApiException(WebClientResponseException cause) {
		super(cause);
	}

}
