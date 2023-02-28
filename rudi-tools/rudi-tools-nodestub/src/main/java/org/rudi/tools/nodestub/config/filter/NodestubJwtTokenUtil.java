package org.rudi.tools.nodestub.config.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.facade.config.filter.JwtTokenData;
import org.rudi.common.facade.config.filter.JwtTokenUtil;
import org.springframework.stereotype.Component;

@Component
public class NodestubJwtTokenUtil extends JwtTokenUtil {

	private static final String EXPECTED_DOMAIN_RUDI = "RUDI/";
	private static final String EXPECTED_NAMESPACE_WSO2 = "@carbon.super";

	@Override
	protected void handleExternalAccount(JwtTokenData token, JWTClaimsSet claims) {
		String subject = claims.getSubject();
		if (subject.startsWith(EXPECTED_DOMAIN_RUDI) && subject.endsWith(EXPECTED_NAMESPACE_WSO2)) {
			String login = subject.substring(EXPECTED_DOMAIN_RUDI.length(), subject.length() - EXPECTED_NAMESPACE_WSO2.length());
			AuthenticatedUser connectedUser = new AuthenticatedUser();
			connectedUser.setLogin(login);
			token.setAccount(connectedUser);
		}
	}
}
