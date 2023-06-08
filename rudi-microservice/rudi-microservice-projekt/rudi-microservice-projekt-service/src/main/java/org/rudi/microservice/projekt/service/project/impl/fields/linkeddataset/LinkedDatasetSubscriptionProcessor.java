package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

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
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@Order(2)
@RequiredArgsConstructor
public class LinkedDatasetSubscriptionProcessor implements DeleteLinkedDatasetFieldProcessor {
	private final ACLHelper aclHelper;
	private final ApplicationService applicationService;
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final UtilPageable utilPageable;
	private final ProjectCustomDao projectCustomDao;

	@Override
	public void process(@Nullable LinkedDatasetEntity linkedDataset, @Nullable LinkedDatasetEntity existingLinkedDataset) throws AppServiceException, APIManagerException {
		val project = getProject(existingLinkedDataset);
		val ownerName = getSubscriptionOwnerName(project);
		// Recup de toutes les demandes de ce owner pour s'assurer qu'une autre demande ne donne pas accès à ce JDD
		val ownerLinkedDatasets = new ArrayList<LinkedDatasetEntity>();
		getOwnerAllRequests(List.of(project.getOwnerUuid()), 0, ownerLinkedDatasets);
		val datasetUuid = existingLinkedDataset.getDatasetUuid();
		if (hasAccessThroughAnotherRequest(ownerLinkedDatasets, datasetUuid)) {
			return; // On ne désouscrit pas au JDD alors
		} else { // Aucun autre linkedDataset ne donne accès au JDD => Désouscription
			applicationService.deleteUserSubscriptionsForDatasetAPIs(ownerName, datasetUuid);
		}
	}

	/**
	 * Utilise la méthode de DAO de recherche d'une page de LinkedDatasets pour retourner tous les linkedDataset de notre user
	 *
	 * @param ownerList               liste de user dont on peut chercher les éléments (dans notre cas liste à 1 élément)
	 * @param offset                  indice à partir duquel remonter des infos
	 * @param linkedDatasetEntityList conteneur de tous les éléments remontés dans chaque page
	 */
	private void getOwnerAllRequests(List<UUID> ownerList, int offset, List<LinkedDatasetEntity> linkedDatasetEntityList) {
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
	private boolean hasAccessThroughAnotherRequest(List<LinkedDatasetEntity> linkedDatasetEntityList, UUID datasetUuid) {
		return linkedDatasetEntityList.stream().filter(linkedDatasetEntity -> linkedDatasetEntity.getDatasetUuid().equals(datasetUuid) && linkedDatasetEntity.getLinkedDatasetStatus() == LinkedDatasetStatus.VALIDATED).count() > 1;
	}

	/**
	 * Retourne le projet pour lequel cette demande avait été faite
	 *
	 * @param existingLinkedDataset la demande
	 * @return
	 * @throws AppServiceException
	 */
	private ProjectEntity getProject(@Nullable LinkedDatasetEntity existingLinkedDataset) throws AppServiceException {
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
	private String getSubscriptionOwnerName(ProjectEntity projectEntity) throws AppServiceException {
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
