package org.rudi.microservice.projekt.service.helper.project.validator;

import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.projekt.core.bean.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectRequiredFieldsValidator implements ProjectValidator {
	/**
	 * @param project le projet DTO à valider
	 */
	@Override
	public void validate(Project project) {
		if(project.getUuid() == null){
			throw new IllegalArgumentException("UUID manquant");
		}

		if(StringUtils.isEmpty(project.getTitle())){
			throw new IllegalArgumentException("Titre manquant");
		}

		if(StringUtils.isEmpty(project.getDescription())){
			throw new IllegalArgumentException("Description manquante");
		}

		if(project.getType() == null){
			throw new IllegalArgumentException("Type manquant");
		}

		if(project.getConfidentiality() == null){
			throw new IllegalArgumentException("Confidentialité manquante");
		}

		// Inutile de tester le reutilisation Status, il est requis dans la création du DTO et donc ne peut pas être null.
	}
}
