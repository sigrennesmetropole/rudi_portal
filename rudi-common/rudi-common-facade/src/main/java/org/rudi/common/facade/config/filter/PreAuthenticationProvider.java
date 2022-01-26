/**
 * 
 */
package org.rudi.common.facade.config.filter;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * A provider that accepts {@link PreAuthenticationToken} authentication
 * objects.
 */
public class PreAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (authentication instanceof PreAuthenticationToken) {
			return authentication;
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return PreAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
