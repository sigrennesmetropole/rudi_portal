/**
 *
 */
package org.rudi.facet.acl.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserPageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.facet.acl.helper.UserSearchCriteria.ROLE_UUIDS_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_LOGIN_PARAMETER;
import static org.rudi.facet.acl.helper.UserSearchCriteria.USER_PASSWORD_PARAMETER;

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
	@Value("${rudi.facet.acl.endpoint.users.client-key.url:/acl/v1/users/{login}/client-key}")
	private String clientKeyEndpointURL;

	@Getter
	@Value("${rudi.facet.acl.endpoint.users.client-registration.url:/acl/v1/users/{login}/client-registration}")
	private String clientRegistrationEndpointURL;

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
		if (userUuid == null) {
			throw new IllegalArgumentException("user uuid required");
		}
		return loadBalancedWebClient.get().uri(buildUsersGetDeleteURL(userUuid)).retrieve().bodyToMono(User.class)
				.block();
	}

	/**
	 * Accède au service µProviders pour trouver un provider par son uuid
	 */
	public void deleteUserByUUID(UUID userUuid) {
		if (userUuid == null) {
			throw new IllegalArgumentException("user uuid required");
		}
		ClientResponse response = loadBalancedWebClient.delete().uri(buildUsersGetDeleteURL(userUuid)).exchange()
				.block();
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
			throw new IllegalArgumentException("login required");
		}

		final var criteria = UserSearchCriteria.builder()
				.login(login)
				.build();
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
			throw new IllegalArgumentException("login required");
		}
		if (password == null) {
			throw new IllegalArgumentException("password required");
		}

		final var criteria = UserSearchCriteria.builder()
				.login(login)
				.password(password)
				.build();
		return getUser(criteria);
	}

	/**
	 * @return l'utilisateur correspondant aux critères, null sinon
	 */
	@Nullable
	private User getUser(UserSearchCriteria criteria) {

		UserPageResult pageResult = loadBalancedWebClient.get()
				.uri(buildUsersSearchURL(), uriBuilder -> uriBuilder
						.queryParam(LIMIT_PARAMETER, 1)
						.queryParam(USER_LOGIN_PARAMETER, criteria.getLogin())
						.queryParam(USER_PASSWORD_PARAMETER, criteria.getPassword())
						.queryParam(ROLE_UUIDS_PARAMETER, getRolesCriteria(criteria.getRoleUuids()))
						.build())
				.retrieve()
				.bodyToMono(UserPageResult.class).block();
		if (pageResult != null && CollectionUtils.isNotEmpty(pageResult.getElements())) {
			return pageResult.getElements().get(0);
		} else {
			return null;
		}
	}

	@Nullable
	private String getRolesCriteria(List<UUID> roleUuids) {
		String roles = null;
		if (CollectionUtils.isNotEmpty(roleUuids)) {
			roles = roleUuids.stream().map(UUID::toString).collect(Collectors.joining(","));
		}

		return roles;
	}

	@Nullable
	public ClientKey getClientKeyByLogin(String login) {
		return loadBalancedWebClient.get().uri(buildClientKeyGetURL(), Map.of(USER_LOGIN_PARAMETER, login)).retrieve()
				.bodyToMono(ClientKey.class).block();
	}

	@Nullable
	public ClientRegistrationDto getClientRegistrationByLogin(String login) {
		return loadBalancedWebClient.get().uri(buildClientRegistrationURL(), Map.of(USER_LOGIN_PARAMETER, login)).retrieve()
				.bodyToMono(ClientRegistrationDto.class).block();
	}

	public void addClientRegistration(String username, AccessKeyDto clientAccessKey) {
		loadBalancedWebClient.post().uri(buildClientRegistrationURL(), Map.of(USER_LOGIN_PARAMETER, username)).bodyValue(clientAccessKey).retrieve()
				.bodyToMono(void.class).block();
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
	 * @param roleCode
	 * @return
	 */
	@NotNull
	public List<User> searchUsers(String roleCode) {
		List<User> result = new ArrayList<>();
		Role[] roles = loadBalancedWebClient.get().uri(buildRolesSearchURL(roleCode)).retrieve()
				.bodyToMono(Role[].class).block();
		if (ArrayUtils.isNotEmpty(roles)) {
			final var criteria = UserSearchCriteria.builder()
					.roleUuids(Arrays.stream(roles).map(Role::getUuid).collect(Collectors.toList()))
					.build();
			UserPageResult pageResult = loadBalancedWebClient
					.get()
					.uri(buildUsersSearchURL(), uriBuilder -> uriBuilder
							.queryParam(LIMIT_PARAMETER, 1)
							.queryParam(USER_LOGIN_PARAMETER, criteria.getLogin())
							.queryParam(USER_PASSWORD_PARAMETER, criteria.getPassword())
							.queryParam(ROLE_UUIDS_PARAMETER, getRolesCriteria(criteria.getRoleUuids()))
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
			throw new AppServiceUnauthorizedException(String.format("Authenticated user with login \"%s\" user is unknown", authenticatedUser.getLogin()));
		}
		return user;
	}


}
