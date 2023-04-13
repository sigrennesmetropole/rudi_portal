package org.rudi.microservice.konsult.service.metadata;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacets;
import org.rudi.facet.kaccess.bean.MetadataList;

public interface MetadataService {

	/**
	 * Recherche sur les métadonnées
	 *
	 * @param datasetSearchCriteria critères de recherches
	 * @return MetadataList
	 * @throws DataverseAPIException Erreur lors de la recherche dans le dataverse
	 */
	MetadataList searchMetadatas(DatasetSearchCriteria datasetSearchCriteria) throws DataverseAPIException;

	/**
	 * Recherche des informations sur les paramètres des métadonnées
	 *
	 * @param facets paramètres des métadonnnées pour lesquelles on récupère la liste des valeurs possibles
	 * @return MetadataFacets
	 * @throws DataverseAPIException Erreur lors de la recherche dans le dataverse
	 */
	MetadataFacets searchMetadatasFacets(List<String> facets) throws DataverseAPIException;

	/**
	 * Récupération des métadonnées d'un jeu de données
	 *
	 * @param globalId Identifiant du jeu de données
	 * @return Metadata
	 * @throws AppServiceException Erreur lors de la récupération des métadonnées
	 */
	@Nonnull
	Metadata getMetadataById(UUID globalId) throws AppServiceException;

	/**
	 * Récupérer les informations d'un média dans les métadonnées
	 *
	 * @param globalId identifiant du jeu de données
	 * @param mediaId  identifiant du média
	 * @return DocumentContent
	 * @throws AppServiceException Erreur lors de la récupération des données
	 */
	DocumentContent downloadMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException, IOException;

	/**
	 * @return true si l'utilisateur connecté ou une de ses organisations a souscrit à tous les médias (API du point de vue Developer Portal)
	 * du jeu de données
	 */
	boolean hasSubscribeToSelfdataDataset(UUID globalId) throws AppServiceException, APIManagerException;

	/**
	 * @return true si l'utilisateur connecté ou une de ses organisations a souscrit à tous les médias (API du point de vue Developer Portal)
	 * du jeu de données
	 */
	boolean hasSubscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid) throws AppServiceException, APIManagerException;

	/**
	 * Souscrit à un jeu de données, ce qui signifie que l'utilisateur connecté va souscrire à tous les médias
	 * (API du point de vue Developer Portal).
	 */
	void subscribeToSelfdataDataset(UUID globalId) throws APIManagerException, AppServiceException;

	/**
	 * Souscrit à un jeu de données restreint, ce qui signifie que l'utilisateur connecté va souscrire à tous les médias
	 * (API du point de vue Developer Portal).
	 *
	 * @param globalId          Id du JDD (restreint à priori)
	 * @param linkedDatasetUuid (
	 * @throws APIManagerException
	 * @throws AppServiceException
	 */
	void subscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid) throws APIManagerException, AppServiceException;

	/**
	 * Permet de savoir si l'utilisateur connecté a souscrit à l'api
	 *
	 * @param globalId identifiant des métadonnées
	 * @param mediaId  identifiant du média
	 * @return true si l'utilisateur connecté a souscrit à l'api
	 * @throws AppServiceException Pas de souscription à l'API
	 */
	Boolean hasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws AppServiceException;

	/**
	 * @return liste des JDD partageant le même thème (cf RUDI-292)
	 */
	List<Metadata> getMetadatasWithSameTheme(UUID globalId, Integer limit) throws AppServiceException;

	/**
	 * @param globalId du dataset
	 * @return nombre de dataset sur le même thème
	 */
	Integer getNumberOfDatasetsOnTheSameTheme(UUID globalId) throws AppServiceException;
}
