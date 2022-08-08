package org.rudi.microservice.projekt.service.project;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;

public interface LinkedDatasetService {
	/**
	 * @param projectUuid UUID du projet
	 * @param status      Statut des jdd demandés
	 * @return Tous les jeux de données réutilisés par le projet
	 */
	List<LinkedDataset> getLinkedDatasets(UUID projectUuid, LinkedDatasetStatus status)
			throws AppServiceNotFoundException;

	/**
	 * @param projectUuid       UUID du projet
	 * @param linkedDatasetUuid UUID du linkedDataset
	 * @return Tous les jeux de données réutilisés par le projet
	 */
	LinkedDataset getLinkedDataset(UUID projectUuid, UUID linkedDatasetUuid) throws AppServiceNotFoundException;

	/**
	 * Ajoute un lien entre un projet et un jeu de données
	 *
	 * @param projectUuid   UUID du projet
	 * @param linkedDataset LinkedDataset fourni par le JSON
	 */
	LinkedDataset linkProjectToDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceNotFoundException, DataverseAPIException, AppServiceException;

	/**
	 * MAJ un linked dataset
	 *
	 * @param projectUuid   UUID du projet
	 * @param linkedDataset LinkedDataset fourni par le JSON à garder
	 */
	LinkedDataset updateLinkedDataset(UUID projectUuid, LinkedDataset linkedDataset)
			throws AppServiceNotFoundException, AppServiceException;

	/**
	 * Supprime un lien entre un projet et un jeu de données
	 *
	 * @param projectUuid       UUID du projet
	 * @param linkedDatasetUuid UUID du jeu de données
	 */
	void unlinkProjectToDataset(UUID projectUuid, UUID linkedDatasetUuid)
			throws AppServiceNotFoundException, AppServiceException;
}
