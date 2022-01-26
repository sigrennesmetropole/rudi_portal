/**
 * 
 */
package org.rudi.common.facade.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

/**
 * @author FNI18300
 *
 */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class CustomMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	/**
	 * Allow skip ROLE_ when check permission using @Secured, like:
	 * 
	 * @Secured({AuthorityConstants.ROLE_SYSTEM_ADMIN})
	 */
	@Override
	protected AccessDecisionManager accessDecisionManager() {
		AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();
		setAuthorityRolePrefix(accessDecisionManager, "");
		return accessDecisionManager;
	}

	private void setAuthorityRolePrefix(AffirmativeBased accessDecisionManager, String rolePrefix) {
		accessDecisionManager.getDecisionVoters().stream().filter(RoleVoter.class::isInstance)
				.map(RoleVoter.class::cast).forEach(it -> it.setRolePrefix(rolePrefix));
	}

	/**
	 * Allow skip ROLE_ when check permission using @PreAuthorize,
	 * like: @PreAuthorize("hasAnyRole('USER', 'SYSTEM_ADMIN')")
	 */
	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}
}
