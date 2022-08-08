package org.rudi.microservice.projekt.service.project.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.CreateLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.DeleteLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.UpdateLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LinkedDatasetServiceImpl implements LinkedDatasetService {

	private final List<CreateLinkedDatasetFieldProcessor> createLinkedDatasetProcessors;
	private final List<UpdateLinkedDatasetFieldProcessor> updateLinkedDatasetProcessors;
	private final List<DeleteLinkedDatasetFieldProcessor> deleteLinkedDatasetProcessors;

	private final ProjectDao projectDao;
	private final LinkedDatasetMapper linkedDatasetMapper;
	private final LinkedDatasetDao linkedDatasetDao;
	private final DatasetService datasetService;
	private final MetadataDetailsHelper metadataDetailsHelper;

	@Override
	@Transactional // readOnly = false
	public LinkedDataset linkProjectToDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceException {

		// 1) traduction DTO en entité
		val project = getRequiredProjectEntity(projectUuid);
		val linkedDatasetEntity = linkedDatasetMapper.dtoToEntity(linkedDataset);

		// 2) on a besoin de récupérer le niveau de confidentialité de JDD qu'on veut lier
		// on rajoute aussi le producerUuid par la même
		try {
			var metadata = datasetService.getDataset(linkedDataset.getDatasetUuid());
			var confidentiality = metadataDetailsHelper.isRestricted(metadata) ?
					DatasetConfidentiality.RESTRICTED
					: DatasetConfidentiality.OPENED;

			linkedDatasetEntity.setDescription(metadata.getResourceTitle());
			linkedDatasetEntity.setDatasetConfidentiality(confidentiality);
			if(metadata.getProducer() != null) {
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
	public LinkedDataset updateLinkedDataset(UUID projectUuid, LinkedDataset linkedDataset) throws AppServiceException {
		val project = getRequiredProjectEntity(projectUuid);
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
	public void unlinkProjectToDataset(UUID projectUuid, UUID linkedDatasetUuid) throws AppServiceException {
		val project = getRequiredProjectEntity(projectUuid);
		if (CollectionUtils.isNotEmpty(project.getLinkedDatasets())) {
			Iterator<LinkedDatasetEntity> it = project.getLinkedDatasets().iterator();
			while (it.hasNext()) {
				LinkedDatasetEntity linkedDataset = it.next();
				if (linkedDataset.getUuid().equals(linkedDatasetUuid)) {
					for (final DeleteLinkedDatasetFieldProcessor processor : deleteLinkedDatasetProcessors) {
						processor.process(null, linkedDataset);
					}

					it.remove();
					linkedDatasetDao.deleteById(linkedDataset.getId());
					projectDao.save(project);
				}
			}
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
}
