package org.rudi.microservice.projekt.service.replacer;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.service.reutilisationstatus.ReutilisationStatusService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReutilisationStatusTransientTestReplacer implements TransientDtoReplacerTest{
	private final ReutilisationStatusService reutilisationStatusService;
	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final ReutilisationStatus reutilisationStatus = project.getReutilisationStatus();
		boolean statusExists = false;

		//On tente de récupérer le status en BDD
		if(reutilisationStatus != null){
			statusExists = reutilisationStatusService.getReutilisationStatusByCode(reutilisationStatus.getCode()) != null;
		}

		//Si c'est la première tentative de création du status on le crée
		if(!statusExists && reutilisationStatus !=null){
			reutilisationStatusService.createReutilisationStatus(reutilisationStatus);
		}

		if(reutilisationStatus != null){
			//On récupère le status en base correspondant à celui voulu sur le projet.
			project.setReutilisationStatus(reutilisationStatusService.getReutilisationStatusByCode(reutilisationStatus.getCode()));
		}
	}

}
