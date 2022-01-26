package org.rudi.facet.dataverse.api.exceptions;

public class DataverseMappingException extends DataverseAPIException {
	public DataverseMappingException(Throwable e) {
		super("An error occurred during Dataverse fields mapping", e);
	}
}
