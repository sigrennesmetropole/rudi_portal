package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.microservice.kalim.service.KalimSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.LAYER_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LayerValidatorUT extends AbstractValidatorUT {
	private final LayerValidator layerValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("Layer bien formée")
	void validate_layer_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val layerDto = getConnectorParameter(connector.getConnectorParameters(), LAYER_PARAMETER);
		val errors = layerValidator.validate(layerDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ layer")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("Layer mal formée")
	void validate_layer_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val layerDto = getConnectorParameter(connector.getConnectorParameters(), LAYER_PARAMETER);
		val errors = layerValidator.validate(layerDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ layer")
				.isGreaterThan(0);
	}
}
