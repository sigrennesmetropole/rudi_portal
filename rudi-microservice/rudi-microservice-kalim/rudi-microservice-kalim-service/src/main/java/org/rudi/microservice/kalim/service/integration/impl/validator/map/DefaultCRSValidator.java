package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.Set;

import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.DEFAULT_CRS_PARAMETER;

@Component
public class DefaultCRSValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		return validateFormatEPSG(DEFAULT_CRS_PARAMETER, connectorConnectorParametersInner.getValue());
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(DEFAULT_CRS_PARAMETER);
	}
}
