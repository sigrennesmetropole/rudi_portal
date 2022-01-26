/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class JwtAuthenticationLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		AuthenticationException exp = null;
		if (exception.getClass().isAssignableFrom(UsernameNotFoundException.class)) {
			exp = new BadCredentialsException("Account bad_credentials");
		} else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
			exp = new BadCredentialsException("Account bad_credentials");
		} else if (exception.getClass().isAssignableFrom(LockedException.class)) {
			exp = new LockedException("Account locked");
		} else if (exception.getClass().isAssignableFrom(DisabledException.class)) {
			exp = new DisabledException("Account disabled");
		}
		if (exp != null) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), exp.getMessage());
		}
	}

}
