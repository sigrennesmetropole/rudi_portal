package org.rudi.wso2.mediation;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.http.protocol.HTTP;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.API;
import org.apache.synapse.rest.RESTConstants;
import org.apache.synapse.transport.nhttp.NhttpConstants;
import org.apache.synapse.transport.passthru.PassThroughConstants;
import org.apache.synapse.transport.passthru.Pipe;
import org.apache.synapse.transport.passthru.util.BinaryRelayBuilder;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIManager;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rudi.wso2.mediation.AdditionalPropertiesUtil.VISIBLE_IN_DEVPORTAL_SUFFIX;
import static org.rudi.wso2.mediation.EncryptedMediaHandler.API_UT_API_PROPERTY;
import static org.rudi.wso2.mediation.EncryptedMediaHandler.API_UT_API_PUBLISHER_PROPERTY;
import static org.rudi.wso2.mediation.EncryptedMediaHandler.ENCRYPTED_PROPERTY;
import static org.rudi.wso2.mediation.EncryptedMediaHandler.PRIVATE_KEY_PATH_PROPERTY;
import static org.rudi.wso2.mediation.PublicKeyContentComparator.PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY;
import static org.rudi.wso2.mediation.PublicKeyURLComparator.PUBLIC_KEY_URL_ADDITIONAL_PROPERTY;
import static org.rudi.wso2.mediation.PublicKeyURLComparator.PUBLIC_KEY_URL_PROPERTY;

@ExtendWith(MockitoExtension.class)
class EncryptedMediaHandlerTest {

	@InjectMocks
	EncryptedMediaHandler encryptedMediaHandler;

	@Mock
	Axis2MessageContext messageContext;

	@Mock
	org.apache.axis2.context.MessageContext axis2MC;

	@Mock
	APIManager fixedAPIManager;

	@Test
	void handleRequest() {

		when(messageContext.getProperty(any())).thenReturn(null);
		when(messageContext.getAxis2MessageContext()).thenReturn(axis2MC);

		when(axis2MC.getPropertyNames()).thenReturn(Collections.emptyIterator());

		final SynapseConfiguration synapseConfiguration = new SynapseConfiguration();
		final API api = new API("admin--007faa17-d6e0-43a3-9248-19572eaa926a_dwnl", "/datasets/007faa17-d6e0-43a3-9248-19572eaa926a/dwnl/1.0.0");
		synapseConfiguration.addAPI(api.getAPIName(), api);

		assertThat(encryptedMediaHandler.handleRequest(messageContext)).isTrue();
	}

