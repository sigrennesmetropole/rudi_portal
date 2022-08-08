package org.rudi.common.service.validator;

import org.rudi.common.service.exception.AppServiceException;

/**
 * @param <T> DTO type
 */
public interface ServiceDtoValidator<T> {
	void validate(T dto) throws AppServiceException;
}
