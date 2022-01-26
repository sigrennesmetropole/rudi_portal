/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security;

import java.util.List;

import org.rudi.microservice.acl.core.bean.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author FNI18300
 *
 */
public class UserDetailServiceImpl extends AbstractDetailServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		User user = getUserService().getUserByLogin(login, true);
		if (user == null) {
			throw new UsernameNotFoundException("Unknown login:" + login);
		}
		List<GrantedAuthority> grantedAuthorities = computeGrantedAuthorities(user);
		return new org.springframework.security.core.userdetails.User(login, user.getPassword(), grantedAuthorities);
	}

}
