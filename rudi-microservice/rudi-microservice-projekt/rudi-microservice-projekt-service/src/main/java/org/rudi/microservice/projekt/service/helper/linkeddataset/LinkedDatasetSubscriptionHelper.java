package org.rudi.microservice.projekt.service.helper.linkeddataset;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkedDatasetSubscriptionHelper {
	private final ACLHelper aclHelper;
	private final ApplicationService applicationService;
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final LinkedDatasetDao linkedDatasetDao;
	private final UtilPageable utilPageable;
	private final ProjectCustomDao projectCustomDao;

	@Transactional // true
	public void cleanLinkedDatasetExpired(LinkedDatasetEntity existingLinkedDataset)
			throws AppServiceException, APIManagerException {
		val project = getProject(existingLinkedDataset);
		val ownerName = getSubscriptionOwnerName(project);
		// Recup de toutes les demandes de ce owner pour s'assurer qu'une autre demande ne donne pas accès à ce JDD
		val ownerLinkedDatasets = new ArrayList<LinkedDatasetEntity>();
		getOwnerAllRequests(List.of(project.getOwnerUuid()), 0, ownerLinkedDatasets);
		val datasetUuid = existingLinkedDataset.getDatasetUuid();
		boolean hasNotAccess = !hasAccessThroughAnotherRequest(ownerLinkedDatasets, datasetUuid);
		if (hasNotAccess) { // Aucun autre linkedDataset ne donne accès au JDD => Désouscription
			blockSubscription(existingLinkedDataset, ownerName);
		} else { // Il existe un autre linkedDataset qui donne accès au JDD => Archivage du linkedDataset actuel expiré mais sans désouscription
			archivedLinkedDataset(existingLinkedDataset);
		}
	}

	@Transactional // true
	public void handleUnlinkLinkedDataset(LinkedDatasetEntity existingLinkedDataset)
			throws AppServiceException, APIManagerException {
		val project = getProject(existingLinkedDataset);
		val ownerName = getSubscriptionOwnerName(project);
		// Recup de toutes les demandes de ce owner pour s'assurer qu'une autre demande ne donne pas accès à ce JDD
		val ownerLinkedDatasets = new ArrayList<LinkedDatasetEntity>();
		getOwnerAllRequests(List.of(project.getOwnerUuid()), 0, ownerLinkedDatasets);
		val datasetUuid = existingLinkedDataset.getDatasetUuid();
		boolean hasNotAccess = !hasAccessThroughAnotherRequest(ownerLinkedDatasets, datasetUuid);
		if (hasNotAccess) { // Aucun autre linkedDataset ne donne accès au JDD => Désouscription
			deleteSubscription(existingLinkedDataset, ownerName, datasetUuid);
		} else { // Il existe un autre linkedDataset qui donne accès au JDD => Archivage du linkedDataset actuel expiré mais sans désouscription
			archivedLinkedDataset(existingLinkedDataset);
		}
	}

	/**
	 *
	 * @param existingLinkedDataset link à archiver
	 */
	protected void archivedLinkedDataset(LinkedDatasetEntity existingLinkedDataset) {
		existingLinkedDataset.setLinkedDatasetStatus(LinkedDatasetStatus.ARCHIVED);
		linkedDatasetDao.save(existingLinkedDataset);
	}

	/**
	 *
	 * @param existingLinkedDataset link à archiver
	 * @param ownerName             nom auquel la souscription au jdd a été faite
	 * @param datasetUuid           uuid du jdd
	 * @throws APIManagerException Execption WSO2
	 */
	protected void deleteSubscription(LinkedDatasetEntity existingLinkedDataset, String ownerName, UUID datasetUuid)
			throws APIManagerException {
		applicationService.deleteUserSubscriptionsForDatasetAPIs(ownerName, datasetUuid);
		archivedLinkedDataset(existingLinkedDataset);
	}

	/**
	 *
	 * @param existingLinkedDataset link à archiver
	 * @param ownerName             nom auquel la souscription au jdd a été faite
	 * @throws APIManagerException Execption WSO2
	 */
	protected void blockSubscription(LinkedDatasetEntity existingLinkedDataset, String ownerName)
			throws APIManagerException {
		applicationService.blockUserSubscriptionsForDatasetAPIs(ownerName, existingLinkedDataset.getDatasetUuid());
		archivedLinkedDataset(existingLinkedDataset);
	}

	/**
	 * Utilise la méthode de DAO de recherche d'une page de LinkedDatasets pour retourner tous les linkedDataset de notre user
	 *
	 * @param ownerList               liste de user dont on peut chercher les éléments (dans notre cas liste à 1 élément)
	 * @param offset                  indice à partir duquel remonter des infos
	 * @param linkedDatasetEntityList conteneur de tous les éléments remontés dans chaque page
	 */
	public void getOwnerAllRequests(List<UUID> ownerList, int offset,
			List<LinkedDatasetEntity> linkedDatasetEntityList) {
		val pageable = utilPageable.getPageable(offset, null, null);
		val searchCriteria = new LinkedDatasetSearchCriteria().projectOwnerUuids(ownerList);
		val pageResult = linkedDatasetCustomDao.searchLinkedDatasets(searchCriteria, pageable);
		linkedDatasetEntityList.addAll(pageResult.getContent());
		if (linkedDatasetEntityList.size() < pageResult.getTotalElements()) {
			getOwnerAllRequests(ownerList, offset + pageResult.getNumberOfElements(), linkedDatasetEntityList);
		}
	}

	/**
	 * Permet de savoir si le owner a accès à ce JDD via une autre demande
	 */
	public boolean hasAccessThroughAnotherRequest(List<LinkedDatasetEntity> linkedDatasetEntityList, UUID datasetUuid) {
		return linkedDatasetEntityList.stream()
				.filter(linkedDatasetEntity -> linkedDatasetEntity.getDatasetUuid().equals(datasetUuid)
						&& linkedDatasetEntity.getLinkedDatasetStatus() == LinkedDatasetStatus.VALIDATED)
				.count() > 1;
	}

	/**
	 * Retourne le projet pour lequel cette demande avait été faite
	 *
	 * @param existingLinkedDataset la demande
	 * @return le projet qui a initié cette demande d'accès
	 * @throws AppServiceException
	 */
	public ProjectEntity getProject(@Nullable LinkedDatasetEntity existingLinkedDataset) throws AppServiceException {
		if (existingLinkedDataset == null) {
			throw new AppServiceException("Suppression impossible. L'élément recherché n'a pas été trouvé");
		}
		return projectCustomDao.findProjectByLinkedDatasetUuid(existingLinkedDataset.getUuid());
	}

	/**
	 * Recupère du linkedDataset qu'on veut supprimer, l'owner ayant associée cette demande au projet
	 *
	 * @param projectEntity projet ayant donné lieu à la demande
	 * @return Login du project owner qui le linkedDataset owner
	 * @throws AppServiceException
	 */
	public String getSubscriptionOwnerName(ProjectEntity projectEntity) throws AppServiceException {
		val ownerUuid = projectEntity.getOwnerUuid();
		val ownerType = projectEntity.getOwnerType();
		if (ownerType == OwnerType.ORGANIZATION) {
			return ownerUuid.toString();
		}
		val user = aclHelper.getUserByUUID(ownerUuid);
		if (user == null) {
			throw new AppServiceNotFoundException(new EmptyResultDataAccessException(1));
		}
		return user.getLogin();
	}
}
