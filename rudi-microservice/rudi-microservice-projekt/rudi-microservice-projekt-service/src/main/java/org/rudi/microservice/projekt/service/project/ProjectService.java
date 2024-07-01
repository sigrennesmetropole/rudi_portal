package org.rudi.microservice.projekt.service.project;

import java.util.List;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.microservice.projekt.core.bean.ComputeIndicatorsSearchCriteria;
import org.rudi.microservice.projekt.core.bean.Indicators;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 */
public interface ProjectService {

	/**
	 * Search for projects
	 *
	 * @return paged project list
	 */
	Page<Project> searchProjects(ProjectSearchCriteria searchCriteria, Pageable pageable);

	Project getProject(UUID uuid) throws AppServiceNotFoundException;

	/**
	 * Create a project
	 */
	Project createProject(Project project) throws AppServiceException;

	/**
	 * Update a project entity
	 */
	Project updateProject(Project project) throws AppServiceException;

	/**
	 * Delete a project entity
	 */
	void deleteProject(UUID uuid) throws AppServiceException;

	/**
	 * Télécharge le média du projet, dans le dataverse Rudi Media
	 *
	 * @param projectUuid l'uuid du projet
	 * @param kindOfData  le type de média
	 * @return le média téléchargé
	 * @throws AppServiceException En cas d'erreur avec le service de téléchargement
	 */
	DocumentContent getMediaContent(UUID projectUuid, KindOfData kindOfData) throws AppServiceException;

	/**
	 * Uploade le média du projet, dans le dataverse Rudi Media
	 *
	 * @param projectUuid l'uuid du projet
	 * @param kindOfData  le type de média
	 * @param documentContent       le média à remplacer dans Dataverse
	 * @throws AppServiceException En cas d'erreur avec le service d'upload
	 */
	void uploadMedia(UUID projectUuid, KindOfData kindOfData, DocumentContent documentContent) throws AppServiceException;

	/**
	 * Supprime le média associé à ce projet
	 *
	 * @param projectUuid l'uuid du projet
	 * @param kindOfData  type du média à supprimer
	 */
	void deleteMedia(UUID projectUuid, KindOfData kindOfData) throws AppServiceException;

	/**
	 * Ajoute une demande de nouveau jdd pour un projet
	 *
	 * @param projectUuid    UUID du projet
	 * @param datasetRequest La requête pour les nouvelles données
	 */
	NewDatasetRequest createNewDatasetRequest(UUID projectUuid, NewDatasetRequest datasetRequest)
			throws AppServiceException;

	/**
	 * Recupère la liste des demandes de nouveau jdd associé à un projet
	 *
	 * @param projectUuid UUID du projet
	 */
	List<NewDatasetRequest> getNewDatasetRequests(UUID projectUuid) throws AppServiceNotFoundException;

	/**
	 * Modifier une demande de nouveau jdd pour un projet
	 *
	 * @param projectUuid       UUID du projet
	 * @param newDatasetRequest La demande de jdd modifiée
	 */
	NewDatasetRequest updateNewDatasetRequest(UUID projectUuid, NewDatasetRequest newDatasetRequest)
			throws AppServiceException;

	/**
	 * Recupère une demande de jdd pour un projet donné et un UUID de requête donnée
	 *
	 * @param projectUuid UUID du projet
	 * @param requestUuid UUID de la requête
	 * @return
	 */
	NewDatasetRequest getNewDatasetRequestByUuid(UUID projectUuid, UUID requestUuid) throws AppServiceNotFoundException;

	/**
	 * Supprime une demande de jdd pour un projet donné et un UUID de requête donnée
	 *
	 * @param projectUuid UUID du projet
	 * @param requestUuid UUID de la requête
	 * @return
	 */
	void deleteNewDatasetRequest(UUID projectUuid, UUID requestUuid)
			throws AppServiceException;

	/**
	 * @param searchCriteria {projet dont on cherche les autres demandes, producteur non concerné par ses demandes (optionnel)}
	 */
	Indicators computeIndicators(ComputeIndicatorsSearchCriteria searchCriteria);

	/**
	 * @param projectUuid
	 * @return nombre de jdds rattachés au projet (restreint + ouvert + nouvelle demande)
	 */
	Integer getNumberOfRequests(UUID projectUuid) throws AppServiceNotFoundException;


	/**
	 * Recherche mes projets et ceux de mon organisation
	 *
	 * @param searchCriteria critère de filtrage
	 * @param pageable       info de pagination
	 * @return Une page de project (limit max sinon 10)
	 * @throws GetOrganizationException si erreur
	 */
	Page<Project> getMyProjects(ProjectSearchCriteria searchCriteria, Pageable pageable) throws GetOrganizationException;

	/**
	 * Détermine si l'utilisateur connecté est owner du projet passé en paramètre.
	 *
	 * @param projectUuid UUID du projet
	 * @return true si l'authenticatedUser est owner du projet, false sinon
	 * @throws GetOrganizationException exception lors de la récupération de l'organization liée au projet
	 * @throws AppServiceUnauthorizedException erreur lors de l'identification de l'utilisateur connecté
	 * @throws AppServiceNotFoundException erreur lors de la récupération du projet
	 */
	boolean isAuthenticatedUserProjectOwner(UUID projectUuid)  throws GetOrganizationException, AppServiceUnauthorizedException, AppServiceNotFoundException;

	List<ProjectByOwner> getNumberOfProjectsPerOwners(ProjectSearchCriteria criteria);
}
