package org.rudi.microservice.projekt.service.replacer;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.rudi.microservice.projekt.service.type.ProjectTypeService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTypeTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final ProjectTypeService projectTypeService;

	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final ProjectType projectType = project.getType();
		//On recupère le projectType en base s'il existe déjà
		boolean typeExist = false;
		if(projectType != null) {
			typeExist = projectTypeService.findByCode(projectType.getCode()) != null;
		}
		if (!typeExist && projectType != null) {
			projectTypeService.createProjectType(projectType);
		}
		if(projectType != null) {
			project.setType(projectTypeService.findByCode(projectType.getCode()));
		}
	}
}

