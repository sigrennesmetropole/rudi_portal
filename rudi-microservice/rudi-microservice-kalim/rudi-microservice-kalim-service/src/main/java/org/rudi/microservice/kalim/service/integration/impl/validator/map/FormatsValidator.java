package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.HashSet;
import java.util.Set;

import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.APP_JSON_FORMAT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.FORMATS_PARAMETER;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.ALPHANUMERIC_REGEX;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.GML2_FORMAT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.GML3_FORMAT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WFS_INTERFACE_CONTRACT;

@Component
public class FormatsValidator extends AbstractConnectorParametersValidator {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(ConnectorConnectorParametersInner connectorConnectorParametersInner, String interfaceContract) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		final String value = connectorConnectorParametersInner.getValue();
		if (WFS_INTERFACE_CONTRACT.equalsIgnoreCase(interfaceContract)) { // WFS
			if (!(value.equals(APP_JSON_FORMAT) || value.equals(GML2_FORMAT) || value.equals(GML3_FORMAT))) {
				integrationRequestsErrors.add(
						buildError302(FORMATS_PARAMETER, value, String.join(",", APP_JSON_FORMAT, GML2_FORMAT, GML3_FORMAT))
				);
			}
		} else { // WMS, WMTS
			String[] splitValue = value.split("/");
			if (splitValue.length != 2) { // Un format MIME à 2 parties séparées par un slash (type/sous-type)
				integrationRequestsErrors.add(buildError307(FORMATS_PARAMETER, value));
			} else {
				boolean matchType = splitValue[0].matches(ALPHANUMERIC_REGEX);
				boolean matchSubType = splitValue[1].matches(ALPHANUMERIC_REGEX);
				if (!matchType || !matchSubType) { // Si le type ou le sous-type ne matche pas la REGEX
					integrationRequestsErrors.add(buildError307(FORMATS_PARAMETER, value));
				}
			}
		}
		return integrationRequestsErrors;
	}

	@Override
	public boolean isToBeUse(ConnectorConnectorParametersInner connectorConnectorParametersInner) {
		return connectorConnectorParametersInner.getKey().equals(FORMATS_PARAMETER);
	}
}
