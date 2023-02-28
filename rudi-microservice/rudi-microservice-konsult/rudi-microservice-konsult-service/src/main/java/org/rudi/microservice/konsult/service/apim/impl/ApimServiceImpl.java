package org.rudi.microservice.konsult.service.apim.impl;

import javax.net.ssl.SSLException;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.helper.registration.RegistrationHelper;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.microservice.konsult.core.bean.ApiKeys;
import org.rudi.microservice.konsult.core.bean.ApiKeysType;
import org.rudi.microservice.konsult.service.apim.ApimService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApimServiceImpl implements ApimService {

	private final ApplicationService applicationService;
	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	private final ACLHelper aclHelper;
	private final RegistrationHelper registrationHelper;

	@Override
	public boolean hasEnabledApi(Credentials credentials) throws SSLException {
		return registrationHelper.findRegistrationForUser(credentials.getLogin(), credentials.getPassword()) != null;
	}

	@Override
	public void enableApi(Credentials credentials) throws APIManagerException, SSLException {
		registrationHelper.assignInternalSubscriberRole(credentials.getLogin(), credentials.getPassword());
		registrationHelper.register(credentials.getLogin(), credentials.getPassword());
		registrationHelper.getOrCreateApplicationForUser(credentials.getLogin());
	}

	@Override
	public ApiKeys getKeys(ApiKeysType type, Credentials credentials) throws APIManagerException, AppServiceForbiddenException {
		checkCredentials(credentials);
		final var application = registrationHelper.getOrCreateApplicationForUser(credentials.getLogin());
		final var applicationKey = getApplicationKey(type, credentials.getLogin(), application.getApplicationId());
		return new ApiKeys()
				.consumerKey(applicationKey.getConsumerKey())
				.consumerSecret(applicationKey.getConsumerSecret());
	}

	private void checkCredentials(Credentials credentials) throws AppServiceForbiddenException {
		final var user = aclHelper.getUserByLoginAndPassword(credentials.getLogin(), credentials.getPassword());
		if (user == null) {
			throw new AppServiceForbiddenException("Invalid credentials");
		}
	}

	private ApplicationKey getApplicationKey(ApiKeysType type, String login, String applicationId) throws ApplicationOperationException {
		final var keyType = EndpointKeyType.fromValue(type.getValue());
		return applicationService.getApplicationKey(applicationId, login, keyType);
	}
}
