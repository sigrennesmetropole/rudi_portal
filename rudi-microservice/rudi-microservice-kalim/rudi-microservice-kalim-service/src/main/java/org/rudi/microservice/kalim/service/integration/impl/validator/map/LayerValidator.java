package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.val;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.LAYER_PARAMETER;

@Component
public class LayerValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();

		final String value = connectorConnectorParametersInner.getValue();
		if (StringUtils.isBlank(value)) {
			final var errorMessage = String.format(IntegrationError.ERR_308.getMessage(), LAYER_PARAMETER);
			val error = new IntegrationRequestErrorEntity(UUID.randomUUID(), IntegrationError.ERR_307.getCode(), errorMessage, LAYER_PARAMETER, LocalDateTime.now());
			integrationRequestsErrors.add(error);
		}

		return integrationRequestsErrors;
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(LAYER_PARAMETER);
	}
}
