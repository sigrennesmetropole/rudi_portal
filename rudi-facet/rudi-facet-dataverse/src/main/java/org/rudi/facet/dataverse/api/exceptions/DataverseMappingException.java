package org.rudi.facet.dataverse.api.exceptions;

public class DataverseMappingException extends DataverseAPIException {

	private static final long serialVersionUID = -5209367027222010512L;

	public DataverseMappingException(Throwable e) {
		super("An error occurred during Dataverse fields mapping", e);
	}
}
