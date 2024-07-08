/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.projectkey.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.UserType;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.acl.core.bean.ProjectKey;
import org.rudi.microservice.acl.core.bean.ProjectKeystore;
import org.rudi.microservice.acl.core.bean.ProjectKeystoreSearchCriteria;
import org.rudi.microservice.acl.service.helper.PasswordHelper;
import org.rudi.microservice.acl.service.mapper.projectkey.ProjectKeyMapper;
import org.rudi.microservice.acl.service.mapper.projectkey.ProjectKeystoreMapper;
import org.rudi.microservice.acl.service.projectkey.ProjectKeyValidator;
import org.rudi.microservice.acl.service.projectkey.ProjectKeystoreService;
import org.rudi.microservice.acl.service.projectkey.ProjectKeystoreValidator;
import org.rudi.microservice.acl.storage.dao.projectkey.ProjectKeyDao;
import org.rudi.microservice.acl.storage.dao.projectkey.ProjectKeystoreCustomDao;
import org.rudi.microservice.acl.storage.dao.projectkey.ProjectKeystoreDao;
import org.rudi.microservice.acl.storage.dao.user.UserDao;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeyEntity;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeystoreEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Service
@ConditionalOnProperty(name = "acl.project-key-store.implementation", havingValue = "bdd", matchIfMissing = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectKeystoreServiceImpl implements ProjectKeystoreService {

	private static final String CAPITAL_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	private static final String SPECIAL_CHARECTER = "!@#$";
	private static final String NUMBERS = "1234567890";
	private static final int LENGTH = 20;

	private Random random = null;

	private final ProjectKeystoreDao projectKeystoreDao;

	private final ProjectKeystoreCustomDao projectKeystoreCustomDao;

	private final ProjectKeyDao projectKeyDao;

	private final UserDao userDao;

	private final ProjectKeyMapper projectKeyMapper;

	private final ProjectKeystoreMapper projectKeystoreMapper;

	private final ProjectKeystoreValidator projectKeystoreValidator;

	private final ProjectKeyValidator projectKeyValidator;

	private final PasswordHelper passwordHelper;

	@Override
	@Transactional(readOnly = false)
	public ProjectKeystore createProjectKeystore(ProjectKeystore projectKeystore) throws AppServiceException {
		projectKeystoreValidator.validateCreation(projectKeystore);
		ProjectKeystoreEntity entity = projectKeystoreMapper.dtoToEntity(projectKeystore);
		entity.setUuid(UUID.randomUUID());
		return projectKeystoreMapper.entityToDto(projectKeystoreDao.save(entity));
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProjectKeystore(UUID projectKeystoreUuid) throws AppServiceBadRequestException {
		ProjectKeystoreEntity entity = getProjectKeystoreEntity(projectKeystoreUuid);
		if (CollectionUtils.isNotEmpty(entity.getProjectKeys())) {
			entity.getProjectKeys().forEach(projetKey -> {
				if (projetKey.getClient() != null) {
					userDao.delete(projetKey.getClient());
				}
			});
		}
		projectKeystoreDao.delete(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public ProjectKey createProjectKey(UUID projectKeystoreUuid, ProjectKey projectKey) throws AppServiceException {
		// récupération du keystore
		ProjectKeystoreEntity entity = getProjectKeystoreEntity(projectKeystoreUuid);
		// validation de l'input
		projectKeyValidator.validateCreation(projectKey);
		// génération du mot de passe
		String password = generatePassword();
		// création de l'utilisateur (avec encodage du mot de passe enrichie)
		UserEntity user = createProjectKeyUSer(projectKeystoreUuid, projectKey, password);

		// création de la clé projet
		ProjectKeyEntity projectKeyEntity = createProjectKey(projectKey, user);
		// ajout dans le store
		entity.addProjectKey(projectKeyEntity);
		// conversion
		ProjectKey result = projectKeyMapper.entityToDto(projectKeyEntity);
		// assignation du mot de passe réel (sinon on a l'encodé)
		result.setClientSecret(password);
		projectKeystoreDao.save(entity);
		return result;
	}

	private ProjectKeyEntity createProjectKey(ProjectKey projectKey, UserEntity user) {
		ProjectKeyEntity projectKeyEntity = projectKeyMapper.dtoToEntity(projectKey);
		projectKeyEntity.setClient(user);
		projectKeyEntity.setCreationDate(LocalDateTime.now());
		projectKeyEntity = projectKeyDao.save(projectKeyEntity);
		return projectKeyEntity;
	}

	private String generatePassword() {
		StringBuilder builder = new StringBuilder(LENGTH);
		String combinedChars = CAPITAL_CASE_LETTERS + LOWER_CASE_LETTERS + SPECIAL_CHARECTER + NUMBERS;
		Random localRandom = getSecureRandom();
		if (localRandom == null) {
			return UUID.randomUUID().toString();
		}

		builder.append(LOWER_CASE_LETTERS.charAt(localRandom.nextInt(LOWER_CASE_LETTERS.length())));
		builder.append(CAPITAL_CASE_LETTERS.charAt(localRandom.nextInt(CAPITAL_CASE_LETTERS.length())));
		builder.append(SPECIAL_CHARECTER.charAt(localRandom.nextInt(SPECIAL_CHARECTER.length())));
		builder.append(NUMBERS.charAt(localRandom.nextInt(NUMBERS.length())));

		for (int i = 0; i < LENGTH - 4; i++) {
			builder.append(combinedChars.charAt(localRandom.nextInt(combinedChars.length())));
		}
		return builder.toString();
	}

	private Random getSecureRandom() {
		if (random == null) {
			try {
				random = SecureRandom.getInstanceStrong();
			} catch (Exception e) {
				log.warn("Failed to create secure random...", e);
			}
		}
		return random;
	}

	private String buildPassword(UUID projectKeystoreUuid, String password) {
		return projectKeystoreUuid.toString() + ":" + UserType.API.name() + ":" + password;
	}

	private UserEntity createProjectKeyUSer(UUID projectKeystoreUuid, ProjectKey projectKey, String password) {
		UserEntity user = new UserEntity();
		user.setUuid(UUID.randomUUID());
		user.setLastname(projectKey.getName());
		user.setLogin(UUID.randomUUID().toString());
		user.setCompany(projectKeystoreUuid.toString());
		user.setType(UserType.API);
		user.setPassword(passwordHelper.encodePassword(buildPassword(projectKeystoreUuid, password)));
		user = userDao.save(user);
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProjectKey(UUID projectKeystoreUuid, UUID projectKeyUuid) throws AppServiceBadRequestException {
		// récupération du keystore
		ProjectKeystoreEntity entity = getProjectKeystoreEntity(projectKeystoreUuid);
		// suppression de la liste des clés
		ProjectKeyEntity item = entity.removeProjectKeyByUUID(projectKeyUuid);
		if (item == null) {
			throw new AppServiceBadRequestException("Invalid projectKeyUuid");
		} else {
			if (item.getClient() != null) {
				userDao.delete(item.getClient());
			}
			projectKeyDao.delete(item);
		}
		projectKeystoreDao.save(entity);

	}

	@Override
	public Page<ProjectKeystore> searchProjectKey(ProjectKeystoreSearchCriteria searchCriteria, Pageable pageable) {
		return projectKeystoreMapper.entitiesToDto(projectKeystoreCustomDao.searchUsers(searchCriteria, pageable),
				pageable);
	}

	@Override
	public ProjectKeystore getProjectKeystoreByUUID(UUID projectKeystoreUuid) throws AppServiceException {
		ProjectKeystoreEntity entity = getProjectKeystoreEntity(projectKeystoreUuid);
		return projectKeystoreMapper.entityToDto(entity);
	}

	protected ProjectKeystoreEntity getProjectKeystoreEntity(UUID uuid) throws AppServiceBadRequestException {
		ProjectKeystoreEntity result = projectKeystoreDao.findByUuid(uuid);
		if (result == null) {
			throw new AppServiceBadRequestException("Invalid projet keystore uuid");
		}
		return result;
	}

}
