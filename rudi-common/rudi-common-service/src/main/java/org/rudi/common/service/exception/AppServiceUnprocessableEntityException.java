package org.rudi.common.service.exception;

/**
 * La requête cliente est correcte (on ne lance pas de {@link AppServiceBadRequestException} ) mais elle ne peut pas être traitée par le serveur
 */
public class AppServiceUnprocessableEntityException extends AppServiceException {

	private static final long serialVersionUID = -4352024473026915727L;

	public AppServiceUnprocessableEntityException(String message) {
		super(message, AppServiceExceptionsStatus.UNPROCESSABLE_ENTITY);
	}

	public AppServiceUnprocessableEntityException(String message, Throwable cause) {
		super(message, cause, AppServiceExceptionsStatus.UNPROCESSABLE_ENTITY);
	}
}
