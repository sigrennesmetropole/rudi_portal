/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.facade.config.filter.JwtTokenUtil;
import org.rudi.common.facade.config.filter.Tokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author FNI18300
 *
 */
@Component
public class JwtAuthenticationLoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String X_TOKEN_HEADER = "X-TOKEN";

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		UsernamePasswordAuthenticationToken customToken = (UsernamePasswordAuthenticationToken) authentication;
		SecurityContextHolder.getContext().setAuthentication(customToken);

		AuthenticatedUser user = (AuthenticatedUser) customToken.getDetails();
		Tokens tokens = generateTokens(user);

		response.setHeader(AUTHORIZATION_HEADER, tokens.getJwtToken());
		response.setHeader(X_TOKEN_HEADER, tokens.getRefreshToken());
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), tokens);

		clearAuthenticationAttributes(request);
	}

	private Tokens generateTokens(AuthenticatedUser user) throws IOException {
		Tokens tokens = null;
		try {
			tokens = jwtTokenUtil.generateTokens(user.getLogin(), user);
		} catch (Exception e) {
			throw new IOException("Failed to generate tokens", e);
		}
		return tokens;
	}

	/**
	 * Removes temporary authentication-related data which may have been stored in the session during the authentication process..
	 *
	 * @param request - HTTP request
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

}
