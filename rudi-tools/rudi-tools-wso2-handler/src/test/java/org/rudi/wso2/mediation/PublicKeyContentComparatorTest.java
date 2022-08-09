package org.rudi.wso2.mediation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
import static org.rudi.wso2.mediation.PublicKeyContentComparator.PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY;

@ExtendWith(MockitoExtension.class)
class PublicKeyContentComparatorTest {
	@InjectMocks
	PublicKeyContentComparator publicKeyContentComparator;
	@Mock
	EncryptedMediaHandler encryptedMediaHandler;
	@Mock
	API engagedApi;

	@ParameterizedTest
	@CsvSource({
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvS3nTZOj01kq1V6wKpMe, true",
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvS3nTZOj01kq1V6wKpMX, false",
			", false",
	})
	void usesSamePublicKey(final String apiPublicKeyPartialContent, final boolean expected) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

		if (apiPublicKeyPartialContent != null) {
			when(encryptedMediaHandler.getPublicKeyContent()).thenReturn("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvS3nTZOj01kq1V6wKpMenROzRDb8V/uBLS6Ey90sWdhbRhv8R1QIYSRs3YjMZ2HBdMOQzuMQvyUF5lJv0KDNjYIf74n3+rDNxICkTlm7cwggtks/JOdNw1o/fGbU83tlAnSr4QbLCCqThzslchiKmGVH5pCiV/aX2bl81iNKkGDiFpmyT/au8+OtZOZe910sDnBsyPHH+wCkh/bb4E+tkKHUGLKpi4T8cm9wrNXFySxP532zuPsJ5CsaDWyu3jXivfYvC5Q520B72F+eK6nIdozONJLMtd9lFegxuRtU3fsYMAZww77hiCsH5mgfLo+iwXicdTCWogkGl8n9ODYhUwIDAQAB");
		}

		final JSONObject additionalProperties = new JSONObject();
		//noinspection unchecked // classe JSONObject non modifiable
		additionalProperties.put(PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY, apiPublicKeyPartialContent);
		when(engagedApi.getAdditionalProperties()).thenReturn(additionalProperties);

		assertThat(publicKeyContentComparator.usesSamePublicKey(encryptedMediaHandler, engagedApi)).isEqualTo(expected);
	}
}
