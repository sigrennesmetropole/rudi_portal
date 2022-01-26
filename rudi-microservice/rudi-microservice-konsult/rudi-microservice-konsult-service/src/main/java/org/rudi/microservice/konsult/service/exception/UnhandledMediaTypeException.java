package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kaccess.bean.Media;

public class UnhandledMediaTypeException extends AppServiceException {
	public UnhandledMediaTypeException(Media.MediaTypeEnum mediaType) {
		super("Type de média " + mediaType + " non pris en charge");
	}
}
