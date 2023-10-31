package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.HashSet;
import java.util.Set;

import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.TRANSPARENT_PARAMETER;

@Component
public class TransparentValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		final String value = connectorConnectorParametersInner.getValue();
		if (!Boolean.TRUE.toString().equalsIgnoreCase(value) && !Boolean.FALSE.toString().equalsIgnoreCase(value)) {
			integrationRequestsErrors.add(buildError201(TRANSPARENT_PARAMETER, value));
		}
		return integrationRequestsErrors;
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(TRANSPARENT_PARAMETER);
	}
}
