package org.rudi.facet.apimaccess.api.registration;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.springframework.stereotype.Component;

/**
 * Lorsqu'on enregistre un utilisateur avec cette API, un nouveau rôle <code>Application/&lt;username&gt;</code> est créé dans Carbon. On peut le
 * retrouver dans WSO2 à cette URL <code>/carbon/role/role-mgt.jsp</code>.
 *
 * @see <a href="https://docs.wso2.com/display/IS510/apidocs/OAuth2-dynamic-client-registration/">Documentation WSO2</a>
 */
@Component
public class OAuth2DynamicClientRegistrationOperationAPI extends AbstractClientRegistrationOperationAPI<Application> {

	public OAuth2DynamicClientRegistrationOperationAPI(APIManagerProperties properties,
			OAuth2DynamicClientRegistrationExceptionFactory clientRegistrationExceptionFactory,
			HttpClientHelper httpClientHelper) {
		super(properties, clientRegistrationExceptionFactory, httpClientHelper);
	}

	@Override
	protected String getRegistrationPath() {
		return properties.getRegistrationV11Path();
	}

	@Override
	protected Class<Application> getClientRegistrationResponseClass() {
		return Application.class;
	}

	@Override
	protected boolean isTrustAllCerts() {
		return properties.isTrustAllCerts();
	}
}
