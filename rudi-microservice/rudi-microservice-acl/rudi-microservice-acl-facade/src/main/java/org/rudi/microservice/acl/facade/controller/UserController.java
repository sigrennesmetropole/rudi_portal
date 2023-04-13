package org.rudi.microservice.acl.facade.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.ClientKey;
import org.rudi.microservice.acl.core.bean.FullName;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserPageResult;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.core.bean.UserType;
import org.rudi.microservice.acl.facade.controller.api.UsersApi;
import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KALIM;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_STRUKTURE;

/**
 * Controleur pour la gestion des utilisateurs RUDI
 *
 * @author MCY12700
 *
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

	private final UserService userService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE + ")")
	public ResponseEntity<UserPageResult> searchUsers(@Valid String login, @Valid String password, @Valid String lastname,
			@Valid String firstname, @Valid String company, @Valid UserType type, @Valid List<UUID> roleUuids,
			@Valid Integer offset, @Valid Integer limit, @Valid String order) {

		UserSearchCriteria searchCriteria = UserSearchCriteria.builder().login(login).password(password)
				.firstname(firstname).lastname(lastname).company(company).type(type).roleUuids(roleUuids).build();

		Pageable pageable = utilPageable.getPageable(offset, limit, order);

		Page<User> page = userService.searchUsers(searchCriteria, pageable);

		UserPageResult result = new UserPageResult();
		result.setTotal(page.getTotalElements());
		result.setElements(page.getContent());

		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE + ")")
	public ResponseEntity<User> getUser(UUID userUuid) throws Exception {
		return ResponseEntity.ok(userService.getUser(userUuid));
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<User> getMe() throws Exception {
		return ResponseEntity.ok(userService.getMe());
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_STRUKTURE + ", " + MODULE_KALIM + ")")
	public ResponseEntity<User> createUser(@Valid User user) throws Exception {
		return ResponseEntity.ok(userService.createUser(user));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<User> updateUser(@Valid User user) throws Exception {
		return ResponseEntity.ok(userService.updateUser(user));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteUser(UUID userUuid) throws Exception {
		userService.deleteUser(userUuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<AbstractAddress> createAddress(UUID userUuid, @Valid AbstractAddress abstractAddress)
			throws Exception {
		return ResponseEntity.ok(userService.createAddress(userUuid, abstractAddress));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteAddress(UUID userUuid, UUID addressUuid) throws Exception {
		userService.deleteAddress(userUuid, addressUuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<AbstractAddress> getAddress(UUID userUuid, UUID addressUuid) throws Exception {
		return ResponseEntity.ok(userService.getAddress(userUuid, addressUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<List<AbstractAddress>> getAddresses(UUID userUuid) throws Exception {
		return ResponseEntity.ok(userService.getAddresses(userUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<AbstractAddress> updateAddress(UUID userUuid, @Valid AbstractAddress abstractAddress)
			throws Exception {
		return ResponseEntity.ok(userService.updateAddress(userUuid, abstractAddress));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSULT + ", " + MODULE_KONSULT_ADMINISTRATOR + ")")
	public ResponseEntity<ClientKey> getClientKeyByLogin(String login) throws Exception {
		return ResponseEntity.ok(userService.getClientKeyByLogin(login));
	}
}
