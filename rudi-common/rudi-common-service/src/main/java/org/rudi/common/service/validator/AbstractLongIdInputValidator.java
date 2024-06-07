
package org.rudi.common.service.validator;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;

/**
 * @author FNI18300
 */
public abstract class AbstractLongIdInputValidator<T> extends AbstractValidator<T> implements InputValidator<T> {

	@Override
	public void validateCreation(T dto) throws AppServiceException {
		validateCommonLongId(dto);
	}

	@Override
	public void validateUpdate(T dto) throws AppServiceException {
		validateCommonLongId(dto);
		validateNotNullField(dto, GET_UUID_METHOD, "validation.error.uuid.mandatory");
	}

	protected void validateCommonLongId(T dto) throws AppServiceBadRequestException {
		if (dto == null) {
			throw new AppServiceBadRequestException("validation.error.input.mandatory");
		}
	}
}
