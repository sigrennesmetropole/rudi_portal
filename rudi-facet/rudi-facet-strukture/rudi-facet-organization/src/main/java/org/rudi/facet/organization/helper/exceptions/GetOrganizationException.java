package org.rudi.facet.organization.helper.exceptions;

import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class GetOrganizationException extends StruktureApiException {
	public GetOrganizationException(WebClientResponseException cause) {
		super(cause);
	}
}
