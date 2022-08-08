package org.rudi.microservice.kos.service.exception;

import org.rudi.common.service.exception.AppServiceBadRequestException;

public class MissingPreferredLabelForDefaultLanguageException extends AppServiceBadRequestException {
	public MissingPreferredLabelForDefaultLanguageException(String conceptCode, String defaultLanguage) {
		super(String.format("Concept \"%s\" must have at least one preferred label for default language \"%s\"", conceptCode, defaultLanguage));
	}
}
