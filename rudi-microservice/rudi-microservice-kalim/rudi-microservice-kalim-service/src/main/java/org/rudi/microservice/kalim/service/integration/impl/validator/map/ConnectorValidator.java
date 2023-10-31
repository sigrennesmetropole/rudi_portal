package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WFS_INTERFACE_CONTRACT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WFS_MANDATORY_PARAMS;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WMS_INTERFACE_CONTRACT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WMS_MANDATORY_PARAMS;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WMTS_INTERFACE_CONTRACT;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.WMTS_MANDATORY_PARAMS;

@Component
@RequiredArgsConstructor
public class ConnectorValidator {
	private final List<AbstractConnectorParametersValidator> mapFieldValidators;

	public Set<IntegrationRequestErrorEntity> validate(Connector connector) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		checkMandatoryParameters(connector.getInterfaceContract(), connector.getConnectorParameters(), integrationRequestsErrors);
		if (!integrationRequestsErrors.isEmpty()) { // Si des champs obligatoires sont manquants, on arrête là
			return integrationRequestsErrors;
		}
		// Pas de champs obligatoires manquants, validation de ceux renseignés
		for (ConnectorConnectorParametersInner parameterInner : connector.getConnectorParameters()) {
			mapFieldValidators.stream()
					.filter(element -> element.isToBeUse(parameterInner))
					.findFirst()
					.ifPresent(validator -> integrationRequestsErrors.addAll(validator.validate(parameterInner, connector.getInterfaceContract())));
		}
		return integrationRequestsErrors;
	}

	/**
	 * Vérifie pour chaque interface contract que tous ses champs obligatoires sont fournis dans le tableau des connectorParameters
	 *
	 * @param interfaceContract   wms/wmts/wfs
	 * @param connectorParameters tableau des paramètres fournis
	 * @param errorEntities       set d'erreur alimenté en cas de param obligatoire manquant
	 */
	private void checkMandatoryParameters(String interfaceContract, List<ConnectorConnectorParametersInner> connectorParameters, Set<IntegrationRequestErrorEntity> errorEntities) {
		List<String> connectorParametersKeys = connectorParameters.stream().map(ConnectorConnectorParametersInner::getKey).collect(Collectors.toList());

		boolean isWfsKo = interfaceContract.equalsIgnoreCase(WFS_INTERFACE_CONTRACT) && !connectorParametersKeys.containsAll(WFS_MANDATORY_PARAMS);
		boolean isWmsKo = interfaceContract.equalsIgnoreCase(WMS_INTERFACE_CONTRACT) && !connectorParametersKeys.containsAll(WMS_MANDATORY_PARAMS);
		boolean isWmtsKo = interfaceContract.equalsIgnoreCase(WMTS_INTERFACE_CONTRACT) && !connectorParametersKeys.containsAll(WMTS_MANDATORY_PARAMS);

		if (isWfsKo || isWmsKo || isWmtsKo) {
			errorEntities.add(buildError405());
		}
	}

	/**
	 * Construit une erreur de type 405
	 *
	 * @return l'erreur
	 */
	private IntegrationRequestErrorEntity buildError405() {
		return new IntegrationRequestErrorEntity(UUID.randomUUID(), IntegrationError.ERR_405.getCode(), IntegrationError.ERR_405.getMessage(), "connector", LocalDateTime.now());
	}
}
