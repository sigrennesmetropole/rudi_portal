package org.rudi.facet.dataverse.api.exceptions;

import org.rudi.facet.dataverse.model.ApiResponseInfo;

public class DuplicateFileContentException extends DataverseAPIException {

	private static final long serialVersionUID = -8731429329357637786L;

	public DuplicateFileContentException(String message, ApiResponseInfo apiResponseInfo) {
		super(message, apiResponseInfo);
	}
}
