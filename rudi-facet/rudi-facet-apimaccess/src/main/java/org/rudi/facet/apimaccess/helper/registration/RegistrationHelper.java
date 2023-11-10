package org.rudi.facet.apimaccess.helper.registration;

import javax.net.ssl.SSLException;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.exception.AdminOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.RudiClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.AdminService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistrationHelper {

	private final RudiClientRegistrationRepository rudiClientRegistrationRepository;
	private final AdminService adminService;
	private final ApplicationService applicationService;

	public void register(String login, String password) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		rudiClientRegistrationRepository.findRegistrationOrRegister(login, password);
	}

	public void assignInternalSubscriberRole(String login, String password) throws AdminOperationException {
		adminService.assignRoleToUser(APIManagerProperties.Roles.INTERNAL_SUBSCRIBER, login);
	}

	public Application getOrCreateApplicationForUser(String login) throws ApplicationOperationException {
		return applicationService.getOrCreateDefaultApplication(login);
	}

	public ClientRegistration findRegistrationForUser(String login, String password) throws SSLException, GetClientRegistrationException, BuildClientRegistrationException {
		return rudiClientRegistrationRepository.findByUsernameAndPassword(login, password);
	}
}
