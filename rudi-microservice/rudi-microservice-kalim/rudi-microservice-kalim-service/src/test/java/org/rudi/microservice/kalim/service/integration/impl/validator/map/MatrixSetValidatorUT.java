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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.MATRIX_SET_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatrixSetValidatorUT extends AbstractValidatorUT {
	private final MatrixSetValidator matrixSetValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("MatrixSet bien formé")
	void validate_matrix_set_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val matrixSetDto = getConnectorParameter(connector.getConnectorParameters(), MATRIX_SET_PARAMETER);
		val errors = matrixSetValidator.validate(matrixSetDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le matrixSet")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("MatrixSet mal formé")
	void validate_matrix_set_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val matrixSetDto = getConnectorParameter(connector.getConnectorParameters(), MATRIX_SET_PARAMETER);
		val errors = matrixSetValidator.validate(matrixSetDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le matrixSet")
				.isGreaterThan(0);
	}
}
