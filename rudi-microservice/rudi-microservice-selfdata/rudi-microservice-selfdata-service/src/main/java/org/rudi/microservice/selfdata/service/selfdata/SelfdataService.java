package org.rudi.microservice.selfdata.service.selfdata;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.microservice.selfdata.core.bean.BarChartData;
import org.rudi.microservice.selfdata.core.bean.GenericDataObject;
import org.rudi.microservice.selfdata.core.bean.MatchingData;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataDatasetList;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataInformationRequestList;
import org.rudi.microservice.selfdata.core.bean.SelfdataDatasetSearchCriteria;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequestSearchCriteria;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 */
public interface SelfdataService {

	/**
	 * Create a selfdata entity
	 */
	SelfdataInformationRequest createSelfdataInformationRequest(SelfdataInformationRequest selfdataInformationRequest);

	/**
	 * Update a selfdata entity
	 */
	SelfdataInformationRequest updateSelfdataInformationRequest(SelfdataInformationRequest selfdataInformationRequest);

	/**
	 * Delete a selfdata entity
	 */
	void deleteSelfdataInformationRequest(UUID uuid);

	/**
	 * Recherche des JDDs selfdata, avec la dernnière demande de l'utilisateur connecté le cas échéant
	 *
	 * @param criteria critères de recherche
	 * @param pageable critères de pagination/tri
	 * @return Une liste dess JDD selfdata
	 * @throws AppServiceException une erreur côté dataverse ou côté métier RUDI
	 */
	PagedSelfdataDatasetList searchSelfdataDatasets(SelfdataDatasetSearchCriteria criteria, Pageable pageable)
			throws AppServiceException;

	/**
	 * Recherche des demandes sur un JDD selfdata pour l'utilisateur connecté
	 *
	 * @param criteria critères de recherches pour affiner la recherche
	 * @param pageable critères de pagination/tri
	 * @return une demande page de demandes selfdata
	 * @throws AppServiceException 401 si appel en étant pas authentifié
	 */
	PagedSelfdataInformationRequestList searchMySelfdataInformationRequests(
			SelfdataInformationRequestSearchCriteria criteria, Pageable pageable) throws AppServiceException;

	/**
	 * Récupération des données de l'utilisateur connecté pour le JDD d'uuid fourni au format GDATA
	 *
	 * @param datasetUuid l'UUID du JDD contenant les données
	 * @return des données au format GDATA
	 */
	GenericDataObject getGdataData(UUID datasetUuid) throws AppServiceException;

	/**
	 * Récupération des données de l'utilisateur connecté pour le JDD d'uuid fourni au format TPBC
	 *
	 * @param datasetUuid l'UUID du JDD contenant les données
	 * @param minDate     la date de début de période de restitution
	 * @param maxDate     la date de fin de période de restitution
	 * @return des données au format TPBC
	 */
	BarChartData getTpbcData(UUID datasetUuid, OffsetDateTime minDate, OffsetDateTime maxDate)
			throws AppServiceException;

	List<MatchingData> getMySelfdataInformationRequestMatchingData(UUID datasetUUID)
			throws AppServiceException, InvalidDataException, FormDefinitionException;

	/**
	 * Rechiffrement ou chiffrement (dans le cas d'un migration) des données pivots
	 * 
	 * @param previousAliasKey c'est le nom de la clé initial (dans le cas d'un rechiffrement)
	 */
	void recryptSelfdataInformationRequest(String previousAliasKey);
}
