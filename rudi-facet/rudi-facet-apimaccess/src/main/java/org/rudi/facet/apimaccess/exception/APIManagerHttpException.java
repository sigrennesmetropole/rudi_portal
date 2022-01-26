package org.rudi.facet.apimaccess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class APIManagerHttpException extends APIManagerException {
	public APIManagerHttpException(HttpStatus statusCode, String errorBody) {
		super(getMessage(statusCode, errorBody));
	}

	private static String getMessage(HttpStatus statusCode, String errorBody) {
		return String.format(
				"HTTP %s received from API Manager with body : %s",
				statusCode,
				errorBody);
	}

	public static ExchangeFilterFunction errorHandlingFilter() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			if (clientResponse.statusCode().isError()) {
				return APIManagerHttpException.errorFrom(clientResponse);
			} else {
				return Mono.just(clientResponse);
			}
		});
	}

	private static Mono<ClientResponse> errorFrom(ClientResponse clientResponse) {
		return clientResponse.bodyToMono(String.class)
				.flatMap(errorBody -> Mono.error(new APIManagerHttpException(clientResponse.statusCode(), errorBody)));
	}
}
