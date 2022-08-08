package org.rudi.facet.kaccess.service.dataset;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataListFacets;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface DatasetService {

	/**
	 * Récupération des métadonnées d'un jeu de données
	 *
	 * @param doi identifiant du jeu de données sous forme DOI
	 * @return jeu de données + métadonnées
	 * @throws DataverseAPIException Erreur lors de la récupération
	 */
	Metadata getDataset(String doi) throws DataverseAPIException;

	/**
	 * Récupération des métadonnées d'un jeu de données
	 *
	 * @param globalId identifiant du jeu de données sous forme
	 * @return jeu de données + métadonnées
	 * @throws DataverseAPIException Erreur lors de la récupération
	 */
	@Nonnull
	Metadata getDataset(UUID globalId) throws DataverseAPIException;

	/**
	 * Création d'un jeu de données
	 *
	 * @param metadata paramètres du jeu de données
	 * @return identifiant du jeu de données crée sous forme DOI
	 * @throws DataverseAPIException Erreur lors de la création
	 */
	String createDataset(Metadata metadata) throws DataverseAPIException;

	/**
	 * Mise à jour des métadonnées d'un jeu de données
	 *
	 * @param metadata paramètres du jeu de donnée
	 * @return Jeu de données modifié
	 * @throws DataverseAPIException Erreur lors de la mise à jour
	 */
	Metadata updateDataset(Metadata metadata) throws DataverseAPIException;

	/**
	 * Archivage d'un jeu de données. Le jeu de donnée est déplacé vers le dataverse archive
	 *
	 * @param doi identifiant du jeu de donnée sous forme DOI
	 * @throws DataverseAPIException Erreur lors de l'archivage
	 */
	String archiveDataset(String doi) throws DataverseAPIException;

	/**
	 * Recherche de jeu de données avec éventuellement les facets
	 *
	 * @param datasetSearchCriteria critères de recherche
	 * @param facets                propriétés pour lesquelles on retourne la liste des valeurs possibles (cf {@link FieldSpec#getFacet()}}
	 * @return MetadataListFacets
	 * @throws DataverseAPIException Erreur lors de la recherche
	 */
	MetadataListFacets searchDatasets(DatasetSearchCriteria datasetSearchCriteria, List<String> facets)
			throws DataverseAPIException;

	/**
	 * Suppression unitaire d'un jeu de données.
	 *
	 * @param doi Identifiant du jeu de données à supprimer sous forme de DOI
	 * @throws DataverseAPIException Exception lors de la suppression du jeu de données
	 */
	void deleteDataset(String doi) throws DataverseAPIException;

	/**
	 * Suppression d'un jeu de données ou des jeux de données, si plusieurs portent le même globalId
	 *
	 * @param globalId Identifiant du jeu de données à supprimer
	 * @throws DataverseAPIException Exception lors de la suppression du jeu de données
	 */
	void deleteDataset(UUID globalId) throws DataverseAPIException;

	/**
	 * @param datasetSearchCriteria critères de recherche de jeu de données
	 * @return true si un jeu de données correspondant aux critères existe dans Dataverse
	 */
	boolean datasetExists(DatasetSearchCriteria datasetSearchCriteria) throws DataverseAPIException;

	/**
	 * @param globalId Identifiant du jeu de données recherché
	 * @return true si un jeu de données existe avec ce globalId dans Dataverse
	 */
	boolean datasetExists(UUID globalId) throws DataverseAPIException;

	/**
	 * @param doi Identifiant du jeu de données recherché dans Dataverse
	 * @return true si un jeu de données existe avec ce globalId dans Dataverse
	 */
	boolean datasetExists(String doi) throws DataverseAPIException;
}