	// Exemple d'API réelle en dev : 007faa17-d6e0-43a3-9248-19572eaa926a_dwnl
	@ParameterizedTest
	@CsvSource({
			"https://rudi.open-dev.com/konsult/v1/encryption-key,",
			"file:../../rudi-microservice/rudi-microservice-konsult/rudi-microservice-konsult-facade/src/main/resources/encryption_key_public.key, MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvS3nTZOj01kq1V6wKpMe",
	})
	void handleResponse_engaged(final String apiPublicKeyURL, final String apiPublicKeyPartialContent) throws APIManagementException, IOException {

		when(messageContext.getProperty(any())).thenReturn(null);
		when(messageContext.getAxis2MessageContext()).thenReturn(axis2MC);

		final Map<String, Object> axis2MCProperties = new HashMap<>();
		axis2MCProperties.put(Constants.Configuration.CONTENT_TYPE, "application/octet-stream");
		axis2MCProperties.put(PassThroughConstants.MESSAGE_BUILDER_INVOKED, null);
		axis2MCProperties.put(NhttpConstants.HTTP_SC, 200);

		final SynapseConfiguration synapseConfiguration = new SynapseConfiguration();
		final API api = new API("admin--007faa17-d6e0-43a3-9248-19572eaa926a_dwnl", "/datasets/007faa17-d6e0-43a3-9248-19572eaa926a/dwnl/1.0.0");
		synapseConfiguration.addAPI(api.getAPIName(), api);

		when(messageContext.getProperty(RESTConstants.PROCESSED_API)).thenReturn(api);
		when(messageContext.getProperty(API_UT_API_PUBLISHER_PROPERTY)).thenReturn("admin");
		when(messageContext.getProperty(API_UT_API_PROPERTY)).thenReturn(api.getAPIName());

		org.wso2.carbon.apimgt.api.model.API engagedApi = mock(org.wso2.carbon.apimgt.api.model.API.class);
		when(fixedAPIManager.getAPI(any(APIIdentifier.class))).thenReturn(engagedApi);

		final var encryptedResource = new ClassPathResource("files/irisa/testDechiffrement.crypt");
		final Pipe passThroughPipe = mock(Pipe.class);
		when(passThroughPipe.getInputStream()).thenReturn(encryptedResource.getInputStream());
		axis2MCProperties.put(PassThroughConstants.PASS_THROUGH_PIPE, passThroughPipe);

		final ConfigurationContext configurationContext = mock(ConfigurationContext.class);
		when(axis2MC.getConfigurationContext()).thenReturn(configurationContext);

		final AxisConfiguration axisConfiguration = mock(AxisConfiguration.class);
		when(configurationContext.getAxisConfiguration()).thenReturn(axisConfiguration);

		final Map<String, String> transportHeaders = new HashMap<>();
		axis2MCProperties.put(MessageContext.TRANSPORT_HEADERS, transportHeaders);

		transportHeaders.put(HTTP.CONTENT_LEN, "5396");

		// application/octet-stream -> {BinaryRelayBuilder@46354}
		// Pour retrouver la liste complète, faire un debug dans la méthode org.apache.axis2.engine.AxisConfiguration.getMessageBuilder(java.lang.String) et surveiller this.messageBuilders
		when(axisConfiguration.getMessageBuilder("application/octet-stream", false)).thenReturn(new BinaryRelayBuilder());

		doCallRealMethod().when(axis2MC).setEnvelope(any());

		final JSONObject additionalProperties = new JSONObject();
		//noinspection unchecked // classe JSONObject non modifiable
		additionalProperties.put(ENCRYPTED_PROPERTY, "true");
		when(engagedApi.getAdditionalProperties()).thenReturn(additionalProperties);

		//noinspection unchecked // classe JSONObject non modifiable
		additionalProperties.put(PUBLIC_KEY_URL_ADDITIONAL_PROPERTY + VISIBLE_IN_DEVPORTAL_SUFFIX, apiPublicKeyURL);

		//noinspection unchecked // classe JSONObject non modifiable
		additionalProperties.put(PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY, apiPublicKeyPartialContent);

		encryptedMediaHandler.addProperty(PUBLIC_KEY_URL_PROPERTY, apiPublicKeyURL);

		when(axis2MC.getEnvelope()).thenCallRealMethod();

		encryptedMediaHandler.addProperty(PRIVATE_KEY_PATH_PROPERTY, "file:src/main/resources/encryption_key.key");


		addPropertiesToMock(axis2MCProperties);


		assertThat(encryptedMediaHandler.handleResponse(messageContext)).isTrue();


		final var replacedBodyInputStream = SOAPUtils.getBinaryTextNodeDataHandlerInputStream(axis2MC.getEnvelope());
		final var decryptedResource = new ClassPathResource("files/irisa/testDechiffrement.decrypt");
		assertThat(replacedBodyInputStream).hasSameContentAs(decryptedResource.getInputStream());
	}

	private void addPropertiesToMock(Map<String, Object> axis2MCProperties) {
		when(axis2MC.getPropertyNames()).thenReturn(axis2MCProperties.keySet().iterator());
		axis2MCProperties.forEach((propertyName, propertyValue) -> {
			when(axis2MC.getProperty(propertyName)).thenReturn(propertyValue);
		});
	}

}
