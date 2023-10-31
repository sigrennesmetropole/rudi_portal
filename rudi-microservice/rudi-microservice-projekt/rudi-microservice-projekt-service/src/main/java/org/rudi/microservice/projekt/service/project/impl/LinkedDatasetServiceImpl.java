package org.rudi.microservice.projekt.service.project.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.PagedLinkedDatasetList;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
import org.rudi.microservice.projekt.service.project.impl.fields.AddDatasetToProjectProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.DeleteDatasetFromProjectProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.UpdateDatasetInProjectProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.CreateLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.DeleteLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.UpdateLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LinkedDatasetServiceImpl implements LinkedDatasetService {

	private final List<CreateLinkedDatasetFieldProcessor> createLinkedDatasetProcessors;
	private final List<UpdateLinkedDatasetFieldProcessor> updateLinkedDatasetProcessors;
	private final List<DeleteLinkedDatasetFieldProcessor> deleteLinkedDatasetProcessors;

	private final List<AddDatasetToProjectProcessor> addDatasetToProjectProcessors;
	private final List<DeleteDatasetFromProjectProcessor> deleteDatasetFromProjectProcessors;
	private final List<UpdateDatasetInProjectProcessor> updateDatasetInProjectProcessors;

	private final ProjectDao projectDao;
	private final LinkedDatasetMapper linkedDatasetMapper;
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final DatasetService datasetService;
	private final MetadataDetailsHelper metadataDetailsHelper;
	private final MyInformationsHelper myInformationsHelper;

	@Override
	@Transactional // readOnly = false
	public LinkedDataset linkProjectToDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceException, APIManagerException {

		// 1) traduction DTO en entité
		ProjectEntity project = getRequiredProjectEntity(projectUuid);

		// Vérification des droits de l'utilisateur et du statut du projet avant d'ajouter le lien sur le dataset
		for (final AddDatasetToProjectProcessor processor : addDatasetToProjectProcessors) {
			processor.process(null, project);
		}

		val linkedDatasetEntity = linkedDatasetMapper.dtoToEntity(linkedDataset);

		// 2) on a besoin de récupérer le niveau de confidentialité de JDD qu'on veut lier
		// on rajoute aussi le producerUuid par la même
		try {
			var metadata = datasetService.getDataset(linkedDataset.getDatasetUuid());

			final var accessConditionConfidentiality = getAccessConditionConfidentiality(metadata);

			linkedDatasetEntity.setDescription(metadata.getResourceTitle());
			linkedDatasetEntity.setDatasetConfidentiality(accessConditionConfidentiality);
			if (metadata.getProducer() != null) {
				linkedDatasetEntity.setDatasetOrganisationUuid(metadata.getProducer().getOrganizationId());
			}
		} catch (DataverseAPIException dnfe) {
			throw new IllegalArgumentException("Unknown dataset " + linkedDataset.getDatasetUuid());
		}

		// 3) on process les champs
		for (final CreateLinkedDatasetFieldProcessor processor : createLinkedDatasetProcessors) {
			processor.process(linkedDatasetEntity, null);
		}

		linkedDatasetEntity.setUuid(UUID.randomUUID());
		linkedDatasetEntity.setCreationDate(LocalDateTime.now());
		linkedDatasetEntity.setUpdatedDate(linkedDatasetEntity.getCreationDate());

		project.getLinkedDatasets().add(linkedDatasetEntity);
		return linkedDatasetMapper.entityToDto(linkedDatasetEntity);
	}

	public DatasetConfidentiality getAccessConditionConfidentiality(Metadata metadata) {
		if (metadataDetailsHelper.isSelfdata(metadata)) {
			return DatasetConfidentiality.SELFDATA;
		}
		if (metadataDetailsHelper.isRestricted(metadata)) {
			return DatasetConfidentiality.RESTRICTED;
		}
		return DatasetConfidentiality.OPENED;

	}

	@Override
	public List<LinkedDataset> getLinkedDatasets(UUID projectUuid, LinkedDatasetStatus status)
			throws AppServiceNotFoundException {
		val project = getRequiredProjectEntity(projectUuid);
		return project.getLinkedDatasets().stream().map(linkedDatasetMapper::entityToDto)
				// si status nul on renvoie tout sinon fitlrage par status
				.filter(linked -> linked.getLinkedDatasetStatus().equals(status) || status == null)
				.collect(Collectors.toList());
	}

	@Override
	public LinkedDataset getLinkedDataset(UUID projectUuid, UUID linkedDatasetUuid) throws AppServiceNotFoundException {
		val project = getRequiredProjectEntity(projectUuid);
		List<LinkedDataset> toReturn = new ArrayList<>();
		project.getLinkedDatasets().forEach((element -> {
			if (element.getUuid().equals(linkedDatasetUuid)) {
				toReturn.add(linkedDatasetMapper.entityToDto(element));
			}
		}));
		if (!toReturn.isEmpty()) {
			return toReturn.get(0);
		}
		return null;
	}

	@Override
	@Transactional
	public LinkedDataset updateLinkedDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceException, APIManagerException {

		val project = getRequiredProjectEntity(projectUuid);

		// Vérification des droits de l'utilisateur et du statut du projet avant de modifier le lien sur le dataset
		for (final UpdateDatasetInProjectProcessor processor : updateDatasetInProjectProcessors) {
			processor.process(null, project);
		}

		val entity = linkedDatasetMapper.dtoToEntity(linkedDataset);
		if (CollectionUtils.isNotEmpty(project.getLinkedDatasets())) {
			Iterator<LinkedDatasetEntity> it = project.getLinkedDatasets().iterator();
			while (it.hasNext()) {
				LinkedDatasetEntity linkedDatasetEntity = it.next();
				if (linkedDatasetEntity.getUuid().equals(linkedDataset.getUuid())) {

					for (final UpdateLinkedDatasetFieldProcessor processor : updateLinkedDatasetProcessors) {
						processor.process(entity, linkedDatasetEntity);
					}

					linkedDatasetMapper.dtoToEntity(linkedDataset, linkedDatasetEntity);
					return linkedDatasetMapper.entityToDto(linkedDatasetEntity);
				}
			}
		}

		throw new AppServiceNotFoundException(entity, null);
	}

	@Override
	@Transactional // readOnly = false
	public void unlinkProjectToDataset(UUID projectUuid, UUID linkedDatasetUuid)
			throws AppServiceException, APIManagerException {
		val project = getRequiredProjectEntity(projectUuid);

		// Vérification des droits de l'utilisateur et du statut du projet avant de supprimer le lien sur le dataset
		for (final DeleteDatasetFromProjectProcessor processor : deleteDatasetFromProjectProcessors) {
			processor.process(null, project);
		}

		if (CollectionUtils.isNotEmpty(project.getLinkedDatasets())) {
			Iterator<LinkedDatasetEntity> it = project.getLinkedDatasets().iterator();
			while (it.hasNext()) {
				LinkedDatasetEntity linkedDataset = it.next();
				if (linkedDataset.getUuid().equals(linkedDatasetUuid)) {
					for (final DeleteLinkedDatasetFieldProcessor processor : deleteLinkedDatasetProcessors) {
						processor.process(null, linkedDataset);
					}

					it.remove();
				}
			}
			projectDao.save(project);
		}
	}

	@Nonnull
	private ProjectEntity getRequiredProjectEntity(UUID projectUuid) throws AppServiceNotFoundException {
		final var project = projectDao.findByUuid(projectUuid);
		if (project == null) {
			throw new AppServiceNotFoundException(ProjectEntity.class, projectUuid);
		}
		return project;
	}

	@Override
	public PagedLinkedDatasetList searchMyLinkedDatasets(LinkedDatasetSearchCriteria criteria, Pageable pageable)
			throws AppServiceNotFoundException, AppServiceException {
		// Récupération des UUIDs du connectedUser et de ses organisations.

		List<UUID> uuids = myInformationsHelper.getMeAndMyOrganizationUuids();
		if (CollectionUtils.isEmpty(uuids)) {
			return new PagedLinkedDatasetList().total(0L).elements(List.of());
		}

		// Rajout des owner uuid dans le criteria
		criteria.setProjectOwnerUuids(uuids);

		Page<LinkedDatasetEntity> pages = linkedDatasetCustomDao.searchLinkedDatasets(criteria, pageable);
		List<LinkedDataset> request = linkedDatasetMapper.entitiesToDto(pages.getContent());

		return new PagedLinkedDatasetList().total(pages.getTotalElements()).elements(request);
	}
}
