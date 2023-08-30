package org.rudi.facet.organization.helper.exceptions;

import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class AddUserToOrganizationException extends StruktureApiException {

	private static final long serialVersionUID = 229769297844495628L;

	public AddUserToOrganizationException(WebClientResponseException cause) {
		super(cause);
	}
}
