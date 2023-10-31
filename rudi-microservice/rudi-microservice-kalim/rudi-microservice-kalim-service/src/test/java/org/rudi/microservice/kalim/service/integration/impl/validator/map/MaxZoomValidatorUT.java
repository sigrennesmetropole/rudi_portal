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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.MAX_ZOOM_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MaxZoomValidatorUT extends AbstractValidatorUT {
	private final MaxZoomValidator maxZoomValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("MaxZoom bien formé")
	void validate_max_zoom_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val maxZoomDto = getConnectorParameter(connector.getConnectorParameters(), MAX_ZOOM_PARAMETER);
		val errors = maxZoomValidator.validate(maxZoomDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ MaxZoom")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("MaxZoom mal formé")
	void validate_max_zoom_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val maxZoomDto = getConnectorParameter(connector.getConnectorParameters(), MAX_ZOOM_PARAMETER);
		val errors = maxZoomValidator.validate(maxZoomDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ MaxZoom")
				.isGreaterThan(0);
	}
}
