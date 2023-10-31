package org.rudi.facet.apimaccess.exception;

public class APIsOperationWithIdException extends APIManagerException {

	private static final long serialVersionUID = 912505804241810046L;

	public APIsOperationWithIdException(String apiId, Throwable cause) {
		super(messageFromApiId(apiId), cause);
	}

	private static String messageFromApiId(String apiId) {
		return String.format("API operation failed for apiId = %s", apiId);
	}
}
