package org.rudi.wso2.mediation;

import java.io.IOException;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageContextUtilsTest {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();

	@Mock
	private MessageContext messageContext;

	@Test
	void getAuthenticatedUserLogin() throws IOException {
		final var messageContextValues = jsonResourceReader.readMap("wso2/sample-messageContext.json");
		bindProperties(messageContextValues);

		final var authenticatedUserLogin = MessageContextUtils.getAuthenticatedUserLogin(messageContext);
		assertThat(authenticatedUserLogin).isEqualTo("kad.merad@gmail.com");
	}

	private void bindProperties(Map<String, Object> messageContextValues) {
		when(messageContext.getProperty(anyString())).then(answer -> {
			final var properties = (Map<String, Object>) messageContextValues.get("properties");
			return properties.get(answer.getArgument(0, String.class));
		});
	}
}
