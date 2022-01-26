/**
 * 
 */
package org.rudi.facet.acl.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.acl.bean.ClientKey;
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

/**
 * L'utilisation de ce helper requiert l'ajout de 2 propriétés dans le fichier de configuration associé
 * 
 * @author FNI18300
 *
 */
@Component
public class ACLHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ACLHelper.class);

	private static final String USER_LOGIN_PARAMETER = "login";

	private static final String LIMIT_PARAMETER = "limit";

	private static final String ACTIVE_PARAMETER = "active";

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
	@Value("${rudi.facet.acl.service.url:lb://RUDI-ACL/}")
	private String aclServiceURL;

	@Autowired
	@Qualifier("rudi_oauth2")
	private WebClient loadBalancedWebClient;

	/**
	 * Accède au service µProviders pour trouver un provider par son uuid
	 * 
	 * @param providerUuid
	 * @return le provider
	 */
	public User getUserByUUID(UUID userUuid) {
		if (userUuid == null) {
			throw new IllegalArgumentException("user uuid required");
		}
		return loadBalancedWebClient.get().uri(buildUsersGetDeleteURL(userUuid)).retrieve().bodyToMono(User.class)
				.block();
	}

	/**
	 * Accède au service µProviders pour trouver un provider par son uuid
	 * 
	 * @param providerUuid
	 * @return le provider
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
	 * @param nodeProviderUUId
	 * @return le provider correspondant
	 */
	public User getUserByLogin(String login) {
		User result = null;
		if (login == null) {
			throw new IllegalArgumentException("login required");
		}

		UserPageResult pageResult = loadBalancedWebClient.get().uri(buildUsersSearchURL(login)).retrieve()
				.bodyToMono(UserPageResult.class).block();
		if (pageResult != null && CollectionUtils.isNotEmpty(pageResult.getElements())) {
			result = pageResult.getElements().get(0);
		}
		return result;
	}

	public ClientKey getClientKeyByLogin(String login) {
		return loadBalancedWebClient.get().uri(buildClientKeyGetURL(), Map.of(USER_LOGIN_PARAMETER, login)).retrieve()
				.bodyToMono(ClientKey.class).block();
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
	 * @param nodeProviderUUId
	 * @return le provider correspondant
	 */
	public List<Role> searchRoles() {
		Role[] roles = loadBalancedWebClient.get().uri(buildRolesSearchURL()).retrieve().bodyToMono(Role[].class)
				.block();
		return Arrays.stream(roles).collect(Collectors.toList());
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

	protected String buildUsersSearchURL(String login) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getUsersEndpointSearchURL());
		urlBuilder.append("?").append(LIMIT_PARAMETER).append('=').append(1);
		urlBuilder.append('&').append(USER_LOGIN_PARAMETER).append('=').append(login);
		return urlBuilder.toString();
	}

	protected String buildRolesSearchURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getRolesEndpointSearchURL());
		urlBuilder.append("?").append(ACTIVE_PARAMETER).append('=').append(true);
		return urlBuilder.toString();
	}

	protected String buildClientKeyGetURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getAclServiceURL()).append(getClientKeyEndpointURL());
		return urlBuilder.toString();
	}
}
