package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.HashSet;
import java.util.Set;

import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.ALPHANUMERIC_REGEX;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.STYLES_PARAMETER;

@Component
public class StylesValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		final String value = connectorConnectorParametersInner.getValue();
		String[] splitValue = value.split(",");
		for(String style : splitValue) {
			if (!style.matches(ALPHANUMERIC_REGEX)) { // Si un élément ne matche pas
				integrationRequestsErrors.add(buildError307(STYLES_PARAMETER, value));
			}
		}

		return integrationRequestsErrors;
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(STYLES_PARAMETER);
	}
}
