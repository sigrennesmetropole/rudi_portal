/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.controller;

import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.facade.controller.api.UserInfoApi;
import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author FNI18300
 *
 */
@RestController
public class Ouath2UserInfoController implements UserInfoApi {

	@Autowired
	private UserService userService;

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE')")
	public ResponseEntity<User> getUserInfo(UUID userUuid) throws Exception {
		return ResponseEntity.ok(userService.getUserInfo(userUuid));
	}

}
