package org.rudi.microservice.projekt.service.replacer;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.confidentiality.ConfidentialityService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConfidentialityTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final ConfidentialityService confidentialityService;
	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final Confidentiality confidentiality = project.getConfidentiality();
		boolean confidentialityExist = false;
		//On tente de récuperer la confidentialité de base
		if(confidentiality != null) {
			confidentialityExist = confidentialityService.getConfidentialityByCode(confidentiality.getCode()) != null;
		}
		//Si c'est la première tentative de création de la confidentialité, on la crée
		if(!confidentialityExist && confidentiality != null) {
			confidentialityService.createConfidentiality(confidentiality);
		}

		if (confidentiality != null) {
			//On recupère la confidentialité en base (créé à tous les coups en haut) correspondant à celui voulu sur ce projet
			project.setConfidentiality(confidentialityService.getConfidentialityByCode(confidentiality.getCode()));
		}
	}
}

