package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.ApplicationTokenGenerateRequest;

public class ApplicationTokenGenerationException extends APIManagerException {

	private static final long serialVersionUID = 9138385033069251777L;

	public ApplicationTokenGenerationException(String applicationId, String keyMappingId,
			ApplicationTokenGenerateRequest applicationTokenGenerateRequest, String username, Throwable cause) {
		super(String.format(
				"Token generation failed for username=% on applicationId=%s with keyMappingId=%s and applicationTokenGenerateRequest : %s",
				username, applicationId, keyMappingId, applicationTokenGenerateRequest), cause);
	}
}
