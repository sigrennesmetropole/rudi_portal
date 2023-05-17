/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.jwt;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.facet.apimaccess.helper.rest.RudiClientRegistrationRepository;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.AddressType;
import org.rudi.microservice.acl.core.bean.EmailAddress;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.facade.config.security.AbstractDetailServiceImpl;
import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@Component
@Slf4j
public class JwtAuthenticationProvider extends AbstractDetailServiceImpl implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Autowired
	private RudiClientRegistrationRepository rudiClientRegistrationRepository;

	@Autowired
	@Qualifier("clientPasswordEncoder")
	private PasswordEncoder passwordEncoder;

	@Value("${apimanager.oauth2.client.anonymous.username}")
	private String anonymousUsername;

	@Override
	public Authentication authenticate(Authentication authentication) {
		Authentication result = checkCredential(authentication);

		// wso2 registration
		String login = authentication.getName();
		String password = (String) authentication.getCredentials();

		try {
			// récupération du client id + client secret de l'utilisateur qui se connecte (inutile de le faire pour les users rudi et anonymous)
			if (!login.equals(anonymousUsername)) {
				rudiClientRegistrationRepository.register(login, password);
			}
		} catch (Exception e) {
			log.error(String.format(
					"Impossible de générer les paramètres d'authentification à l'API Manager pour l'utilisateur %s",
					login), e);
		}

		return result;
	}

	/**
	 * Check credential and return authentication objet
	 *
	 * @param authentication
	 * @return
	 */
	public Authentication checkCredential(Authentication authentication) {
		String login = authentication.getName();
		String password = (String) authentication.getCredentials();

		User user = userService.getUserByLogin(login, true);
		checkUser(user, login);
		checkPassword(authentication, user);

		AuthenticatedUser authenticatedUser = createAuthenticatedUser(user);

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				login, password, computeGrantedAuthorities(user));
		usernamePasswordAuthenticationToken.setDetails(authenticatedUser);
		return usernamePasswordAuthenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	private void checkUser(User user, String login) {
		if (user == null) {
			log.info("Impossible de trouver l'utilisateur: {}", login);
			throw new UsernameNotFoundException("Impossible de trouver l'utilisateur : " + login + ".");
		} else if (Boolean.TRUE.equals(user.getAccountLocked())) {
			log.warn("Le compte est vérouillé l'utilisateur: {}", login);
			throw new LockedException("Le compte est vérouillé");
		}
	}

	private void checkPassword(Authentication authentication, User user) {
		if (!isAnonymous(user)
				&& !passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
			if (!userService.recordAuthentication(user.getUuid(), false)) {
				log.info("Mot de passe erroné pour l'utilisateur: {}", user.getLogin());
				throw new BadCredentialsException("Mot de passe erroné.");
			} else {
				log.info("Compte verouillé: {}", user.getLogin());
				throw new LockedException("Account is locked");
			}
		} else {
			userService.recordAuthentication(user.getUuid(), true);
		}
	}

	private AuthenticatedUser createAuthenticatedUser(User user) {
		UserType userType;
		try {
			userType = UserType.valueOf(user.getType().name());
		} catch (Exception e) {
			userType = UserType.PERSON;
		}
		AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getLogin(), userType);
		authenticatedUser.setFirstname(user.getFirstname());
		authenticatedUser.setLastname(user.getLastname());
		authenticatedUser.setOrganization(user.getCompany());
		authenticatedUser.setRoles(computeRoles(user));
		if (CollectionUtils.isNotEmpty(user.getAddresses())) {
			for (AbstractAddress abstractAddress : user.getAddresses()) {
				if (abstractAddress.getType() == AddressType.EMAIL) {
					authenticatedUser.setEmail(((EmailAddress) abstractAddress).getEmail());
					break;
				}
			}
		}
		return authenticatedUser;
	}

	private boolean isAnonymous(User user) {
		if (CollectionUtils.isNotEmpty(user.getRoles())) {
			return user.getRoles().stream().anyMatch(role -> role.getCode().equalsIgnoreCase("anonymous"));
		} else {
			return false;
		}
	}

}
