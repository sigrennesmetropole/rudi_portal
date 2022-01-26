package org.rudi.common.service.exception;

import lombok.Getter;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.http.HttpStatus;

/**
 * Codes des status remontés en front en cas d'erreur de saisie de l'utilsateur.
 */
@Getter
public enum AppServiceExceptionsStatus {

	BAD_REQUEST("ERROR_FORBIDDEN_400", HttpStatus.BAD_REQUEST),
	UNAUTHORIZE("ERROR_UNAUTHORIZE_401", HttpStatus.UNAUTHORIZED),
	FORBIDDEN(HttpStatus.FORBIDDEN),
	NOT_FOUND(HttpStatus.NOT_FOUND),
	UNKNOWN_CLIENT_KEY("ERROR_UNKNOWN_CLIENT_KEY_480", 480),
	ACCESS_DENIED_METADATA_MEDIA("ERROR_ACCESS_DENIED_METADATA_MEDIA_483", 483),
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
