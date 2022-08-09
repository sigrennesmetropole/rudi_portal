package org.rudi.microservice.konsult.service.apim.impl;

import javax.net.ssl.SSLException;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.AdminOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.AdminService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.microservice.konsult.core.bean.ApiKeys;
import org.rudi.microservice.konsult.core.bean.ApiKeysType;
import org.rudi.microservice.konsult.core.bean.Credentials;
import org.rudi.microservice.konsult.service.apim.ApimService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApimServiceImpl implements ApimService {

	private final ApplicationService applicationService;
	private final AdminService adminService;
	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	private final ACLHelper aclHelper;

	@Override
	public boolean hasEnabledApi(Credentials credentials) throws SSLException {
		final var registration = customClientRegistrationRepository.findByUsernameAndPassword(credentials.getLogin(), credentials.getPassword());
		return registration != null;
	}

	@Override
	public void enableApi(Credentials credentials) throws APIManagerException, SSLException {
		assignInternalSubscriberRole(credentials);
		register(credentials);
		getOrCreateApplicationForUser(credentials);
	}

	private void register(Credentials credentials) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		customClientRegistrationRepository.findRegistrationOrRegister(credentials.getLogin(), credentials.getPassword());
	}

	private void assignInternalSubscriberRole(Credentials credentials) throws AdminOperationException {
		adminService.assignRoleToUser(APIManagerProperties.Roles.INTERNAL_SUBSCRIBER, credentials.getLogin());
	}

	private Application getOrCreateApplicationForUser(Credentials credentials) throws ApplicationOperationException {
		return applicationService.getOrCreateDefaultApplication(credentials.getLogin());
	}

	@Override
	public ApiKeys getKeys(ApiKeysType type, Credentials credentials) throws APIManagerException, AppServiceForbiddenException {
		checkCredentials(credentials);
		final var application = getOrCreateApplicationForUser(credentials);
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
