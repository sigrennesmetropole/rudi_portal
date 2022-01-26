/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author FNI18300
 *
 */
public class AbstractDetailServiceImpl {

	@Autowired
	private UserService userService;

	protected List<GrantedAuthority> computeGrantedAuthorities(User user) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(user.getRoles())) {
			for (Role role : user.getRoles()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
			}
		}
		return grantedAuthorities;
	}

	protected List<String> computeRoles(User user) {
		List<String> grantedAuthorities = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(user.getRoles())) {
			for (Role role : user.getRoles()) {
				grantedAuthorities.add(role.getCode());
			}
		}
		return grantedAuthorities;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService() {
		return userService;
	}
}
