package org.rudi.facet.dataverse.api.exceptions;

import org.rudi.facet.dataverse.model.ApiResponseInfo;

public class DuplicateFileContentException extends DataverseAPIException {
	public DuplicateFileContentException(String message, ApiResponseInfo apiResponseInfo) {
		super(message, apiResponseInfo);
	}
}
