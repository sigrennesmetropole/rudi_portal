package org.rudi.facet.organization.helper.exceptions;

import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class CreateOrganizationException extends StruktureApiException {
	public CreateOrganizationException(WebClientResponseException cause) {
		super(cause);
	}
}
