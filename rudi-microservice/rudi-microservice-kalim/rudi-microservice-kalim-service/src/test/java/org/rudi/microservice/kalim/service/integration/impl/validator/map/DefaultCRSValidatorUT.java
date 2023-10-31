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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.DEFAULT_CRS_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultCRSValidatorUT extends AbstractValidatorUT {
	private final JsonResourceReader jsonResourceReader;
	private final DefaultCRSValidator defaultCRSValidator;

	@Test
	@DisplayName("Default_crs bien formé")
	void validate_default_crs_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val defaultCrsDto = getConnectorParameter(connector.getConnectorParameters(), DEFAULT_CRS_PARAMETER);
		val errors = defaultCRSValidator.validate(defaultCrsDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ default_crs")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("Default_crs mal formé")
	void validate_default_crs_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val defaultCrsDto = getConnectorParameter(connector.getConnectorParameters(), DEFAULT_CRS_PARAMETER);
		val errors = defaultCRSValidator.validate(defaultCrsDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le cahmp default_crs")
				.isGreaterThan(0);
	}
}
