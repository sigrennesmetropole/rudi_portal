package org.rudi.facet.acl.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.AccessKeyDto;
import org.rudi.facet.acl.bean.AddressType;
import org.rudi.facet.acl.bean.ClientKey;
import org.rudi.facet.acl.bean.ClientRegistrationDto;
import org.rudi.facet.acl.bean.EmailAddress;
import org.rudi.facet.acl.bean.PasswordUpdate;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserPageResult;
import org.rudi.facet.acl.bean.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_LIMIT_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_LOGIN_AND_DENOMINATION_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_LOGIN_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_TYPE_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_UUIDS_PARAMETER;

/**
 * L'utilisation de ce helper requiert l'ajout de 2 propriétés dans le fichier de configuration associé
 *
 * @author FNI18300
 */
@Component
@RequiredArgsConstructor
public class ACLHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ACLHelper.class);

	private static final String LIMIT_PARAMETER = "limit";
	private static final String ACTIVE_PARAMETER = "active";
	private static final String CODE_PARAMETER = "code";

	public static final String LOGIN_REQUIRED = "login required";

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.search.url:/acl/v1/roles}")
	private String rolesEndpointSearchURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.roles.search.url:/acl/v1/users}")
	private String usersEndpointSearchURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.crud.url:/acl/v1/users}")
	private String userEndpointCRUDURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.count.url:/acl/v1/users/count}")
	private String userCountEndpointURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.client-key.url:/acl/v1/users/{login}/client-key}")
	private String clientKeyEndpointURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.client-registration.url:/acl/v1/users/{login}/client-registration-access-key}")
	private String clientRegistrationEndpointURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.client-registration-by-password.url:/acl/v1/users/{login}/client-registration-password}")
	private String registrationByPasswordEndpointURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.client-registration-by-password.url:/acl/v1/users/{login}/password}")
	private String updateUserPasswordEndpointUrl;

	@Getter
	@Value("${rudi.facet.acl.service.url:lb://RUDI-ACL/}")
	private String aclServiceURL;

	@Autowired
	@Qualifier("rudi_oauth2")
	private WebClient loadBalancedWebClient;

	private final UtilContextHelper utilContextHelper;

	/**
	 * Accède au service µACL pour trouver un utilisateur par son uuid
	 *
	 * @return l'utilisateur, <code>null</code> si l'utilisateur n'existe pas
	 */
	@Nullable
	public User getUserByUUID(UUID userUuid) {
		return getMonoUserByUUID(userUuid).block();
	}

	@Nullable
	public Mono<User> getMonoUserByUUID(UUID userUuid) {
		if (userUuid == null) {
			throw new IllegalArgumentException("user uuid required");
		}
		return loadBalancedWebClient.get().uri(buildUsersGetDeleteURL(userUuid)).retrieve().bodyToMono(User.class);
	}

	/**
	 * Accède au service µProviders pour trouver un provider par son uuid
	 */
	public void deleteUserByUUID(UUID userUuid) {
		if (userUuid == null) {
			throw new IllegalArgumentException("user uuid required");
		}
		ClientResponse response = loadBalancedWebClient.delete().uri(buildUsersGetDeleteURL(userUuid))
				.exchangeToMono(c -> Mono.just(c)).block();
		if (response != null && response.statusCode() != HttpStatus.OK) {
			LOGGER.warn("Failed to delete user :{}", userUuid);
		}
	}

	/**
	 * Accède au service µProviders pour trouver un provider par l'uuid d'un de ses noeuds
	 *
	 * @return le provider correspondant
	 */
	@Nullable
	public User getUserByLogin(String login) {
		if (login == null) {
			throw new IllegalArgumentException(LOGIN_REQUIRED);
		}

		final var criteria = UserSearchCriteria.builder().login(login).build();
		return getUser(criteria);
	}

	/**
	 * Accède au service ACL pour trouver un utilisateur par son login et son mot-de-passe
	 *
	 * @return l'utilisateur correspondant, null sinon
	 */
	@Nullable
	public User getUserByLoginAndPassword(String login, String password) {
		if (login == null) {
			throw new IllegalArgumentException(LOGIN_REQUIRED);
		}
		if (password == null) {
			throw new IllegalArgumentException("password required");
		}

		final var criteria = UserSearchCriteria.builder().login(login).password(password).build();
		return getUser(criteria);
	}

	/**
	 * @return l'utilisateur correspondant aux critères, null sinon
	 */
	@Nullable
	private User getUser(UserSearchCriteria criteria) {
		UserPageResult pageResult = getMonoUsers(criteria).block();
		if (pageResult != null && CollectionUtils.isNotEmpty(pageResult.getElements())) {
			return pageResult.getElements().get(0);
		} else {
			return null;
		}
	}

	/**
	 * @return l'utilisateur correspondant aux critères, null sinon
	 */
	@Nullable
	public Mono<UserPageResult> getMonoUsers(UserSearchCriteria criteria) {

		return loadBalancedWebClient.get().uri(buildUsersSearchURL(), uriBuilder -> uriBuilder
				.queryParam(LIMIT_PARAMETER, 1)
				.queryParamIfPresent(UserSearchCriteria.USER_LOGIN_PARAMETER, Optional.ofNullable(criteria.getLogin()))
				.queryParamIfPresent(UserSearchCriteria.USER_PASSWORD_PARAMETER,
						Optional.ofNullable(criteria.getPassword()))
				.queryParam(UserSearchCriteria.ROLE_UUIDS_PARAMETER, formatListParameter(criteria.getRoleUuids()))
				.build()).retrieve().bodyToMono(UserPageResult.class);
	}

	@Nullable
	private String formatListParameter(List<UUID> uuidList) {
		String result = null;
		if (CollectionUtils.isNotEmpty(uuidList)) {
			result = uuidList.stream().map(UUID::toString).collect(Collectors.joining(","));
		}

		return result;
	}

	@Nullable
	public ClientKey getClientKeyByLogin(String login) {
		return loadBalancedWebClient.get()
				.uri(buildClientKeyGetURL(), Map.of(UserSearchCriteria.USER_LOGIN_PARAMETER, login)).retrieve()
				.bodyToMono(ClientKey.class).block();
	}

	@Nullable
	public ClientRegistrationDto getClientRegistrationByLogin(String login) {
		return loadBalancedWebClient.get()
				.uri(buildClientRegistrationURL(), Map.of(UserSearchCriteria.USER_LOGIN_PARAMETER, login)).retrieve()
				.bodyToMono(ClientRegistrationDto.class).block();
	}

	public void addClientRegistration(String username, AccessKeyDto clientAccessKey) {
		loadBalancedWebClient.post()
				.uri(buildClientRegistrationURL(), Map.of(UserSearchCriteria.USER_LOGIN_PARAMETER, username))
				.bodyValue(clientAccessKey).retrieve().bodyToMono(void.class).block();
	}

	public ClientRegistrationDto findRegistrationOrRegister(String username, String password) {
		return loadBalancedWebClient.post()
				.uri(buildClientRegistrationByPasswordURL(), Map.of(UserSearchCriteria.USER_LOGIN_PARAMETER, username))
				.bodyValue(password).retrieve().bodyToMono(ClientRegistrationDto.class).block();
	}

	public User createUser(User user) {
		User result = null;
		if (user == null) {
			throw new IllegalArgumentException("user required");
		}

		result = loadBalancedWebClient.post().uri(buildUsersPostPutURL()).bodyValue(user).retrieve()
				.bodyToMono(User.class).block();
		return result;
	}

	public User updateUser(User user) {
		User result = null;
		if (user == null) {
			throw new IllegalArgumentException("user required");
		}

		result = loadBalancedWebClient.put().uri(buildUsersPostPutURL()).bodyValue(user).retrieve()
				.bodyToMono(User.class).block();
		return result;
	}

	/**
	 * Accède au service µProviders pour trouver un provider par l'uuid d'un de ses noeuds
	 *
	 * @return le provider correspondant
	 */
	public List<Role> searchRoles() {
		Role[] roles = loadBalancedWebClient.get().uri(buildRolesSearchURL(null)).retrieve().bodyToMono(Role[].class)
				.block();
		return Arrays.stream(roles).collect(Collectors.toList());
	}

	/**
	 * Retourne la liste des utilisateurs associé à un rôle dont le code est passé en paramètre
	 *
	 * @param roleCode code du Role sur lequel filtrer
	 * @return List de User ayant le rôle. Si Aucun, list Vide
	 */
	@NotNull
	public List<User> searchUsers(String roleCode) {
		List<User> result = new ArrayList<>();
		Role[] roles = loadBalancedWebClient.get().uri(buildRolesSearchURL(roleCode)).retrieve()
				.bodyToMono(Role[].class).block();
		if (ArrayUtils.isNotEmpty(roles)) {
			final var criteria = UserSearchCriteria.builder()
					.roleUuids(Arrays.stream(roles).map(Role::getUuid).collect(Collectors.toList())).build();

			// Retrait de la limit à 1 : envoie de mail au role MODERATOR
			// et pas seulement à 1 membre ayant le role MODERATOR
			UserPageResult pageResult = loadBalancedWebClient.get()
					.uri(buildUsersSearchURL(),
							uriBuilder -> uriBuilder
									.queryParam(UserSearchCriteria.ROLE_UUIDS_PARAMETER,
											formatListParameter(criteria.getRoleUuids()))
									.build())
					.retrieve().bodyToMono(UserPageResult.class).block();

			if (pageResult != null && pageResult.getElements() != null) {
				result.addAll(pageResult.getElements());
			}
		}
		return result;
	}

	@NotNull
	public List<User> searchUsersWithCriteria(List<UUID> userUuids, @Nullable String searchText,
			@Nullable String type, @Nullable Integer limit) {
		List<User> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(userUuids)) {
			if (searchText == null) {
				searchText = "";
			}

			UserType valueType = null;
			if (StringUtils.isNotBlank(type)) {
				try {
					valueType = UserType.fromValue(type);
				} catch (IllegalArgumentException e) {
					// ignore the wrong value no error on call
				}
			}

			final var criteria = UserSearchCriteria.builder().userUuids(userUuids).loginAndDenomination(searchText)
					.userType(valueType).build();
			UserPageResult pageResult = loadBalancedWebClient.get()
					.uri(buildUsersSearchURL(), uriBuilder -> uriBuilder
							.queryParam(USER_UUIDS_PARAMETER, formatListParameter(criteria.getUserUuids()))
							.queryParam(USER_LOGIN_AND_DENOMINATION_PARAMETER, criteria.getLoginAndDenomination())
							.queryParam(USER_TYPE_PARAMETER,
									criteria.getUserType() != null ? criteria.getUserType().toString() : null)
							.queryParam(USER_LIMIT_PARAMETER, limit)
							.build())
					.retrieve().bodyToMono(UserPageResult.class).block();
			if (pageResult != null && pageResult.getElements() != null) {
				result.addAll(pageResult.getElements());
			}
		}
		return result;
	}

	protected String buildUsersGetDeleteURL(UUID value) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getUserEndpointCRUDURL()).append('/').append(value);
		return urlBuilder.toString();
	}

	protected String buildUsersPostPutURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getUserEndpointCRUDURL());
		return urlBuilder.toString();
	}

	protected String buildUsersSearchURL() {
		return getAclServiceURL() + getUsersEndpointSearchURL();
	}

	protected String buildUsersCountURL() {
		return getAclServiceURL() + getUserCountEndpointURL();
	}

	protected String buildRolesSearchURL(String code) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getRolesEndpointSearchURL());
		urlBuilder.append("?").append(ACTIVE_PARAMETER).append('=').append(true);
		if (StringUtils.isNotEmpty(code)) {
			urlBuilder.append("&").append(CODE_PARAMETER).append('=').append(code);
		}
		return urlBuilder.toString();
	}

	protected String buildClientKeyGetURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getClientKeyEndpointURL());
		return urlBuilder.toString();
	}

	protected String buildClientRegistrationURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getClientRegistrationEndpointURL());
		return urlBuilder.toString();
	}

	protected String buildClientRegistrationByPasswordURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getRegistrationByPasswordEndpointURL());
		return urlBuilder.toString();
	}

	protected String buildUpdateUserPasswordURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getUpdateUserPasswordEndpointUrl());
		return urlBuilder.toString();
	}

	public String lookupEMailAddress(User user) {
		String result = null;
		if (CollectionUtils.isNotEmpty(user.getAddresses())) {
			result = user.getAddresses().stream().filter(a -> a.getType() == AddressType.EMAIL)
					.map(a -> ((EmailAddress) a).getEmail()).findFirst().orElse(null);
		}
		if (StringUtils.isEmpty(result) && user.getLogin().contains("@")) {
			return user.getLogin();
		}
		return result;
	}

	public List<String> lookupEmailAddresses(List<User> users) {
		List<String> result = new ArrayList<>();
		for (User user : users) {
			String email = lookupEMailAddress(user);
			if (StringUtils.isNotEmpty(email)) {
				result.add(email);
			}
		}
		return result;
	}

	protected boolean andOrWhat(StringBuilder urlBuilder, boolean and) {
		if (and) {
			urlBuilder.append("&");
		} else {
			urlBuilder.append("?");
		}
		return true;
	}

	@Nonnull
	public UUID getAuthenticatedUserUuid() throws AppServiceUnauthorizedException {
		return getAuthenticatedUser().getUuid();
	}

	@Nonnull
	public User getAuthenticatedUser() throws AppServiceUnauthorizedException {
		val authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("No authenticated user");
		}
		return lookupUser(authenticatedUser);
	}

	private User lookupUser(AuthenticatedUser authenticatedUser) throws AppServiceUnauthorizedException {
		val user = getUserByLogin(authenticatedUser.getLogin());
		if (user == null) {
			throw new AppServiceUnauthorizedException(String
					.format("Authenticated user with login \"%s\" user is unknown", authenticatedUser.getLogin()));
		}
		return user;
	}

	/**
	 * Permet la modification du mot de passe d'un utilisateur.
	 *
	 * @param login       de l'utilisateur
	 * @param oldPassword contient l'ancien mot de passe
	 * @param newPassword contient le nouveau mot de passe
	 */
	public void updateUserPassword(String login, String oldPassword, String newPassword)
			throws WebClientResponseException {
		if (login == null) {
			throw new IllegalArgumentException(LOGIN_REQUIRED);
		}
		if (oldPassword == null) {
			throw new IllegalArgumentException("old password required");
		}
		if (newPassword == null) {
			throw new IllegalArgumentException("new password required");
		}
		PasswordUpdate passwordUpdate = new PasswordUpdate();
		passwordUpdate.setNewPassword(newPassword);
		passwordUpdate.setOldPassword(oldPassword);

		loadBalancedWebClient.put().uri(buildUpdateUserPasswordURL(), Map.of(USER_LOGIN_PARAMETER, login))
				.bodyValue(passwordUpdate).retrieve().toBodilessEntity().block();
	}

	public Long getUserCount() {
		Long userCount = loadBalancedWebClient.get().uri(buildUsersCountURL()).retrieve().bodyToMono(Long.class)
				.block();
		if (userCount != null) {
			return userCount;
		}
		return 0L;
	}
}
