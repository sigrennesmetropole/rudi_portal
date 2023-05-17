package org.rudi.microservice.projekt.service.project.impl;

import java.util.List;
import java.util.UUID;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.core.bean.PagedNewDatasetRequestList;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.service.project.NewDatasetRequestService;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestCustomDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewDatasetRequestServiceImpl implements NewDatasetRequestService {
	private final ProjectMapper projectMapper;
	private final NewDatasetRequestMapper newDatasetRequestMapper;
	private final ProjectCustomDao projectCustomDao;
	private final NewDatasetRequestCustomDao newDatasetRequestCustomDao;
	private final MyInformationsHelper myInformationsHelper;

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

	@Override
	public PagedNewDatasetRequestList searchMyNewDatasetRequests(NewDatasetRequestSearchCriteria criteria, Pageable pageable) throws AppServiceNotFoundException, AppServiceException {
		//Récupération des UUIDs du connectedUser et de ses organisations.
		List<UUID> uuids = myInformationsHelper.getMeAndMyOrganizationUuids();
		if (CollectionUtils.isEmpty(uuids)) {
			return new PagedNewDatasetRequestList().total(0L).elements(List.of());
		}

		//Création d'un custom criteria contenant cette liste d'UUIDs
		NewDatasetRequestSearchCriteria customCriteria = new NewDatasetRequestSearchCriteria();
		customCriteria.status(criteria.getStatus());
		customCriteria.setProjectOwnerUuids(uuids);

		Page<NewDatasetRequestEntity> pages = newDatasetRequestCustomDao.searchNewDatasetRequest(customCriteria, pageable);
		List<NewDatasetRequest> request = newDatasetRequestMapper.entitiesToDto(pages.getContent());

		return new PagedNewDatasetRequestList().total(pages.getTotalElements()).elements(request);
	}
}
