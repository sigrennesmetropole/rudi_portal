package org.rudi.microservice.projekt.service.project.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.ComputeIndicatorsSearchCriteria;
import org.rudi.microservice.projekt.core.bean.Indicators;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.service.exception.DataverseExternalServiceException;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.helper.ProjektAuthorisationHelper;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.service.project.ProjectService;
import org.rudi.microservice.projekt.service.project.impl.fields.CreateProjectFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.DeleteMediaFromProjectProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.DeleteProjectFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.UpdateMediaInProjectProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.UpdateProjectFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset.DeleteLinkedDatasetFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest.CreateNewDatasetRequestFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest.DeleteNewDatasetRequestFieldProcessor;
import org.rudi.microservice.projekt.service.project.impl.fields.newdatasetrequest.UpdateNewDatasetRequestFieldProcessor;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final List<CreateProjectFieldProcessor> createProjectProcessors;
	private final List<UpdateProjectFieldProcessor> updateProjectProcessors;
	private final List<DeleteProjectFieldProcessor> deleteProjectProcessors;

	private final List<DeleteMediaFromProjectProcessor> deleteMediaFromProjectProcessors;
	private final List<UpdateMediaInProjectProcessor> updateMediaInProjectProcessors;

	private final List<CreateNewDatasetRequestFieldProcessor> createNewDatasetRequestProcessors;
	private final List<UpdateNewDatasetRequestFieldProcessor> updateNewDatasetRequestProcessors;
	private final List<DeleteNewDatasetRequestFieldProcessor> deleteNewDatasetRequestProcessors;
	private final List<DeleteLinkedDatasetFieldProcessor> deleteLinkedDatasetProcessors;

	private final ProjectMapper projectMapper;
	private final ProjectDao projectDao;
	private final ProjectCustomDao projectCustomDao;

	private final MediaService mediaService;
	private final ResourceHelper resourceHelper;
	private final ResourceLoader resourceLoader;
	private final NewDatasetRequestMapper datasetRequestMapper;
	private final NewDatasetRequestDao datasetRequestDao;
	private final UtilContextHelper utilContextHelper;
	private final OrganizationHelper organizationHelper;
	private final ACLHelper aclHelper;
	private final MyInformationsHelper myInformationsHelper;
	private final ProjektAuthorisationHelper projektAuthorisationHelper;

	@Override
	@Transactional // readOnly = false
	public Project createProject(Project project) throws AppServiceException {
		ProjectEntity entity = projectMapper.dtoToEntity(project);
		projektAuthorisationHelper.checkRightsInitProject(entity);

		for (final CreateProjectFieldProcessor processor : createProjectProcessors) {
			processor.process(entity, null);
		}
		entity.setUuid(UUID.randomUUID());
		final LocalDateTime now = LocalDateTime.now();
		entity.setCreationDate(now);
		entity.setUpdatedDate(now);
		ProjectEntity savedEntity = projectDao.save(entity);
		Project savedProject = projectMapper.entityToDto(savedEntity);

		try {
			createProjectDataset(savedProject);
		} catch (DataverseExternalServiceException e) {
			projectDao.delete(savedEntity);
		}

		return savedProject;
	}

	private void createProjectDataset(Project project) throws DataverseExternalServiceException {
		// TODO RUDI-1301
	}

	@Override
	public Project getProject(UUID uuid) throws AppServiceNotFoundException {
		final var projectEntity = getRequiredProjectEntity(uuid);
		return projectMapper.entityToDto(projectEntity);
	}

	@Override
	public Page<Project> searchProjects(ProjectSearchCriteria searchCriteria, Pageable pageable) {
		return projectMapper.entitiesToDto(projectCustomDao.searchProjects(searchCriteria, pageable), pageable);
	}

	@Override
	@Transactional // readOnly = false
	public Project updateProject(Project projectToUpdate) throws AppServiceException {
		ProjectEntity entity = projectMapper.dtoToEntity(projectToUpdate);
		ProjectEntity existingProject = getRequiredProjectEntity(projectToUpdate.getUuid());

		projektAuthorisationHelper.checkRightsAdministerProject(existingProject);
		projektAuthorisationHelper.checkRightsAdministerProject(entity);

		for (final UpdateProjectFieldProcessor processor : updateProjectProcessors) {
			processor.process(entity, existingProject);
		}
		existingProject.setUpdatedDate(LocalDateTime.now());

		projectMapper.dtoToEntity(projectToUpdate, existingProject);
		final ProjectEntity savedEntity = projectDao.save(existingProject);
		val savedProject = projectMapper.entityToDto(savedEntity);

		updateProjectDataset(savedProject);

		return savedProject;
	}

	private void updateProjectDataset(Project project) throws DataverseExternalServiceException {
		// TODO RUDI-1301
	}

	@Override
	@Transactional // readOnly = false
	public void deleteProject(UUID uuid) throws AppServiceException {
		ProjectEntity existingProject = projectDao.findByUuid(uuid);
		if (existingProject == null) {
			return;
		}
		projektAuthorisationHelper.checkRightsAdministerProject(existingProject);
		for (final DeleteProjectFieldProcessor processor : deleteProjectProcessors) {
			processor.process(null, existingProject);
		}

		deleteProjectDataset(existingProject);

		projectDao.delete(existingProject);
	}

	private void deleteProjectDataset(ProjectEntity existingProject) throws AppServiceException {
		// suppression de tous les linkeddataset et des souscriptions associées si besoin
		if (CollectionUtils.isNotEmpty(existingProject.getLinkedDatasets())) {
			Iterator<LinkedDatasetEntity> it = existingProject.getLinkedDatasets().iterator();
			while (it.hasNext()) {
				LinkedDatasetEntity linkedDataset = it.next();
				for (final DeleteLinkedDatasetFieldProcessor processor : deleteLinkedDatasetProcessors) {
					try {
						processor.process(null, linkedDataset);
					} catch (APIManagerException e) {
						throw new AppServiceException(String.format(
								"Erreur de suppression du linkedDataset %s (dataset %s) dans le projet %s",
								linkedDataset.getUuid(), linkedDataset.getDatasetUuid(), existingProject.getUuid()), e);
					}
				}
				it.remove();
			}
			projectDao.save(existingProject);
		}
	}

	@Override
	public DocumentContent getMediaContent(UUID projectUuid, KindOfData kindOfData) throws AppServiceException {
		try {
			final var logo = mediaService.getMediaFor(MediaOrigin.PROJECT, projectUuid, KindOfData.LOGO);
			if (logo == null) {
				return getDefaultLogo();
			}
			return logo;
		} catch (DataverseAPIException e) {
			throw new AppServiceException(
					String.format("Erreur lors du téléchargement du %s du projet avec projectUuid = %s",
							kindOfData.getValue(), projectUuid),
					e);
		}
	}

	@Nonnull
	private DocumentContent getDefaultLogo() throws AppServiceException {
		try {
			final var resource = resourceLoader.getResource("classpath:medias/default-logo.png");
			return DocumentContent.fromResource(resource, false);
		} catch (IOException e) {
			throw new AppServiceException("Impossible de trouver le logo par défaut d'un projet", e);
		}
	}

	@Override
	public void uploadMedia(UUID projectUuid, KindOfData kindOfData, Resource media) throws AppServiceException {
		ProjectEntity existingProject = getRequiredProjectEntity(projectUuid);
		checkRightsAdministerProjectMedia(existingProject);

		// vérification des droits sur le projet
		for (final UpdateMediaInProjectProcessor processor : updateMediaInProjectProcessors) {
			processor.process(null, existingProject);
		}

		try {
			val tempFile = resourceHelper.copyResourceToTempFile(media);
			mediaService.setMediaFor(MediaOrigin.PROJECT, projectUuid, kindOfData, tempFile);
		} catch (final DataverseAPIException | IOException e) {
			throw new AppServiceException(String.format("Erreur lors de l'upload du %s du projet d'uuid %s",
					kindOfData.getValue(), projectUuid), e);
		}
	}

	@Override
	public void deleteMedia(UUID projectUuid, KindOfData kindOfData) throws AppServiceException {
		ProjectEntity existingProject = getRequiredProjectEntity(projectUuid);
		checkRightsAdministerProjectMedia(existingProject);

		// vérification des droits sur le projet
		for (final DeleteMediaFromProjectProcessor processor : deleteMediaFromProjectProcessors) {
			processor.process(null, existingProject);
		}

		try {
			mediaService.deleteMediaFor(MediaOrigin.PROJECT, projectUuid, kindOfData);
		} catch (final DataverseAPIException e) {
			throw new AppServiceException(String.format("Erreur lors de la suppression du %s du projet d'uuid %s",
					kindOfData.getValue(), projectUuid), e);
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
	@Transactional // readOnly = false
	public NewDatasetRequest createNewDatasetRequest(UUID projectUuid, NewDatasetRequest datasetRequest)
			throws AppServiceException {
		// Recuperer le projet pour vérifier son statut
		ProjectEntity associatedProject = getRequiredProjectEntity(projectUuid);

		projektAuthorisationHelper.checkRightsAdministerProjectDataset(associatedProject);

		// Vérification du statut du projet avant d'ajouter le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(associatedProject);

		NewDatasetRequestEntity entity = datasetRequestMapper.dtoToEntity(datasetRequest);

		entity.setUuid(UUID.randomUUID());
		entity.setCreationDate(LocalDateTime.now());
		entity.setUpdatedDate(entity.getCreationDate());

		for (CreateNewDatasetRequestFieldProcessor createNewDatasetRequestFieldProcessor : createNewDatasetRequestProcessors) {
			createNewDatasetRequestFieldProcessor.process(entity, null);
		}

		associatedProject.getDatasetRequests().add(entity);
		// Enregistrer les 2 entités (mettre tout ça dans le bloc try..catch)
		NewDatasetRequestEntity savedEntity = datasetRequestDao.save(entity);
		projectDao.save(associatedProject);
		return datasetRequestMapper.entityToDto(savedEntity);
	}

	@Override
	public List<NewDatasetRequest> getNewDatasetRequests(UUID projectUuid) throws AppServiceNotFoundException {
		return datasetRequestMapper.entitiesToDto(getRequiredProjectEntity(projectUuid).getDatasetRequests());
	}

	@Override
	public NewDatasetRequest getNewDatasetRequestByUuid(UUID projectUuid, UUID requestUuid)
			throws AppServiceNotFoundException {
		for (NewDatasetRequestEntity element : getRequiredProjectEntity(projectUuid).getDatasetRequests()) {
			if (element.getUuid().equals(requestUuid)) {
				return datasetRequestMapper.entityToDto(element);
			}
		}
		return null;
	}

	@Override
	@Transactional // readOnly = false
	public NewDatasetRequest updateNewDatasetRequest(UUID projectUuid, NewDatasetRequest newDatasetRequest)
			throws AppServiceException {
		ProjectEntity project = getRequiredProjectEntity(projectUuid);
		projektAuthorisationHelper.checkRightsAdministerProjectDataset(project);

		// Vérification du statut du projet avant de modifier le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(project);

		NewDatasetRequestEntity entity = datasetRequestMapper.dtoToEntity(newDatasetRequest);
		if (CollectionUtils.isNotEmpty(project.getDatasetRequests())) {
			for (NewDatasetRequestEntity newDatasetRequestEntity : project.getDatasetRequests()) {
				if (newDatasetRequestEntity.getUuid().equals(newDatasetRequest.getUuid())) {

					for (UpdateNewDatasetRequestFieldProcessor processor : updateNewDatasetRequestProcessors) {
						processor.process(entity, newDatasetRequestEntity);
					}

					datasetRequestMapper.dtoToEntity(newDatasetRequest, newDatasetRequestEntity);
					projectDao.save(project);
					return datasetRequestMapper.entityToDto(newDatasetRequestEntity);
				}
			}
		}

		throw new AppServiceNotFoundException(entity, null);
	}

	@Override
	@Transactional // readOnly = false
	public void deleteNewDatasetRequest(UUID projectUuid, UUID requestUuid) throws AppServiceException {
		ProjectEntity project = getRequiredProjectEntity(projectUuid);
		projektAuthorisationHelper.checkRightsAdministerProjectDataset(project);

		// Vérification du statut du projet avant de supprimer le lien sur le dataset
		projektAuthorisationHelper.checkStatusForProjectModification(project);

		Iterator<NewDatasetRequestEntity> iterator = project.getDatasetRequests().iterator();
		NewDatasetRequestEntity currentElement = null;
		while (iterator.hasNext()) {
			currentElement = iterator.next();
			if (currentElement.getUuid().equals(requestUuid)) {

				for (DeleteNewDatasetRequestFieldProcessor processor : deleteNewDatasetRequestProcessors) {
					processor.process(null, currentElement);
				}

				iterator.remove();
				datasetRequestDao.deleteById(currentElement.getId());
				projectDao.save(project);

				return;
			}
		}
	}

	@Override
	public Indicators computeIndicators(ComputeIndicatorsSearchCriteria searchCriteria) {
		return projectCustomDao.computeProjectInfos(searchCriteria);
	}

	@Override
	public Integer getNumberOfRequests(UUID projectUuid) throws AppServiceNotFoundException {
		getRequiredProjectEntity(projectUuid);
		return projectCustomDao.getNumberOfLinkedDatasets(projectUuid)
				+ projectCustomDao.getNumberOfNewRequests(projectUuid);
	}

	@Override
	public Page<Project> getMyProjects(ProjectSearchCriteria searchCriteria, Pageable pageable)
			throws GetOrganizationException {
		// get user uuid
		UUID userUuid = null;
		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser != null) {
			userUuid = aclHelper.getUserByLogin(authenticatedUser.getLogin()).getUuid();
		}
		// get user organization uuids
		if (userUuid == null) {
			return Page.empty();
		}

		List<UUID> organizationsUuid = organizationHelper.getMyOrganizationsUuids(userUuid);

		if (organizationsUuid != null) {
			// Add userUuid to this list before to searchProjects
			organizationsUuid.add(userUuid);
		}

		searchCriteria.setOwnerUuids(organizationsUuid);

		// call searchProjects
		return this.searchProjects(searchCriteria, pageable);
	}

	@Override
	public boolean isAuthenticatedUserProjectOwner(UUID projectUuid) throws GetOrganizationException, AppServiceUnauthorizedException, AppServiceNotFoundException {
		var uuids = myInformationsHelper.getMeAndMyOrganizationUuids();
		final var projectEntity = getRequiredProjectEntity(projectUuid);
		return CollectionUtils.containsAny(uuids, projectEntity.getOwnerUuid());
	}

	@Override
	public List<ProjectByOwner> getNumberOfProjectsPerOwners(ProjectSearchCriteria criteria) {
		return projectCustomDao.getNumberOfProjectsPerOwners(criteria.getOwnerUuids());
	}

	/**
	 * Définition de l'ouverture des droits la fonctionnalité de d'administration de media associé à un projet : Le projectowner ou un membre de
	 * l'organisation peut / L'administrateur peut (uniquement via Postman) / Un autre user ne peut pas
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	private void checkRightsAdministerProjectMedia(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, AppServiceUnauthorizedException, MissingParameterException {
		// pour le moment les droits d'accès à cette fonction sont les mêmes que la fonction de création de projet
		projektAuthorisationHelper.checkRightsInitProject(projectEntity);
	}
}
