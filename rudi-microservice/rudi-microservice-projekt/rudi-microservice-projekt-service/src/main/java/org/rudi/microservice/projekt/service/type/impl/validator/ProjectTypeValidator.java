package org.rudi.microservice.projekt.service.type.impl.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.ProjectType;

public interface ProjectTypeValidator {
	void validate(ProjectType projectType) throws AppServiceException;
}
