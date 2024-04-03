package org.rudi.microservice.projekt.service.project.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.core.bean.PagedNewDatasetRequestList;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.helper.ProjektAuthorisationHelper;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.service.project.NewDatasetRequestService;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestCustomDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewDatasetRequestServiceImpl implements NewDatasetRequestService {
	private static final String VIEW_COMMENT_FORM_KEY = "VIEW-COMMENT";
	private final ProjectMapper projectMapper;
	private final NewDatasetRequestMapper newDatasetRequestMapper;
	private final ProjectCustomDao projectCustomDao;
	private final ProjectDao projectDao;
	private final NewDatasetRequestCustomDao newDatasetRequestCustomDao;
	private final MyInformationsHelper myInformationsHelper;
	private final ProjektAuthorisationHelper projektAuthorisationHelper;

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;
	private final FormHelper formHelper;

	@Override
	public Project findProjectByNewDatasetRequest(UUID newDatasetRequestUuid) throws AppServiceException {
		ProjectEntity projectEntity;
		if (newDatasetRequestUuid == null) {
			throw new AppServiceBadRequestException("UUID non renseigné");
		}
		projectEntity = projectCustomDao.findProjectByNewDatasetRequestUuid(newDatasetRequestUuid);
		return projectMapper.entityToDto(projectEntity);
	}

	@Override
	public PagedNewDatasetRequestList searchMyNewDatasetRequests(NewDatasetRequestSearchCriteria criteria,
			Pageable pageable) throws AppServiceException {
		// Récupération des UUIDs du connectedUser et de ses organisations.
		List<UUID> uuids = myInformationsHelper.getMeAndMyOrganizationUuids();
		if (CollectionUtils.isEmpty(uuids)) {
			return new PagedNewDatasetRequestList().total(0L).elements(List.of());
		}

		// Création d'un custom criteria contenant cette liste d'UUIDs
		NewDatasetRequestSearchCriteria customCriteria = new NewDatasetRequestSearchCriteria();
		customCriteria.status(criteria.getStatus());
		customCriteria.setProjectOwnerUuids(uuids);

		Page<NewDatasetRequestEntity> pages = newDatasetRequestCustomDao.searchNewDatasetRequest(customCriteria,
				pageable);
		List<NewDatasetRequest> request = newDatasetRequestMapper.entitiesToDto(pages.getContent());

		return new PagedNewDatasetRequestList().total(pages.getTotalElements()).elements(request);
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
	public Form getDecisionInformations(UUID projectUuid, UUID newDatasetRequestUUID) throws AppServiceException {

		// Retourne le formulaire avec les bonnes informations, si le commentaire est renseigné.
		ProjectEntity projectEntity = getRequiredProjectEntity(projectUuid);

		// Vérification de la cohérence des données pour le projet + dataset
		NewDatasetRequestEntity newDatasetRequestEntity = IterableUtils.find(projectEntity.getDatasetRequests(),
				l -> l.getUuid().equals(newDatasetRequestUUID));
		if (newDatasetRequestEntity == null) {
			// dataset request non trouvé pour ce project
			throw new AppServiceNotFoundException(NewDatasetRequestEntity.class, newDatasetRequestUUID);
		}

		checkRightsGetDecisionInformations(projectEntity);

		Form form = null;
		try {
			Map<String, Object> mapOfData = formHelper.hydrateData(newDatasetRequestEntity.getData());
			// Si le commentaire n'est pas renseigné, on ne renvoie aucune info
			if (MapUtils.isNotEmpty(mapOfData) && StringUtils
					.isNotEmpty(MapUtils.getString(mapOfData, "messageToProjectOwner", StringUtils.EMPTY))) {
				form = formHelper.lookupViewForm(newDatasetRequestTaskService.getProcessDefinitionKey(),
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
	 * Définition de l'ouverture des droits la fonctionnalité de récupération des informations de decision de nouvelle demande de JDD : Le projectowner ou
	 * un membre de l'organisation peut accéder aux commentaires / Les animateurs ont accès aux commentaires (uniquement via Postman) / L'administrateur
	 * peut accéder aux commentaires (uniquement via Postman) / Un autre user ne peut pas accéder aux commentaires
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	private void checkRightsGetDecisionInformations(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, AppServiceUnauthorizedException, MissingParameterException {
		Map<String, Boolean> accessRightsByRole = ProjektAuthorisationHelper.getADMINISTRATOR_MODERATOR_ACCESS();
		// Vérification des droits d'accès
		// les droits autorisés dans accessRights doivent être cohérents avec ceux définis en PreAuth coté Controller
		if (!(projektAuthorisationHelper.isAccessGrantedByRole(accessRightsByRole)
				|| projektAuthorisationHelper.isAccessGrantedForUserOnProject(projectEntity))) {
			throw new AppServiceUnauthorizedException("Accès non autorisé à la fonctionnalité pour l'utilisateur");
		}
	}
}
