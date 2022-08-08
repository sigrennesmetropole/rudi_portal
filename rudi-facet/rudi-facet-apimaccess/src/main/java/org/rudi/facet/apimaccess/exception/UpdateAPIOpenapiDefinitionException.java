package org.rudi.facet.apimaccess.exception;

public class UpdateAPIOpenapiDefinitionException extends APIManagerException {

	public UpdateAPIOpenapiDefinitionException(String apiId, String apiDefinition, Throwable cause) {
		super(message(apiId, apiDefinition), cause);
	}

	private static String message(String apiId, String apiDefinition) {
		return String.format("Fail to update API with id = %s with the following definition : %s", apiId, apiDefinition);
	}
}
