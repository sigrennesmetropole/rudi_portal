package org.rudi.microservice.projekt.service.project.impl;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.service.project.NewDatasetRequestService;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewDatasetRequestServiceImpl implements NewDatasetRequestService {
	private final ProjectMapper projectMapper;
	private final ProjectCustomDao projectCustomDao;

	@Override
	public Project findProjectByNewDatasetRequest(UUID newDatasetRequestUuid) throws AppServiceException {
		ProjectEntity projectEntity = null;
		if (newDatasetRequestUuid == null) {
			throw new AppServiceBadRequestException("UUID non renseigné");
		}
		projectEntity = projectCustomDao
				.findProjectByNewDatasetRequestUuid(newDatasetRequestUuid);
		return projectMapper.entityToDto(projectEntity);
	}
}
