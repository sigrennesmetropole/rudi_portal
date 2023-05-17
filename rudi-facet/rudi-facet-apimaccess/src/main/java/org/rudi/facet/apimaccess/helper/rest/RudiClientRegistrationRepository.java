package org.rudi.facet.apimaccess.helper.rest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;

import org.rudi.facet.apimaccess.api.registration.ClientRegistrationResponse;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

public interface RudiClientRegistrationRepository extends ReactiveClientRegistrationRepository {
	@Nullable
	ClientRegistration findByUsername(String username) throws GetClientRegistrationException;

	@Nullable
	ClientRegistration findByUsernameAndPassword(String username, String password) throws SSLException, GetClientRegistrationException;

	/**
	 * Renvoie l'enregistrement client s'il existe dans le cache, sinon réalise l'enregistrement
	 */
	@Nonnull
	ClientRegistration findRegistrationOrRegister(String username, String password) throws BuildClientRegistrationException, SSLException, GetClientRegistrationException;

	ClientRegistration register(String username, String password) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException;

	void addClientRegistration(String username, ClientRegistrationResponse clientAccessKey);
}
