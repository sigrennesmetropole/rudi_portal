/**
 * RUDI Portail
 */
package org.rudi.facet.apigateway.exceptions;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author FNI18300
 *
 */
public class DeleteApiException extends GetApiException {

	private static final long serialVersionUID = 1L;

	public DeleteApiException(WebClientResponseException cause) {
		super(cause);
	}

	public DeleteApiException(Throwable cause) {
		super(cause);
	}

}
