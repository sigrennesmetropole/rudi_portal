package org.rudi.microservice.strukture.service.organization.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.rudi.microservice.strukture.service.mapper.OrganizationMapper;
import org.rudi.microservice.strukture.service.mapper.OrganizationMemberMapper;
import org.rudi.microservice.strukture.service.organization.OrganizationService;
import org.rudi.microservice.strukture.service.organization.impl.fields.CreateOrganizationFieldProcessor;
import org.rudi.microservice.strukture.service.organization.impl.fields.UpdateOrganizationFieldProcessor;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationCustomDao;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationDao organizationDao;
	private final OrganizationCustomDao organizationCustomDao;
	private final OrganizationMapper organizationMapper;
	private final Collection<CreateOrganizationFieldProcessor> createOrganizationFieldProcessors;
	private final Collection<UpdateOrganizationFieldProcessor> updateOrganizationFieldProcessors;

	private final OrganizationMemberMapper organizationMemberMapper;

	@Override
	public Organization getOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		return organizationMapper.entityToDto(entity);
	}

	@Nonnull
	private OrganizationEntity getOrganizationEntity(UUID uuid) throws AppServiceNotFoundException {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		val entity = organizationDao.findByUuid(uuid);
		if (entity == null) {
			throw new AppServiceNotFoundException(OrganizationEntity.class, uuid);
		}
		return entity;
	}

	@Override
	@Transactional(readOnly = false)
	public Organization createOrganization(Organization organization) {
		val entity = organizationMapper.dtoToEntity(organization);
		for (final CreateOrganizationFieldProcessor processor : createOrganizationFieldProcessors) {
			processor.processBeforeCreate(entity);
		}
		organizationDao.save(entity);
		return organizationMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateOrganization(Organization organization) throws AppServiceException {
		val existingEntity = getOrganizationEntity(organization.getUuid());
		for (final UpdateOrganizationFieldProcessor processor : updateOrganizationFieldProcessors) {
			processor.processBeforeUpdate(organization, existingEntity);
		}
		organizationMapper.dtoToEntity(organization, existingEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		organizationDao.delete(entity);
	}

	@Override
	public Page<Organization> searchOrganizations(OrganizationSearchCriteria searchCriteria, Pageable pageable) {
		return organizationMapper.entitiesToDto(organizationCustomDao.searchOrganizations(searchCriteria, pageable), pageable);
	}

	@Override
	@Transactional // readOnly = false
	public OrganizationMember addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember) throws AppServiceNotFoundException {
		val organizationEntity = getOrganizationEntity(organizationUuid);
		val organizationMemberEntity = organizationMemberMapper.dtoToEntity(organizationMember);
		organizationEntity.getMembers().add(organizationMemberEntity);
		return organizationMemberMapper.entityToDto(organizationMemberEntity);
	}

	@Override
	public List<OrganizationMember> getOrganizationMembers(UUID organizationUuid) throws AppServiceNotFoundException {
		val organizationEntity = getOrganizationEntity(organizationUuid);
		return organizationMemberMapper.entitiesToDto(organizationEntity.getMembers());
	}

	@Override
	@Transactional // readOnly = false
	public void removeOrganizationMembers(UUID organizationUuid, UUID userUuid) throws AppServiceNotFoundException, CannotRemoveLastAdministratorException {
		val organizationEntity = getOrganizationEntity(organizationUuid);

		final var anyAdministratorBeforeRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR)
				.findAny();

		organizationEntity.getMembers().removeIf(member -> member.getUserUuid().equals(userUuid));

		final var anyAdministratorAfterRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR)
				.findAny();

		if (anyAdministratorBeforeRemovingMember.isPresent() && anyAdministratorAfterRemovingMember.isEmpty()) {
			throw new CannotRemoveLastAdministratorException(userUuid, organizationUuid);
		}
	}

}
