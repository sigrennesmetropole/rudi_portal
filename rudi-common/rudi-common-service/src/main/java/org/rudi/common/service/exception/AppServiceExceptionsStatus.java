package org.rudi.common.service.exception;

import org.assertj.core.util.VisibleForTesting;
import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Codes des status remontés en front en cas d'erreur de saisie de l'utilsateur.
 */
@Getter
public enum AppServiceExceptionsStatus {

	BAD_REQUEST("ERROR_FORBIDDEN_400", HttpStatus.BAD_REQUEST),
	UNAUTHORIZE("ERROR_UNAUTHORIZE_401", HttpStatus.UNAUTHORIZED),
	FORBIDDEN(HttpStatus.FORBIDDEN),
	NOT_FOUND(HttpStatus.NOT_FOUND),
	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),
	BUSINESS_ERROR("BUSINESS_ERROR", 409),
	UNKNOWN_CLIENT_KEY("ERROR_UNKNOWN_CLIENT_KEY_480", 480),
	ACCESS_DENIED_METADATA_MEDIA("ERROR_ACCESS_DENIED_METADATA_MEDIA_483", 483),
	MISSING_FIELD_ACCOUNT("ERROR_MISSING_FIELD_ACCOUNT_441", 441),
	LOGIN_NOT_MAIL("ERROR_LOGIN_NOT_MAIL_442", 442),
	LOGIN_ALREADY_EXISTS("ERROR_LOGIN_ALREADY_EXISTS_443", 443),
	PASSWORD_LENGTH("ERROR_PASSWORD_LENGTH_444", 444),
	MISSING_FIELD_PASSWORD_CHANGE("ERROR_MISSING_FIELD_PASSWORD_CHANGE_445", 445),
	SEND_EMAIL_ACTIVATION("ERROR_SEND_EMAIL_CONFIRMATION_545", 545),
	SEND_EMAIL_REGISTRATION("ERROR_SEND_EMAIL_REGISTRATION_546", 546),
	BAD_GATEWAY(HttpStatus.BAD_GATEWAY);
	
	private final String stringValue;
	private final HttpStatus httpStatus;
	private final int customHttpStatusCode;

	AppServiceExceptionsStatus(String stringValue, HttpStatus httpStatus) {
		this.stringValue = stringValue;
		this.httpStatus = httpStatus;
		customHttpStatusCode = 0;
	}

	AppServiceExceptionsStatus(String stringValue, int customHttpStatusCode) {
		this.stringValue = stringValue;
		this.httpStatus = null;
		this.customHttpStatusCode = customHttpStatusCode;
	}

	AppServiceExceptionsStatus(HttpStatus httpStatus) {
		this(stringValueFrom(httpStatus), httpStatus);
	}

	@VisibleForTesting
	protected static String stringValueFrom(HttpStatus httpStatus) {
		return "ERROR_" + httpStatus.name() + "_" + httpStatus.value();
	}

}
