package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;

public class AbstractValidatorUT {
	protected static final String JSON_CONNECTOR_OK = "connector/correct-values.json";
	protected static final String JSON_CONNECTOR_NOK = "connector/incorrect-values.json";

	protected Connector createConnectorFromJson(JsonResourceReader reader, String jsonPath) throws IOException {
		return reader.read(jsonPath, Connector.class);
	}

	protected ConnectorConnectorParametersInner getConnectorParameter(List<ConnectorConnectorParametersInner> parameters, String key) throws NoSuchElementException {
		return parameters.stream().filter(element -> element.getKey().equals(key)).findFirst().orElseThrow();
	}
}
