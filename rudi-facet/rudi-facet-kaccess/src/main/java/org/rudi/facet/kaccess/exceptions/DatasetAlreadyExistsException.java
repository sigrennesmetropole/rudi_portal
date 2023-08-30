package org.rudi.facet.kaccess.exceptions;

import java.util.UUID;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;

public class DatasetAlreadyExistsException extends DataverseAPIException {

	private static final long serialVersionUID = 7747416670813084229L;

	public DatasetAlreadyExistsException(final UUID globalId) {
		super(String.format("Dataset with globalId %s already exists", globalId));
	}
}
