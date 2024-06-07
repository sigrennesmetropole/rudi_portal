/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.projectkey;

import java.time.LocalDateTime;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.validator.AbstractLongIdInputValidator;
import org.rudi.microservice.acl.core.bean.ProjectKey;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ProjectKeyValidator extends AbstractLongIdInputValidator<ProjectKey> {

	@Override
	public void validateCreation(ProjectKey dto) throws AppServiceException {
		super.validateCreation(dto);
		validateCommon(dto);
	}

	private void validateCommon(ProjectKey dto) throws AppServiceBadRequestException {
		validateStringField(dto, "getName", "validation.error.name.mandatory");
		if (dto.getExpirationDate() != null && dto.getExpirationDate().isAfter(LocalDateTime.now())) {
			throw new AppServiceBadRequestException("Invalid expiration date (past date)");
		}
	}

	@Override
	public void validateUpdate(ProjectKey dto) throws AppServiceException {
		super.validateUpdate(dto);
		validateCommon(dto);
	}

}
