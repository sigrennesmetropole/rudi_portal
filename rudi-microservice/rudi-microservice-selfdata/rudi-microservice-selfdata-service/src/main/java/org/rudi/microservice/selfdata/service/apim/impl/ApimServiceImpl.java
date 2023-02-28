package org.rudi.microservice.selfdata.service.apim.impl;

import javax.net.ssl.SSLException;

import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.helper.registration.RegistrationHelper;
import org.rudi.microservice.selfdata.service.apim.ApimService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApimServiceImpl implements ApimService {

	private final RegistrationHelper registrationHelper;

	@Override
	public void enableApi(Credentials credentials) throws APIManagerException, SSLException {
		registrationHelper.assignInternalSubscriberRole(credentials.getLogin(), credentials.getPassword());
		registrationHelper.register(credentials.getLogin(), credentials.getPassword());
		registrationHelper.getOrCreateApplicationForUser(credentials.getLogin());
	}

	@Override
	public boolean hasEnabledApi(Credentials credentials) throws SSLException {
		return registrationHelper.findRegistrationForUser(credentials.getLogin(), credentials.getPassword()) != null;
	}
}
