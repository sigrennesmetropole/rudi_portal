package org.rudi.microservice.projekt.service.type.impl;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.rudi.microservice.projekt.core.bean.ProjectTypeSearchCriteria;
import org.rudi.microservice.projekt.service.mapper.ProjectTypeMapper;
import org.rudi.microservice.projekt.service.type.ProjectTypeService;
import org.rudi.microservice.projekt.service.type.impl.validator.ProjectTypeValidator;
import org.rudi.microservice.projekt.service.type.impl.validator.UpdateProjectTypeValidator;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeCustomDao;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectTypeServiceImpl implements ProjectTypeService {

	private final List<UpdateProjectTypeValidator> updateProjectTypeValidators;
	private final ProjectTypeMapper projectTypeMapper;
	private final ProjectTypeDao projectTypeDao;
	private final ProjectTypeCustomDao projectTypeCustomDao;

	@Override
	public Page<ProjectType> searchProjectTypes(ProjectTypeSearchCriteria searchCriteria, Pageable pageable) {
		return projectTypeMapper.entitiesToDto(projectTypeCustomDao.searchProjectTypes(searchCriteria, pageable),
				pageable);
	}

	@Override
	public ProjectType getProjectType(UUID uuid) {
		return projectTypeMapper.entityToDto(projectTypeDao.findByUUID(uuid));
	}

	@Override
	public ProjectType findByCode(String code) {
		return projectTypeMapper.entityToDto(projectTypeDao.findByCode(code));
	}

	@Override
	@Transactional // readOnly = false
	public ProjectType createProjectType(ProjectType projectType) {
		assignReadOnlyFields(projectType);

		val entity = projectTypeMapper.dtoToEntity(projectType);
		val savedEntity = projectTypeDao.save(entity);
		return projectTypeMapper.entityToDto(savedEntity);
	}

	private void assignReadOnlyFields(ProjectType projectType) {
		projectType.setUuid(UUID.randomUUID());
	}

	@Override
	@Transactional // readOnly = false
	public ProjectType updateProjectType(ProjectType projectType) throws AppServiceException {
		validateUpdate(projectType);

		final var entity = projectTypeDao.findByUUID(projectType.getUuid());

		projectTypeMapper.dtoToEntity(projectType, entity);
		projectTypeDao.save(entity);

		return projectTypeMapper.entityToDto(entity);
	}

	private void validateUpdate(ProjectType projectType) throws AppServiceException {
		for (final ProjectTypeValidator validator : updateProjectTypeValidators) {
			validator.validate(projectType);
		}
	}

	@Override
	@Transactional // readOnly = false
	public void deleteProjectType(UUID uuid) {
		val entity = projectTypeDao.findByUUID(uuid);
		projectTypeDao.delete(entity);
	}

	@Override
	public ProjectType getProjectTypeByCode(String code) {
		return projectTypeMapper.entityToDto(projectTypeDao.findByCode(code));
	}
}
