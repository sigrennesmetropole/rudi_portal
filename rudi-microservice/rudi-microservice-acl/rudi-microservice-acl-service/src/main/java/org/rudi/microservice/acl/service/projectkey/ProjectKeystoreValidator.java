/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.projectkey;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.validator.AbstractLongIdInputValidator;
import org.rudi.microservice.acl.core.bean.ProjectKeystore;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ProjectKeystoreValidator extends AbstractLongIdInputValidator<ProjectKeystore> {

	@Override
	public void validateCreation(ProjectKeystore dto) throws AppServiceException {
		super.validateCreation(dto);
		validateStringField(dto, "getProjectUuid", "validation.error.projectuuid.mandatory");

	}

	@Override
	public void validateUpdate(ProjectKeystore dto) throws AppServiceException {
		super.validateUpdate(dto);
		validateStringField(dto, "getProjectUuid", "validation.error.projectuuid.mandatory");
	}

}
