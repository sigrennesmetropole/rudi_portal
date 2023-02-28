package org.rudi.microservice.acl.service.account;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.acl.core.bean.Account;
import org.rudi.microservice.acl.core.bean.PasswordChange;
import org.rudi.microservice.acl.core.bean.User;

/**
 * Service métier de gestion des comptes utilisateur
 */
public interface AccountService {

	/**
	 * Contrôle des informations entrées pour créer un compte utilisateur
	 *
	 * @param account les informations de création de compte
	 * @throws AbstractAccountRegistrationException erreur détectée
	 */
	void checkAccountCreation(Account account) throws AbstractAccountRegistrationException;

	/**
	 * Record a account waiting for validation
	 *
	 * @param account the account to register
	 * @throws AppServiceException
	 */
	void registerAccount(Account account) throws AbstractAccountRegistrationException;

	/**
	 * Validate an existing account by token
	 *
	 * @param token the account token
	 * @throws AppServiceException
	 */
	User validateAccount(String token) throws AbstractAccountRegistrationException;

	/**
	 * Return the account associated with token or null
	 *
	 * @param token the account token
	 * @return
	 */
	Account getAccountByToken(String token);

	/**
	 * Retourne si un account est en cours de création donc non validé pour un login donné
	 *
	 * @param login le login
	 * @return vrai si le account est en cours de création
	 */
	boolean isAccountCreatedNotValidated(String login);

	/**
	 * Clean all obsolete registered accounts
	 */
	void cleanExpiredAccounts();

	/**
	 * Generate token to reset password
	 *
	 * @param email
	 */
	void requestPasswordChange(String email);

	/**
	 * Vérifie si un token utilisé pour changer un mot-de-passe est toujours valide
	 */
	void checkPasswordChangeToken(UUID token)
			throws AppServiceNotFoundException, TokenExpiredException, MissingParameterException;

	/**
	 * Modifie le mot-de-passe d'un compte en se basant sur un token. Ce token a été obtenu suite à une demande de
	 * changement de mot-de-passe.
	 */
	void validatePasswordChange(PasswordChange passwordChange) throws AppServiceException;

	/**
	 * Nettoyer la table UpdatePassword en supprimant tous les tokens périmés
	 */
	void deleteAllExpiredToken();

}
