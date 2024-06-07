package org.rudi.facet.apigateway.exceptions;

import org.rudi.common.service.exception.MicroserviceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ApiGatewayApiException extends MicroserviceException {

	private static final long serialVersionUID = 1L;

	public ApiGatewayApiException(WebClientResponseException cause) {
		super("apigateway", cause, cause.getStatusCode(), cause.getResponseBodyAsString());
	}

	public ApiGatewayApiException(Throwable cause) {
		super("apigateway", cause,
				((cause instanceof WebClientResponseException) ? ((WebClientResponseException) cause).getStatusCode()
						: HttpStatus.NOT_ACCEPTABLE),
				((cause instanceof WebClientResponseException)
						? ((WebClientResponseException) cause).getResponseBodyAsString()
						: cause.getMessage()));

	}

}
