/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.anonymous;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.rudi.common.facade.config.filter.JwtTokenUtil;
import org.rudi.common.facade.config.filter.Tokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 */
@Component
public class AnonymousAuthenticationLoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final String CLIENT_ID = "client_id";

	private static final String READ_SCOPE = "read";

	private static final String SCOPE = "scope";

	private static final String PASSWORD = "password";

	private static final String USERNAME = "username";

	private static final String GRANT_TYPE = "grant_type";

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String X_TOKEN_HEADER = "X-TOKEN";

	@Autowired
	private TokenEndpoint tokenEndPoint;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		UsernamePasswordAuthenticationToken customToken = (UsernamePasswordAuthenticationToken) authentication;
		SecurityContextHolder.getContext().setAuthentication(customToken);

		// Nous déléguons ici la créationdu token à OAuth2
		Map<String, String> parameters = new HashMap<>();
		parameters.put(GRANT_TYPE, PASSWORD);
		parameters.put(USERNAME, customToken.getName());
		parameters.put(PASSWORD, customToken.getName());
		parameters.put(SCOPE, READ_SCOPE);
		parameters.put(CLIENT_ID, customToken.getName());

		ResponseEntity<OAuth2AccessToken> tokenResponse = tokenEndPoint.postAccessToken(customToken, parameters);
		String value = null;
		Object body = null;
		if (tokenResponse.hasBody()) {
			body = tokenResponse.getBody();
			value = AbstractJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX
					+ ((body != null) ? ((OAuth2AccessToken) body).getValue() : "");
		} else {
			/// ça marche pas on généère nous même
			AuthenticatedUser user = (AuthenticatedUser) customToken.getDetails();
			body = generateTokens(user);
			value = ((Tokens) body).getJwtToken();
		}

		response.setHeader(AUTHORIZATION_HEADER, value);
		response.setHeader(X_TOKEN_HEADER, value);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), body);

		clearAuthenticationAttributes(request);
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

	private Tokens generateTokens(AuthenticatedUser user) throws IOException {
		Tokens tokens = null;
		try {
			tokens = jwtTokenUtil.generateTokens(user.getLogin(), user);
		} catch (Exception e) {
			throw new IOException("Failed to generate tokens", e);
		}
		return tokens;
	}
}
