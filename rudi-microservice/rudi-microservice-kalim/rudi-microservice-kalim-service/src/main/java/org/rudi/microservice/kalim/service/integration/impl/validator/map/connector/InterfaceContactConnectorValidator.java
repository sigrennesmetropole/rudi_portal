package org.rudi.microservice.kalim.service.integration.impl.validator.map.connector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.rudi.facet.dataset.bean.InterfaceContract;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

public interface InterfaceContactConnectorValidator {

	default boolean accept(InterfaceContract interfaceContract){
		return interfaceContract == getInterfaceContract();
	}

	InterfaceContract getInterfaceContract();

	void validate(List<String> connectorParametersKeys, Set<IntegrationRequestErrorEntity> errorEntities);

	/**
	 * Construit une erreur de type 405
	 *
	 * @return l'erreur
	 */
	default IntegrationRequestErrorEntity buildError405() {
		return new IntegrationRequestErrorEntity(UUID.randomUUID(), IntegrationError.ERR_405.getCode(), IntegrationError.ERR_405.getMessage(), "connector", LocalDateTime.now());
	}
}
