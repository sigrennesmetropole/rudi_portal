package org.rudi.facet.kaccess.exceptions;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;

import java.util.UUID;

public class DatasetAlreadyExists extends DataverseAPIException {
	public DatasetAlreadyExists(final UUID globalId) {
		super(String.format("Dataset with globalId %s already exists", globalId));
	}
}
