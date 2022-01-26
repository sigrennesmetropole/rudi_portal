package org.rudi.facet.dataverse.api.exceptions;

import org.rudi.facet.dataverse.model.ApiResponseInfo;

public class CannotReplaceUnpublishedFileException extends DataverseAPIException {
	public CannotReplaceUnpublishedFileException(String message, ApiResponseInfo apiResponseInfo) {
		super(message, apiResponseInfo);
	}
}
