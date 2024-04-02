package org.rudi.microservice.kalim.service.integration.impl.validator.map.connector.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rudi.facet.dataset.bean.InterfaceContract;
import org.rudi.microservice.kalim.service.integration.impl.validator.map.connector.InterfaceContactConnectorValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WMTS_MANDATORY_PARAMS;

@Component
public class InterfaceContractWmtsValidator implements InterfaceContactConnectorValidator {
	@Override
	public InterfaceContract getInterfaceContract() {
		return InterfaceContract.WMTS;
	}

	@Override
	public void validate(List<String> connectorParametersKeys, Set<IntegrationRequestErrorEntity> errorEntities) {
		// Demande Sonar : transformation en HashSet pour des raisons de performance
		Set<String> connectorParametersKeySet = new HashSet<>(connectorParametersKeys);

		if(!connectorParametersKeySet.containsAll(WMTS_MANDATORY_PARAMS)){
			errorEntities.add(buildError405());
		}
	}
}
