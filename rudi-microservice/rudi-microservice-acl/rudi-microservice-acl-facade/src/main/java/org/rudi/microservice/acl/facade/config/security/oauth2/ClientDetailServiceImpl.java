/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.oauth2;

import java.util.Arrays;
import java.util.List;

import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.facade.config.security.AbstractDetailServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author FNI18300
 *
 */
public class ClientDetailServiceImpl extends AbstractDetailServiceImpl implements ClientDetailsService {

	private static final String GRANT_PASSWORD = "password";
	private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
	private static final String GRANT_REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE_WRITE = "write";
	private static final String SCOPE_READ = "read";

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		User user = getUserService().getUserByLogin(clientId, true);
		if (user == null) {
			throw new UsernameNotFoundException("Unknown clientId:" + clientId);
		}
		List<GrantedAuthority> grantedAuthorities = computeGrantedAuthorities(user);
		BaseClientDetails result = new BaseClientDetails();
		result.setClientId(clientId);
		result.setAuthorizedGrantTypes(Arrays.asList(GRANT_PASSWORD, GRANT_CLIENT_CREDENTIALS, GRANT_REFRESH_TOKEN));
		result.setClientSecret(user.getPassword());
		result.setScope(Arrays.asList(SCOPE_READ, SCOPE_WRITE));
		result.setAuthorities(grantedAuthorities);
		return result;
	}

}
