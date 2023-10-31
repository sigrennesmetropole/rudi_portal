package org.rudi.facet.apimaccess.api.registration;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.exception.APIManagerHttpExceptionFactory;
import org.springframework.stereotype.Component;

/**
 * @see <a href="https://apim.docs.wso2.com/en/3.2.0/develop/product-apis/admin-apis/admin-v0.17/admin-v0.17/#section/Authentication">Documentation
 *      WSO2</a>
 * @deprecated cf Documentation WSO2 mentionnée
 */
@Component
@Deprecated
public class ClientRegistrationV017OperationAPI extends AbstractClientRegistrationOperationAPI<ClientAccessKey> {

	public ClientRegistrationV017OperationAPI(APIManagerProperties properties,
			APIManagerHttpExceptionFactory exceptionFactory, HttpClientHelper httpClientHelper) {
		super(properties, exceptionFactory, httpClientHelper);
	}

	@Override
	protected String getRegistrationPath() {
		return properties.getRegistrationV017Path();
	}

	@Override
	protected Class<ClientAccessKey> getClientRegistrationResponseClass() {
		return ClientAccessKey.class;
	}

	@Override
	protected boolean isTrustAllCerts() {
		return properties.isTrustAllCerts();
	}
}
