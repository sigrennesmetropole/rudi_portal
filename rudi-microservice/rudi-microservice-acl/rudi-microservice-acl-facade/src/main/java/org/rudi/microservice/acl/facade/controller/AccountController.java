package org.rudi.microservice.acl.facade.controller;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.acl.core.bean.Account;
import org.rudi.microservice.acl.core.bean.PasswordChange;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.facade.controller.api.AccountApi;
import org.rudi.microservice.acl.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController implements AccountApi {

	@Autowired
	private AccountService accountService;

	@Override
	// Méthode accessible derrière authent si on est authentifié ça passe (anonymous = OK)
	public ResponseEntity<Void> requestAccountCreation(Account account) throws AppServiceException {
		// Création du compte utilisateur
		accountService.registerAccount(account);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<User> validateAccount(String token) throws Exception {
		return ResponseEntity.ok(accountService.validateAccount(token));
	}

	@Override
	public ResponseEntity<Void> requestPasswordChange(String email) {
		accountService.requestPasswordChange(email);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> checkPasswordChangeToken(UUID token) throws Exception {
		accountService.checkPasswordChangeToken(token);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> validatePasswordChange(PasswordChange change) throws AppServiceException {
		accountService.validatePasswordChange(change);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Boolean> isCreatedNotValidated(String login) throws Exception {
		return ResponseEntity.ok(accountService.isAccountCreatedNotValidated(login));
	}

	@Override
	public ResponseEntity<Void> accountLogout(String token) throws Exception {
		return ResponseEntity.noContent().build();
	}
}
