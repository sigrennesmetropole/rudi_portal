package org.rudi.common.facade.exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ValidationException;

@ControllerAdvice
public class AppExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionHandler.class);

	@ExceptionHandler(org.rudi.common.service.exception.AppServiceException.class)
	protected ResponseEntity<Object> handleExceptionService(final AppServiceException ex, final WebRequest request) {

		LOGGER.error(ex.getMessage(), ex);

		final AppServiceExceptionsStatus appExceptionStatusCode = ex.getAppExceptionStatusCode();
		if (appExceptionStatusCode != null) {
			final HttpStatus httpStatus = appExceptionStatusCode.getHttpStatus();
			if (httpStatus != null) {
				return ResponseEntity.status(httpStatus).build();
			}

			final int customHttpStatusCode = appExceptionStatusCode.getCustomHttpStatusCode();
			if (customHttpStatusCode > 0) {
				return ResponseEntity.status(customHttpStatusCode).build();
			}

		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler({ AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class })
	protected ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

	}

	@ExceptionHandler({ AppServiceNotFoundException.class })
	protected ResponseEntity<Object> handleNotFoundException(final Exception ex, final WebRequest request) {
		LOGGER.error("Ressource not found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@ExceptionHandler({ IllegalAccessException.class })
	protected ResponseEntity<Object> handleIllegalAccessException(final Exception ex, final WebRequest request) {
		LOGGER.error("Ressource not authorized");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@ExceptionHandler({ ValidationException.class, JsonParseException.class, HttpMessageNotReadableException.class,
			MethodArgumentNotValidException.class })
	protected ResponseEntity<Object> handleValidationException(final Exception ex, final WebRequest request) {
		LOGGER.error("Ressource not valid");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBody(ex, HttpStatus.BAD_REQUEST));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleException(final Exception ex, final WebRequest request) {
		LOGGER.error("Unknown error", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(buildBody(ex, HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private String buildBody(Exception ex, HttpStatus status) {
		return "{ \"code\": \"" + status.toString() + "\", \"label\":\"" + ex.getMessage() + "\"}";
	}
}
