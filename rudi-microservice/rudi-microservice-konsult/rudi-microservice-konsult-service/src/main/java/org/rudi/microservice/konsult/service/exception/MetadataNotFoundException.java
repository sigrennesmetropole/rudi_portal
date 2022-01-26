package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;

public class MetadataNotFoundException extends AppServiceException {
	public MetadataNotFoundException(DatasetNotFoundException cause) {
		super(cause.getMessage(), cause, AppServiceExceptionsStatus.NOT_FOUND);
	}
}
