/**
 *
 */
package org.rudi.microservice.acl.service.user.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.LongId;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.PageableUtil;
import org.rudi.facet.apimaccess.api.registration.Application;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.RudiClientRegistrationRepository;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.AccessKeyDto;
import org.rudi.microservice.acl.core.bean.ClientKey;
import org.rudi.microservice.acl.core.bean.ClientRegistrationDto;
import org.rudi.microservice.acl.core.bean.PasswordUpdate;
import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.service.helper.PasswordHelper;
import org.rudi.microservice.acl.service.mapper.ClientRegistrationMapper;
import org.rudi.microservice.acl.service.mapper.address.AbstractAddressMapper;
import org.rudi.microservice.acl.service.mapper.user.UserFullMapper;
import org.rudi.microservice.acl.service.mapper.user.UserLightMapper;
import org.rudi.microservice.acl.service.mapper.user.UserMapper;
import org.rudi.microservice.acl.service.password.IdenticalNewPasswordException;
import org.rudi.microservice.acl.service.password.InvalidCredentialsException;
import org.rudi.microservice.acl.service.user.UserService;
import org.rudi.microservice.acl.storage.dao.address.AbstractAddressDao;
import org.rudi.microservice.acl.storage.dao.address.AddressRoleDao;
import org.rudi.microservice.acl.storage.dao.role.RoleCustomDao;
import org.rudi.microservice.acl.storage.dao.role.RoleDao;
import org.rudi.microservice.acl.storage.dao.user.UserCustomDao;
import org.rudi.microservice.acl.storage.dao.user.UserDao;
import org.rudi.microservice.acl.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.acl.storage.entity.address.AddressRoleEntity;
import org.rudi.microservice.acl.storage.entity.role.RoleEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de gestion des utilisateurs RUDI
 *
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

	private static final String UUID_USER_MISSING_MESSAGE = "UUID user missing";
	private static final String LOGIN_USER_MISSING_MESSAGE = "Login user missing";
	private static final String UUID_ADRESSE_MISSING_MESSAGE = "UUID Adresse missing";
	private static final String USER_UNKNOWN_MESSAGE = "User unknown :";
	private static final String ADDRESS_UNKNOWN_MESSAGE = "Address unknown:";
	private static final String ADDRESS_MISSING_MESSAGE = "Address missing";
	private static final String ADDRESS_ROLE_INVALID_MESSAGE = "AddresseRole invalid";

	@Value("${apimanager.oauth2.client.anonymous.username}")
	private String anonymousUsername;

	@Value("${apimanager.oauth2.client.rudi.username}")
	private String rudiUsername;

	@Value("${apimanager.oauth2.client.anonymous.password}")
	private String anonymousPassword;

	@Value("${apimanager.oauth2.client.rudi.password}")
	private String rudiPassword;

	@Value("${user.authentication.maxFailedAttempt:10}")
	@Getter
	private int maxFailedAttempt;

	@Value("${user.authentication.lockDuration:20}")
	@Getter
	private int lockDuration;

	@Value("${application.role.user.code:USER}")
	private String userRoleCode;

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Autowired
	private PasswordHelper passwordHelper;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserCustomDao userCustomDao;

	@Autowired
	private AbstractAddressDao abstractAddressDao;

	@Autowired
	private AddressRoleDao addressRoleDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private RoleCustomDao roleCustomDao;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserFullMapper userFullMapper;

	@Autowired
	private UserLightMapper userLightMapper;

	@Autowired
	private AbstractAddressMapper abstractAddressMapper;

	@Autowired
	private RudiClientRegistrationRepository rudiClientRegistrationRepository;

	@Autowired
	private PageableUtil pageableUtil;

	@Autowired
	private NonDaoUserSearchCriteriaMapper nonDaoUserSearchCriteriaMapper;

	@Autowired
	private ClientRegistrationMapper clientRegistrationMapper;

	@Override
	public Page<User> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable) {
		if (searchCriteria != null) {
			final var pagedUserEntities = userCustomDao.searchUsers(searchCriteria, pageable);
			final var filteredPagedUserEntities = applyNonDaoCriteria(searchCriteria, pagedUserEntities);
			return userMapper.entitiesToDto(filteredPagedUserEntities, pageable);
		}
		return null;
	}

	private Page<UserEntity> applyNonDaoCriteria(UserSearchCriteria searchCriteria,
			Page<UserEntity> pagedUserEntities) {
		final var predicate = nonDaoUserSearchCriteriaMapper.searchCriteriaToPredicate(searchCriteria);
		return pageableUtil.filter(pagedUserEntities, predicate);
	}

	@Override
	public User getUser(UUID uuid) {
		return userFullMapper.entityToDto(userDao.findByUuid(uuid));
	}

	@Override
	public User getUserInfo(UUID uuid) {
		return userLightMapper.entityToDto(userDao.findByUuid(uuid));
	}

	@Override
	public User getUserInfo(String login) {
		UserEntity user = userDao.findByLogin(login);
		return userLightMapper.entityToDto(user);
	}

	@Override
	public User getMe() {
		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser != null) {
			return getUserByLogin(authenticatedUser.getLogin(), false);
		}
		return null;
	}

	@Override
	public User getUserByLogin(String login, boolean withPassword) {
		UserEntity user = userDao.findByLogin(login);
		User result = userFullMapper.entityToDto(user);
		if (result != null && withPassword) {
			result.setPassword(user.getPassword());
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public User createUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User est obligatoire");
		}
		user.setAddresses(null);
		UserEntity entity = userMapper.dtoToEntity(user);
		entity.setUuid(UUID.randomUUID());
		if (StringUtils.isNotEmpty(user.getPassword())) {
			entity.setPassword(passwordHelper.encodePassword(user.getPassword()));
		}

		// mapping des roles
		entity.setRoles(roleListToRoleEntitySet(user.getRoles()));

		validEntity(entity);
		userDao.save(entity);
		return userMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public User updateUser(User user) {
		if (user.getUuid() == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		UserEntity entity = userDao.findByUuid(user.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException(USER_UNKNOWN_MESSAGE + user.getUuid());
		}
		userMapper.dtoToEntity(user, entity);

		// mapping des roles
		entity.setRoles(roleListToRoleEntitySet(user.getRoles()));

		validEntity(entity);
		userDao.save(entity);
		return userMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteUser(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		UserEntity entity = userDao.findByUuid(uuid);
		if (entity == null) {
			throw new IllegalArgumentException(USER_UNKNOWN_MESSAGE + uuid);
		}
		userDao.delete(entity);
	}

	/**
	 * Validation d'un User
	 *
	 * @param entity
	 */
	private void validEntity(UserEntity entity) {
		if (StringUtils.isEmpty(entity.getLogin())) {
			throw new IllegalArgumentException("Invalid empty login");
		}
		if (StringUtils.isEmpty(entity.getPassword())) {
			throw new IllegalArgumentException("Invalid empty password");
		}
		if (null == entity.getType()) {
			throw new IllegalArgumentException("Invalid empty type");
		}
		// au moins un role
		if (CollectionUtils.isEmpty(entity.getRoles())) {
			throw new IllegalArgumentException("Invalid empty roles");
		}
	}

	@Override
	public AbstractAddress getAddress(UUID providerUuid, UUID addressUuid) {
		if (providerUuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		if (addressUuid == null) {
			throw new IllegalArgumentException(UUID_ADRESSE_MISSING_MESSAGE);
		}
		UserEntity userEntity = userDao.findByUuid(providerUuid);
		return abstractAddressMapper.entityToDto(userEntity.lookupAddress(addressUuid));
	}

	@Override
	public List<AbstractAddress> getAddresses(UUID userUuid) {
		if (userUuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		UserEntity userEntity = userDao.findByUuid(userUuid);
		return abstractAddressMapper.entitiesToDto(userEntity.getAddresses());
	}

	@Override
	@Transactional(readOnly = false)
	public AbstractAddress createAddress(UUID providerUuid, AbstractAddress abstractAddress) {
		if (providerUuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		if (abstractAddress == null) {
			throw new IllegalArgumentException(ADDRESS_MISSING_MESSAGE);
		}
		UserEntity userEntity = userDao.findByUuid(providerUuid);
		if (userEntity == null) {
			throw new IllegalArgumentException(USER_UNKNOWN_MESSAGE + providerUuid);
		}
		AbstractAddressEntity abstractAddressEntity = abstractAddressMapper.dtoToEntity(abstractAddress);
		assignAddressRole(abstractAddress, abstractAddressEntity);
		abstractAddressEntity.setUuid(UUID.randomUUID());
		userEntity.getAddresses().add(abstractAddressEntity);
		userDao.save(userEntity);
		return abstractAddressMapper.entityToDto(abstractAddressEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public AbstractAddress updateAddress(UUID providerUuid, @Valid AbstractAddress abstractAddress) {
		if (providerUuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		if (abstractAddress == null || abstractAddress.getUuid() == null) {
			throw new IllegalArgumentException(UUID_ADRESSE_MISSING_MESSAGE);
		}
		UserEntity userEntity = userDao.findByUuid(providerUuid);
		AbstractAddressEntity abstractAddressEntity = userEntity.lookupAddress(abstractAddress.getUuid());
		if (abstractAddressEntity == null) {
			throw new IllegalArgumentException(ADDRESS_UNKNOWN_MESSAGE + abstractAddress.getUuid());
		}
		abstractAddressMapper.dtoToEntity(abstractAddress, abstractAddressEntity);
		assignAddressRole(abstractAddress, abstractAddressEntity);
		abstractAddressDao.save(abstractAddressEntity);
		return abstractAddressMapper.entityToDto(abstractAddressEntity);
	}

	private AbstractAddressEntity assignAddressRole(AbstractAddress abstractAddress,
			AbstractAddressEntity abstractAddressEntity) {
		AddressRoleEntity addressRoleEntity = null;
		if (abstractAddress.getAddressRole() != null && abstractAddress.getAddressRole().getUuid() != null) {
			addressRoleEntity = addressRoleDao.findByUUID(abstractAddress.getAddressRole().getUuid());
		}
		if (addressRoleEntity != null) {
			abstractAddressEntity.setAddressRole(addressRoleEntity);
		}
		if (addressRoleEntity != null && addressRoleEntity.getType() != abstractAddressEntity.getType()) {
			throw new IllegalArgumentException(ADDRESS_ROLE_INVALID_MESSAGE);
		}
		return abstractAddressEntity;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAddress(UUID providerUuid, UUID addressUuid) {
		if (providerUuid == null) {
			throw new IllegalArgumentException(UUID_USER_MISSING_MESSAGE);
		}
		if (addressUuid == null) {
			throw new IllegalArgumentException(UUID_ADRESSE_MISSING_MESSAGE);
		}
		UserEntity userEntity = userDao.findByUuid(providerUuid);
		userEntity.removeAddress(addressUuid);
		userDao.save(userEntity);
	}

	@Override
	@Nullable
	public ClientKey getClientKeyByLogin(String login)
			throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		ClientKey clientKey = null;
		if (StringUtils.isEmpty(login)) {
			throw new IllegalArgumentException(LOGIN_USER_MISSING_MESSAGE);
		}
		ClientRegistration clientRegistration = rudiClientRegistrationRepository.findByUsername(login);

		// si c'est l'utilisateur anonymous, on crée son client id et client secret
		if (clientRegistration == null && login.equals(anonymousUsername)) {
			clientRegistration = rudiClientRegistrationRepository.register(anonymousUsername, anonymousPassword);
		}

		if (clientRegistration != null) {
			clientKey = new ClientKey().clientId(clientRegistration.getClientId())
					.clientSecret(clientRegistration.getClientSecret());
		}

		// TODO Doit-on ajouter un message d'erreur pour indiquer d'activer les API pour pouvoir télécharger des médias ?

		return clientKey;
	}

	private Set<RoleEntity> roleListToRoleEntitySet(List<Role> list) {
		if (list == null) {
			return Collections.emptySet();
		}

		Set<RoleEntity> set = new HashSet<>();
		for (Role role : list) {
			set.add(roleToRoleEntity(role));
		}

		return set;
	}

	private RoleEntity roleToRoleEntity(Role role) {
		if (role.getUuid() == null) {
			return null;
		}
		return roleDao.findByUUID(role.getUuid());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean recordAuthentication(UUID userUuid, boolean success) {
		UserEntity user = userDao.findByUuid(userUuid);
		if (user == null) {
			throw new IllegalArgumentException("Unknown user:" + userUuid);
		}
		if (success) {
			unlockUser(user);
			user.setLastConnexion(LocalDateTime.now());
		} else {
			user.setLastFailedAttempt(LocalDateTime.now());
			user.incrementFailedAttempts();
			if (user.getFailedAttempt() > maxFailedAttempt) {
				if (user.getType() != UserType.ROBOT) {
					user.lockAccount();
					log.info("Lock account for {} due to maxFailedAttempt exceeded", user.getLogin());
				} else {
					log.warn("Record authentication failure for robot {}. Failed attempts execeeded:{}",
							user.getLogin(), user.getFailedAttempt());
				}
			}
		}
		userDao.save(user);
		return user.isAccountLocked();
	}

	@Override
	@Transactional(readOnly = false)
	public void unlockUsers() {
		LocalDateTime d = LocalDateTime.now().minus(Duration.ofMinutes(lockDuration));
		List<UserEntity> users = userDao.findByAccountLockedAndLastFailedAttemptLessThan(true, d);
		if (CollectionUtils.isNotEmpty(users)) {
			users.forEach(this::unlockUser);
		}
	}

	protected void unlockUser(UserEntity user) {
		user.unlockAccount();
		user.resetFailedAttempt();
		user.setLastFailedAttempt(null);
		userDao.save(user);
	}

	@Override
	public org.rudi.microservice.acl.core.bean.ClientRegistrationDto getClientRegistration(String login)
			throws GetClientRegistrationException, BuildClientRegistrationException, SSLException {
		ClientRegistration registration = null;
		// Register anonymous et rudi
		if (login.equals(anonymousUsername)) {
			registration = rudiClientRegistrationRepository.findRegistrationOrRegister(anonymousUsername,
					anonymousPassword);
		} else if (login.equals(rudiUsername)) {
			registration = rudiClientRegistrationRepository.findRegistrationOrRegister(rudiUsername, rudiPassword);
		}
		if (registration == null) {
			registration = rudiClientRegistrationRepository.findByUsername(login);
		}
		return clientRegistrationMapper.entityToDto(registration);
	}

	@Override
	public ClientRegistrationDto registerClientByPassword(String login, String password)
			throws GetClientRegistrationException, BuildClientRegistrationException, SSLException {
		return clientRegistrationMapper
				.entityToDto(rudiClientRegistrationRepository.findRegistrationOrRegister(login, password));
	}

	@Override
	public void addClientRegistration(String login, AccessKeyDto accessKey) {
		val clientAccessKey = new Application().setClientId(accessKey.getClientId())
				.setClientSecret(accessKey.getClientSecret());
		rudiClientRegistrationRepository.addClientRegistration(login, clientAccessKey);
	}

	@Override
	@Transactional
	public void updateUserPassword(String login, PasswordUpdate passwordUpdate) throws AppServiceException {

		// Tente de modifier l'entité
		var user = userDao.findByLogin(login);
		if (user == null) {
			throw new InvalidCredentialsException();
		}
		if (!passwordHelper.buildUserHasPasswordPredicate(passwordUpdate.getOldPassword()).test(user)) {
			throw new InvalidCredentialsException();
		}
		if (passwordUpdate.getNewPassword().equals(passwordUpdate.getOldPassword())) {
			throw new IdenticalNewPasswordException();
		}

		// Vérifie la complexité du nouveau mot de passe.
		passwordHelper.checkPassword(passwordUpdate.getNewPassword());

		// Changement du mot de passe
		user.setPassword(passwordHelper.encodePassword(passwordUpdate.getNewPassword()));
		userDao.save(user);
	}

	@Override
	public Long countUsers() {

		RoleSearchCriteria criteria = new RoleSearchCriteria();
		criteria.setCode(userRoleCode);
		List<RoleEntity> roles = roleCustomDao.searchRoles(criteria);

		List<UUID> roleUuids = CollectionUtils.emptyIfNull(roles).stream().map(LongId::getUuid)
				.collect(Collectors.toList());

		UserSearchCriteria searchCriteria = UserSearchCriteria.builder().roleUuids(roleUuids).build();

		Pageable pageable = PageRequest.of(0, 1);

		final Page<UserEntity> pagedUserEntities = userCustomDao.searchUsers(searchCriteria, pageable);
		return pagedUserEntities.getTotalElements();

	}
}
