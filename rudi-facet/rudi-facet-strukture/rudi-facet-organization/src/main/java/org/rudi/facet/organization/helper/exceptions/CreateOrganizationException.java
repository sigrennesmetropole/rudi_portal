package org.rudi.facet.organization.helper.exceptions;

import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class CreateOrganizationException extends StruktureApiException {

	private static final long serialVersionUID = 3715933300320377831L;

	public CreateOrganizationException(WebClientResponseException cause) {
		super(cause);
	}
}
