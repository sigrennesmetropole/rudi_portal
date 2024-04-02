package org.rudi.microservice.konsult.service.customization.impl;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.core.customization.KeyFigureTypeData;
import org.rudi.microservice.konsult.service.customization.KeyFigureComputer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserKeyFigureComputerImpl implements KeyFigureComputer {
	
	private final ACLHelper aclHelper;

	@Override
	public KeyFigureTypeData getAcceptedData() {
		return KeyFigureTypeData.USER;
	}

	@Override
	public void compute(KeyFigureData keyFigureData) {
		try {
			Long userCount = aclHelper.getUserCount();
			keyFigureData.setCount(userCount);
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du nombre d'utilisateurs de la plateforme", e);
			keyFigureData.setCount(getDefaultKeyFigureValue());
		}

	}
}
