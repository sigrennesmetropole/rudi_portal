package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;

public class MetadataNotFoundException extends AppServiceNotFoundException {

	private static final long serialVersionUID = -8761870388742307550L;

	public MetadataNotFoundException(DatasetNotFoundException cause) {
		super(new EmptyResultDataAccessException(cause.getMessage(), 1));
	}
}
