package org.rudi.microservice.projekt.service.project.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.PagedLinkedDatasetList;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.helper.ProjektAuthorisationHelper;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
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
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LinkedDatasetServiceImpl implements LinkedDatasetService {
	private static final String VIEW_COMMENT_FORM_KEY = "VIEW-COMMENT";
	private final List<CreateLinkedDatasetFieldProcessor> createLinkedDatasetProcessors;
	private final List<UpdateLinkedDatasetFieldProcessor> updateLinkedDatasetProcessors;
	private final List<DeleteLinkedDatasetFieldProcessor> deleteLinkedDatasetProcessors;

	private final ProjectDao projectDao;
	private final LinkedDatasetMapper linkedDatasetMapper;
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final DatasetService datasetService;
	private final MetadataDetailsHelper metadataDetailsHelper;
	private final MyInformationsHelper myInformationsHelper;
	private final FormHelper formHelper;
	private final ProjektAuthorisationHelper projektAuthorisationHelper;

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	@Override
	@Transactional // readOnly = false
	public LinkedDataset linkProjectToDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceException, APIManagerException {

		// 1) traduction DTO en entité
		ProjectEntity project = getRequiredProjectEntity(projectUuid);

		projektAuthorisationHelper.checkRightsAdministerProjectDataset(project);

		// Vérification des droits de l'utilisateur et du statut du projet avant d'ajouter le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(project);

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
	public List<LinkedDataset> getLinkedDatasets(UUID projectUuid, List<LinkedDatasetStatus> status)
			throws AppServiceNotFoundException {
		val project = getRequiredProjectEntity(projectUuid);
		return project.getLinkedDatasets().stream().map(linkedDatasetMapper::entityToDto)
				// si status nul on renvoie tout sinon fitlrage par status
				.filter(linked -> status == null || status.contains(linked.getLinkedDatasetStatus()))
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
		projektAuthorisationHelper.checkRightsAdministerProjectDataset(project);

		// Vérification des droits de l'utilisateur et du statut du projet avant de modifier le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(project);

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

		projektAuthorisationHelper.checkRightsAdministerProjectDataset(project);

		// Vérification du statut du projet avant de supprimer le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(project);

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
			throws AppServiceException {
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

	@Override
	public Form getDecisionInformations(UUID projectUuid, UUID linkedDatasetUuid) throws AppServiceException {

		ProjectEntity projectEntity = getRequiredProjectEntity(projectUuid);

		// Vérification de la cohérence des données pour le projet + dataset
		LinkedDatasetEntity linkedDatasetEntity = IterableUtils.find(projectEntity.getLinkedDatasets(),
				l -> l.getUuid().equals(linkedDatasetUuid));
		if (linkedDatasetEntity == null) {
			// linked dataset non trouvé pour ce project
			throw new AppServiceNotFoundException(LinkedDatasetEntity.class, linkedDatasetUuid);
		}

		checkRightsGetDecisionInformations(projectEntity, linkedDatasetEntity);

		// Retourne le formulaire avec les bonnes informations, si le commentaire est renseigné.
		Form form = null;
		try {
			Map<String, Object> mapOfData = formHelper.hydrateData(linkedDatasetEntity.getData());
			// Si le commentaire n'est pas renseigné, on ne renvoit aucune info
			if (MapUtils.isNotEmpty(mapOfData) && StringUtils
					.isNotEmpty(MapUtils.getString(mapOfData, "messageToProjectOwner", StringUtils.EMPTY))) {
				form = formHelper.lookupViewForm(linkedDatasetTaskService.getProcessDefinitionKey(),
						VIEW_COMMENT_FORM_KEY);
				if (form != null) {
					formHelper.fillForm(form, mapOfData);
				}
			}
		} catch (Exception e) {
			throw new AppServiceException("Failed to get decision informations", e);
		}
		return form;
	}

	/**
	 * Définition de l'ouverture des droits la fonctionnalité de récupération des informations de decision d'accès au JDD : Le projectowner ou un membre
	 * de l'organisation peut accéder aux commentaires / Le producteur ou les membres d'une organisation ayant établis le commentaire d'une demande ont
	 * accès aux commentaires (uniquement via Postman) / L'administrateur peut accéder aux commentaires (uniquement via Postman) / L'animateur ne peut pas
	 * accéder aux commentaires / Un autre user ne peut pas accéder aux comment
	 * 
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity       l'entité projet pour laquelle vérifier le droit d'accès
	 * @param linkedDatasetEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	private void checkRightsGetDecisionInformations(ProjectEntity projectEntity,
			LinkedDatasetEntity linkedDatasetEntity) throws GetOrganizationMembersException, GetOrganizationException,
			AppServiceUnauthorizedException, MissingParameterException {

		Map<String, Boolean> accessRightsByRole = ProjektAuthorisationHelper.getADMINISTRATOR_ACCESS();

		// Vérification des droits d'accès
		if (!(projektAuthorisationHelper.isAccessGrantedByRole(accessRightsByRole)
				|| projektAuthorisationHelper.isAccessGrantedForUserOnProject(projectEntity)
				|| projektAuthorisationHelper.isAccessGrantedForUserOnLinkedDataset(linkedDatasetEntity))) {
			throw new AppServiceUnauthorizedException("Accès non autorisé à la fonctionnalité pour l'utilisateur");
		}
	}

	/**
	 * @param datasetUuid
	 * @return
	 */
	@Override
	public boolean isMyAccessGratedToDataset(UUID datasetUuid) throws GetOrganizationException, AppServiceUnauthorizedException {
		if(datasetUuid == null){
			log.error("Dataset Uuid null. Requête impossible.");
			return false;
		}

		//on récupère l'utilisateur connecté et l'ensemble des organisations dont il fait partie.
		List<UUID> meAndMyOrganizations = myInformationsHelper.getMeAndMyOrganizationUuids();
		if(CollectionUtils.isEmpty(meAndMyOrganizations)){
			log.error("Utilisateur connecté null");
			return false;
		}

		var linkedDatasetSearchCriteria = new LinkedDatasetSearchCriteria()
				.datasetUuid(datasetUuid)
				.projectOwnerUuids(meAndMyOrganizations)
				.status(List.of(LinkedDatasetStatus.VALIDATED))
				.endDateIsNotOver(true);
		final var linkedDatasetEntities = linkedDatasetCustomDao.searchLinkedDatasets(linkedDatasetSearchCriteria, Pageable.unpaged());
		return linkedDatasetEntities.getTotalElements() > 0;
	}
}
