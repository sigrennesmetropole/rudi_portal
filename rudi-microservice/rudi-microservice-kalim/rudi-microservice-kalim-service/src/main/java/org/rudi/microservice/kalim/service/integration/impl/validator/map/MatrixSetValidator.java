package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.MATRIX_SET_PARAMETER;

@Component
public class MatrixSetValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		final String value = connectorConnectorParametersInner.getValue();
		if (StringUtils.isBlank(value)) {
			integrationRequestsErrors.add(buildError307(MATRIX_SET_PARAMETER, value));
		}
		return integrationRequestsErrors;
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(MATRIX_SET_PARAMETER);
	}
}
