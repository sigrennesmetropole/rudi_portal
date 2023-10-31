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
import static org.rudi.microservice.kalim.service.integration.impl.validator.map.ConnectorParametersConstants.MAX_FEATURES_PARAMETER;

@KalimSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MaxFeaturesValidatorUT extends AbstractValidatorUT {
	private final MaxFeaturesValidator maxFeaturesValidator;
	private final JsonResourceReader jsonResourceReader;

	@Test
	@DisplayName("MaxFeatures bien formé")
	void validate_max_features_ok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_OK);
		val maxFeaturesDto = getConnectorParameter(connector.getConnectorParameters(), MAX_FEATURES_PARAMETER);
		val errors = maxFeaturesValidator.validate(maxFeaturesDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il n'y a pas d'erreurs sur le champ maxFeatures")
				.isEqualTo(0);
	}

	@Test
	@DisplayName("MaxFeatures mal formé")
	void validate_max_features_nok() throws IOException {
		val connector = createConnectorFromJson(jsonResourceReader, JSON_CONNECTOR_NOK);
		val maxFeaturesDto = getConnectorParameter(connector.getConnectorParameters(), MAX_FEATURES_PARAMETER);
		val errors = maxFeaturesValidator.validate(maxFeaturesDto, connector.getInterfaceContract());

		assertThat(errors.size())
				.as("Il y a des erreurs sur le champ maxFeatures")
				.isGreaterThan(0);
	}
}
