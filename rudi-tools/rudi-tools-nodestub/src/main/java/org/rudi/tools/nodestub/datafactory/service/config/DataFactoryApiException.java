package org.rudi.tools.nodestub.datafactory.service.config;

import org.rudi.common.service.exception.ExternalServiceException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class DataFactoryApiException extends ExternalServiceException {
	private static final long serialVersionUID = -7561736289302516925L;

	public DataFactoryApiException(WebClientResponseException cause) {
		super("data-factory", cause);
	}

	public DataFactoryApiException(Throwable cause) {
		super("data-factory", cause);
	}
}
