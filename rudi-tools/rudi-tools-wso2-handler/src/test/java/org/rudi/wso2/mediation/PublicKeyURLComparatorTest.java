package org.rudi.wso2.mediation;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.apimgt.api.model.API;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.rudi.wso2.mediation.PublicKeyURLComparator.PUBLIC_KEY_URL_ADDITIONAL_PROPERTY;
import static org.rudi.wso2.mediation.PublicKeyURLComparator.PUBLIC_KEY_URL_PROPERTY;

@ExtendWith(MockitoExtension.class)
class PublicKeyURLComparatorTest extends TestCase {
	@InjectMocks
	PublicKeyURLComparator publicKeyURLComparator;
	@Mock
	EncryptedMediaHandler encryptedMediaHandler;
	@Mock
	API engagedApi;

	@SuppressWarnings("HttpUrlsUsage") // on utilise des URL en http pour les besoins du test
	@ParameterizedTest
	@CsvSource({
			"https://rudi.open-dev.com/konsult/v1/encryption-key, https://rudi.open-dev.com/konsult/v1/encryption-key, true",
			"https://rudi.open-dev.com/konsult/v1/encryption-key, http://rudi.open-dev.com/konsult/v1/encryption-key, true",
			"http://rudi.open-dev.com/konsult/v1/encryption-key, https://rudi.open-dev.com/konsult/v1/encryption-key, true",
			"https://rudi.open-dev.com/konsult/v1/encryption-key, file:rudi.open-dev.com/konsult/v1/encryption-key, true",
			"https://rudi.open-dev.com/konsult/v1/encryption-key, rudi.open-dev.com/konsult/v1/encryption-key, true",
			"rudi.open-dev.com/konsult/v1/encryption-key, 10.50.1.45/konsult/v1/encryption-key, true",
			"rudi.open-dev.com/konsult/v1/encryption-key, rudi.bzh/konsult/v1/encryption-key, false",
			"rudi.open-dev.com/konsult/v1/encryption-key, rudi.open-dev.com/konsult/v2/encryption-key, false",
			"rudi.open-dev.com/konsult/v1/encryption-key,, false",
			", rudi.open-dev.com/konsult/v1/encryption-key, false",
			"rudi.open-dev.com:80/konsult/v1/encryption-key, rudi.open-dev.com:443/konsult/v1/encryption-key, true",
			"rudi.open-dev.com/konsult/v1/encryption-key, unknown-host/konsult/v1/encryption-key, false",
			"unknown-host/konsult/v1/encryption-key, unknown-host/konsult/v1/encryption-key, true",
			"file:../../rudi-microservice/rudi-microservice-konsult/rudi-microservice-konsult-facade/src/main/resources/encryption_key_public.key, file:../../rudi-microservice/rudi-microservice-konsult/rudi-microservice-konsult-facade/src/main/resources/encryption_key_public.key, true",
	})
	void usesSamePublicKey(final String portalPublicKeyURL, final String apiPublicKeyURL, final boolean expected) {
		final Map<String, String> encryptedMediaHandlerProperties = new HashMap<>();
		encryptedMediaHandlerProperties.put(PUBLIC_KEY_URL_PROPERTY, portalPublicKeyURL);
		when(encryptedMediaHandler.getProperties()).thenReturn(encryptedMediaHandlerProperties);

		final JSONObject additionalProperties = new JSONObject();
		//noinspection unchecked // classe JSONObject non modifiable
		additionalProperties.put(PUBLIC_KEY_URL_ADDITIONAL_PROPERTY, apiPublicKeyURL);
		when(engagedApi.getAdditionalProperties()).thenReturn(additionalProperties);

		assertThat(publicKeyURLComparator.usesSamePublicKey(encryptedMediaHandler, engagedApi)).isEqualTo(expected);
	}

}
