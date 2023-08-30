package org.rudi.facet.dataverse.api.exceptions;

import org.rudi.facet.dataverse.model.ApiResponseInfo;

/**
 * 
 * @author FNI18300
 *
 */
public class CannotReplaceUnpublishedFileException extends DataverseAPIException {

	private static final long serialVersionUID = 7219348943642554261L;

	public CannotReplaceUnpublishedFileException(String message, ApiResponseInfo apiResponseInfo) {
		super(message, apiResponseInfo);
	}
}
