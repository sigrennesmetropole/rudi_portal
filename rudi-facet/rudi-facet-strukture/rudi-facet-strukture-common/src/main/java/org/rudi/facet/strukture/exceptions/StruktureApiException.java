package org.rudi.facet.strukture.exceptions;

import org.rudi.common.service.exception.MicroserviceException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class StruktureApiException extends MicroserviceException {

	private static final long serialVersionUID = 3614069698311775073L;

	public StruktureApiException(WebClientResponseException cause) {
		super("strukture", cause, cause.getStatusCode(), cause.getResponseBodyAsString());
	}

}
