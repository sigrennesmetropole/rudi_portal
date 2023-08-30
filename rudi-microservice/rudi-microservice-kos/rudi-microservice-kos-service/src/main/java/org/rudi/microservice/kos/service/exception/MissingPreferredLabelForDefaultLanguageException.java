package org.rudi.microservice.kos.service.exception;

import org.rudi.common.service.exception.AppServiceBadRequestException;

/**
 * Absence de libellé pour la langue par défaut
 * 
 * @author FNI18300
 *
 */
public class MissingPreferredLabelForDefaultLanguageException extends AppServiceBadRequestException {

	private static final long serialVersionUID = 2511684137086355589L;

	public MissingPreferredLabelForDefaultLanguageException(String conceptCode, String defaultLanguage) {
		super(String.format("Concept \"%s\" must have at least one preferred label for default language \"%s\"",
				conceptCode, defaultLanguage));
	}
}
