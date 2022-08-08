package org.rudi.microservice.projekt.service.type.impl.validator;

import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.springframework.stereotype.Component;

@Component
class ProjectTypeUuidValidator implements UpdateProjectTypeValidator {
	@Override
	public void validate(ProjectType projectType) throws IllegalArgumentException {
		if (projectType.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
	}
}
