/**
 * 
 */
package org.rudi.microservice.acl.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.ClientKey;
import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.service.helper.PasswordHelper;
import org.rudi.microservice.acl.service.mapper.AbstractAddressMapper;
import org.rudi.microservice.acl.service.mapper.UserFullMapper;
import org.rudi.microservice.acl.service.mapper.UserMapper;
import org.rudi.microservice.acl.service.user.UserService;
import org.rudi.microservice.acl.storage.dao.address.AbstractAddressDao;
import org.rudi.microservice.acl.storage.dao.address.AddressRoleDao;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service de gestion des utilisateurs RUDI
 *
 */
@Service
@Transactional(readOnly = true)
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

	@Value("${apimanager.oauth2.client.anonymous.password}")
	private String anonymousPassword;

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
	private UserMapper userMapper;

	@Autowired
	private UserFullMapper userFullMapper;

	@Autowired
	private AbstractAddressMapper abstractAddressMapper;

	@Autowired
	private CustomClientRegistrationRepository customClientRegistrationRepository;

	@Override
	public Page<User> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable) {
		if (searchCriteria != null) {
			return userMapper.entitiesToDto(userCustomDao.searchUsers(searchCriteria, pageable), pageable);
		}
		return null;
	}

	@Override
	public User getUser(UUID uuid) {
		return userFullMapper.entityToDto(userDao.findByUuid(uuid));
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
		if (withPassword) {
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
	public ClientKey getClientKeyByLogin(String login) throws SSLException {
		ClientKey clientKey = null;
		if (StringUtils.isEmpty(login)) {
			throw new IllegalArgumentException(LOGIN_USER_MISSING_MESSAGE);
		}
		ClientRegistration clientRegistration = customClientRegistrationRepository.findByRegistrationId(login).block();
		if (clientRegistration != null) {
			clientKey = new ClientKey().clientId(clientRegistration.getClientId()).clientSecret(clientRegistration.getClientSecret());
		}
		// si c'est l'utilisateur anonymous, on crée son client id et client secret
		else if (login.equals(anonymousUsername)) {
			clientRegistration = customClientRegistrationRepository.addClientRegistration(anonymousUsername, anonymousPassword);
			clientKey = new ClientKey().clientId(clientRegistration.getClientId()).clientSecret(clientRegistration.getClientSecret());
		}

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
}
