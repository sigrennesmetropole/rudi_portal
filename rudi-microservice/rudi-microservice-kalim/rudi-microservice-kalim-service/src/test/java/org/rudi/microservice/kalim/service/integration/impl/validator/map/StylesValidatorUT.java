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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.STYLES_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StylesValidatorUT extends AbstractValidatorUT {
	private final StylesValidator stylesValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("styles bien formé")
	void validate_styles_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val stylesDto = getConnectorParameter(connector.getConnectorParameters(), STYLES_PARAMETER);
		val errors = stylesValidator.validate(stylesDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ styles")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("styles mal formé")
	void validate_styles_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val stylesDto = getConnectorParameter(connector.getConnectorParameters(), STYLES_PARAMETER);
		val errors = stylesValidator.validate(stylesDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ styles")
				.isGreaterThan(0);
	}
}
