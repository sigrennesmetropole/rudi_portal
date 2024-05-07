/**
 *
 */
package org.rudi.microservice.acl.service.user;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import javax.validation.Valid;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.AccessKeyDto;
import org.rudi.microservice.acl.core.bean.ClientKey;
import org.rudi.microservice.acl.core.bean.ClientRegistrationDto;
import org.rudi.microservice.acl.core.bean.PasswordUpdate;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service de gestion des utilisateurs Rudi
 *
 * @author FNI18300
 *
 */
public interface UserService {

	int getMaxFailedAttempt();

	int getLockDuration();

	/**
	 * Charge la liste paginée des utilisateurs en fonction de critères de recherche
	 *
	 * @param searchCriteria critères de recherche
	 * @param pageable outils de pagnination
	 * @return liste paginée des utilisateurs
	 */
	Page<User> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * Retourne un utilisateur en fonction de son uuid, avec toutes ses propriétés chargées
	 *
	 * @param uuid uuid de l'utilisateur
	 * @return un utilisateur en fonction de son uuid, avec toutes ses propriétés chargées
	 */
	User getUser(UUID uuid);

	/**
	 * Retourne un utilisateur en fonction de son uuid, avec uniquement les propriétés minimales
	 *
	 * @param uuid uuid de l'utilisateur
	 * @return un utilisateur en fonction de son uuid, avec uniquement les propriétés minimales
	 */
	User getUserInfo(UUID uuid);

	/**
	 * Retourne un utilisateur en fonction de son login, avec uniquement les propriétés minimales
	 *
	 * @param login login de l'utilisateur
	 * @return un utilisateur en fonction de son login, avec uniquement les propriétés minimales
	 */
	User getUserInfo(String login);

	/**
	 * Retourne l'utilisateur connecté
	 *
	 * @return l'utilisateur connecté
	 */
	public User getMe();

	/**
	 * Retourn un utilisateur par son login
	 *
	 * @param login        le login
	 * @param withPassword pour avoir le password ou non
	 * @return un utilisateur par son login
	 */
	User getUserByLogin(String login, boolean withPassword);

	/**
	 * Create a user
	 *
	 * @param user l'utilisateur à créer et à ajouter en BDD
	 * @return l'utilisateur créé
	 */
	User createUser(User user);

	/**
	 * Update a User entity
	 *
	 * @param user l'utilisateur avec les données mises à jour
	 * @return l'utilisateur tel que mis à jour en BDD
	 */
	User updateUser(User user);

	/**
	 * Delete a User entity
	 *
	 * @param uuid de l'utilisateur à supprimer
	 */
	void deleteUser(UUID uuid);

	/**
	 * Retourne une adresse d'un utilisateur
	 *
	 * @param userUuid uuid de l'utilisateur ciblé
	 * @param addressUuid uuid de l'adresse souhaité chez l'utilisateur ciblé
	 * @return une adresse d'un utilisateur
	 */
	AbstractAddress getAddress(UUID userUuid, UUID addressUuid);

	/**
	 * Retourne les adresses d'un utilisateur
	 *
	 * @param userUuid uuid de l'utilsiateur
	 * @return les adresses d'un utilisateur
	 */
	List<AbstractAddress> getAddresses(UUID userUuid);

	/**
	 * Ajoute une adresse sur un utilisateur
	 *
	 * @param userUuid uui de l'utilisateur
	 * @param abstractAddress adresse à rajouter à l'utilisateur
	 * @return une adresse sur un utilisateur
	 */
	AbstractAddress createAddress(UUID userUuid, AbstractAddress abstractAddress);

	/**
	 * Modifie une adresse d'un utilisateur
	 *
	 * @param userUuid uuid de l'utilisateur ciblé
	 * @param abstractAddress adresse à modifier
	 * @return une adresse modifiée
	 */
	AbstractAddress updateAddress(UUID userUuid, @Valid AbstractAddress abstractAddress);

	/**
	 * Supprime une adresse d'un utilisateur
	 *
	 * @param userUuid uuid de l'utilisateur ciblé
	 * @param addressUuid uuid de l'adresse à supprimer
	 */
	void deleteAddress(UUID userUuid, UUID addressUuid);

	/**
	 * Récupération des clés WSO2 d'un utilisateur
	 *
	 * @param login login de l'utilisateur
	 * @return ClientKey
	 * @throws SSLException Erreur lors de la récupération des clés
	 */
	@Nullable
	ClientKey getClientKeyByLogin(String login)
			throws SSLException, BuildClientRegistrationException, GetClientRegistrationException;

	/**
	 * Enregistre une authentification avec ou sans succès
	 *
	 * @param userUuid uuid de l'utilisateur ciblé
	 * @param success true si l'autenthification est réussie
	 * @return true if account is locked
	 */
	boolean recordAuthentication(UUID userUuid, boolean success);

	/**
	 * Déverouille les comptes après un certains délais
	 */
	void unlockUsers();

	/**
	 * Récupération de la CLientRegistration d'un utilisateur par login
	 *
	 * @param login login de l'utilisateur
	 * @return la ClientRegistration WSO2
	 * @throws GetClientRegistrationException erreur connexion WSO2
	 * @throws BuildClientRegistrationException erreur connexion WSO2
	 * @throws SSLException erreur connexion WSO2
	 */
	ClientRegistrationDto getClientRegistration(String login) throws GetClientRegistrationException, BuildClientRegistrationException, SSLException;

	/**
	 * Création d'une client registration WSO2 pour un user
	 * 
	 * @param login     le login de l'user qui fait l'action
	 * @param accessKey les clés d'accès WSO2
	 */
	void addClientRegistration(String login, AccessKeyDto accessKey);

	/**
	 * Réalise la registration dans WSO2 à l'aide du login et du pwd de l'utilisateur connecté
	 * 
	 * @param login    le login de l'utilisateur
	 * @param password son mot de passe
	 * @return uen client rgeistration associée
	 * @throws GetClientRegistrationException erreur connexion WSO2
	 * @throws BuildClientRegistrationException erreur connexion WSO2
	 * @throws SSLException erreur connexion WS
	 */
	ClientRegistrationDto registerClientByPassword(String login, String password) throws GetClientRegistrationException, BuildClientRegistrationException, SSLException;

	/**
	 * Mise à jour du mot de passe d'un utilisateur autre que celui connecté par login
	 * 
	 * @param login          le login de l'utilisateur modifié
	 * @param passwordUpdate les infos de changement de mot de passe
	 * @throws AppServiceException  – si traitement invalide
	 */
	void updateUserPassword(String login, PasswordUpdate passwordUpdate) throws AppServiceException;

	/**
	 * Comptage des nombres d'utilisateurs, pour l'affichage des chiffres clé sur la page d'accueil
	 * 
	 * @return le nombre d'utilisateurs ayant le rôle USER
	 */
	Long countUsers();

}
