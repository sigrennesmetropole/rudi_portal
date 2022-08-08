/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.oauth2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author FNI18300
 *
 */
@RestController
public class OAuthUserInfoController {

	@GetMapping("/oauth/userinfo")
	public Principal user(Principal user) {
		return user;
	}

}
