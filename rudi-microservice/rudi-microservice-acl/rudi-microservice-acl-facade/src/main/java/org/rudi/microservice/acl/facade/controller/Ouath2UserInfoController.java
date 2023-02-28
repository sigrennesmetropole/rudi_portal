/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.controller;

import java.util.Optional;
import java.util.UUID;

import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.facade.controller.api.UserInfoLoginApi;
import org.rudi.microservice.acl.facade.controller.api.UserInfoUuidApi;
import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE;

/**
 * @author FNI18300
 */
@RestController
public class Ouath2UserInfoController implements UserInfoLoginApi, UserInfoUuidApi {

	@Autowired
	private UserService userService;

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return UserInfoLoginApi.super.getRequest();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE + ")")
	public ResponseEntity<User> getUserInfo(UUID userUuid) throws Exception {
		return ResponseEntity.ok(userService.getUserInfo(userUuid));
	}

	@Override
	public ResponseEntity<User> getUserInfoByLogin(String login) throws Exception {
		return ResponseEntity.ok(userService.getUserInfo(login));
	}
}
