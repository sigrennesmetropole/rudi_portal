package org.rudi.microservice.konsult.service.customization.impl;

import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.core.customization.KeyFigureTypeData;
import org.rudi.microservice.konsult.service.customization.KeyFigureComputer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectKeyFigureComputerImpl implements KeyFigureComputer {

	private final ProjektHelper projektHelper;

	@Override
	public KeyFigureTypeData getAcceptedData() {
		return KeyFigureTypeData.PROJEKT;
	}

	@Override
	public void compute(KeyFigureData keyFigureData) {
		// comptage des projects VALIDATED
		try {
			Long projektCount = projektHelper.getNumberOfValidatedProjects();
			keyFigureData.setCount(projektCount);
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du nombre de projets de la plateforme", e);
			keyFigureData.setCount(getDefaultKeyFigureValue());
		}
	}
}
