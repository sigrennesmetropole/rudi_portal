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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.TRANSPARENT_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransparentValidatorUT extends AbstractValidatorUT {
	private final TransparentValidator transparentValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("Transparent bien formé")
	void validate_transparent_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val transparentDto = getConnectorParameter(connector.getConnectorParameters(), TRANSPARENT_PARAMETER);
		val errors = transparentValidator.validate(transparentDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ transparent")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("Transparent mal formé")
	void validate_transparent_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val transparentDto = getConnectorParameter(connector.getConnectorParameters(), TRANSPARENT_PARAMETER);
		val errors = transparentValidator.validate(transparentDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ transparent")
				.isGreaterThan(0);
	}
}
