package org.rudi.common.service.validator;

import org.rudi.common.service.exception.AppServiceException;

/**
 * @param <T> DTO type
 */
public interface InputValidator<T> {

	void validateCreation(T dto) throws AppServiceException;

	void validateUpdate(T dto) throws AppServiceException;
}
