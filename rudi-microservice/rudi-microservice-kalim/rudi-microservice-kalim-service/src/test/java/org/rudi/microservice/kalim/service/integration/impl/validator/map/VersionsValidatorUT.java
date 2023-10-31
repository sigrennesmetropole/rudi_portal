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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.VERSION_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VersionsValidatorUT extends AbstractValidatorUT {
	private final VersionsValidator versionsValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("Version bien formée")
	void validate_version_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val versionDto = getConnectorParameter(connector.getConnectorParameters(), VERSION_PARAMETER);
		val errors = versionsValidator.validate(versionDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ version")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("Version mal formée")
	void validate_version_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val versionDto = getConnectorParameter(connector.getConnectorParameters(), VERSION_PARAMETER);
		val errors = versionsValidator.validate(versionDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ version")
				.isGreaterThan(0);
	}
}
