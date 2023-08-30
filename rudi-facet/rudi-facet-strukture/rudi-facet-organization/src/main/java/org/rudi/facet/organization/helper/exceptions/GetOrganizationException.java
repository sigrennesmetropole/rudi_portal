package org.rudi.facet.organization.helper.exceptions;

import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class GetOrganizationException extends StruktureApiException {

	private static final long serialVersionUID = -5527906550768782992L;

	public GetOrganizationException(WebClientResponseException cause) {
		super(cause);
	}
}
