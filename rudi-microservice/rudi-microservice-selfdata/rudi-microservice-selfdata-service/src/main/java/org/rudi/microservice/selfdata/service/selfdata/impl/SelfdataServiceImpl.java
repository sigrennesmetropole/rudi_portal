package org.rudi.microservice.selfdata.service.selfdata.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.registration.RegistrationHelper;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.selfdata.core.bean.BarChartData;
import org.rudi.microservice.selfdata.core.bean.FieldType;
import org.rudi.microservice.selfdata.core.bean.GenericDataObject;
import org.rudi.microservice.selfdata.core.bean.MatchingData;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataDatasetList;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataInformationRequestList;
import org.rudi.microservice.selfdata.core.bean.SelfdataDataset;
import org.rudi.microservice.selfdata.core.bean.SelfdataDatasetSearchCriteria;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequestSearchCriteria;
import org.rudi.microservice.selfdata.service.exception.UserNotFoundException;
import org.rudi.microservice.selfdata.service.helper.selfdatadataset.SelfdataApiParameters;
import org.rudi.microservice.selfdata.service.helper.selfdatadataset.SelfdataDatasetApisHelper;
import org.rudi.microservice.selfdata.service.helper.selfdatadataset.SelfdataDatasetHelper;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.rudi.microservice.selfdata.service.mapper.SelfdataInformationRequestMapper;
import org.rudi.microservice.selfdata.service.selfdata.SelfdataService;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomSearchCriteria;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.SelfdataDataset.SelfdataDatasetEntity;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author FNI18300
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SelfdataServiceImpl implements SelfdataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SelfdataServiceImpl.class);
	private final ApplicationContext applicationContext;
	private final SelfdataInformationRequestDao selfdataInformationRequestDao;
	private final SelfdataInformationRequestCustomDao selfdataInformationRequestCustomDao;
	private final SelfdataInformationRequestMapper selfdataInformationRequestMapper;
	private final SelfdataDatasetHelper selfdataDatasetHelper;
	private final UtilContextHelper utilContextHelper;
	private final DatasetService datasetService;
	private final SelfdataDatasetApisHelper selfdataDatasetApisHelper;
	private final RegistrationHelper registrationHelper;
	private final SelfdataMatchingDataHelper selfdataMatchingDataHelper;

	@Value("${rudi.selfdata.recrypt.pageSize:20}")
	private int recryptPageSize;

	@Override
	@Transactional // readOnly = false
	public SelfdataInformationRequest createSelfdataInformationRequest(
			SelfdataInformationRequest selfdataInformationRequest) {
		final var entity = selfdataInformationRequestMapper.dtoToEntity(selfdataInformationRequest);
		entity.setUuid(UUID.randomUUID());
		validEntity(entity);
		final var savedEntity = selfdataInformationRequestDao.save(entity);
		return selfdataInformationRequestMapper.entityToDto(savedEntity);
	}

	@Override
	@Transactional // readOnly = false
	public SelfdataInformationRequest updateSelfdataInformationRequest(
			SelfdataInformationRequest selfdataInformationRequest) {
		if (selfdataInformationRequest.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
		SelfdataInformationRequestEntity entity = selfdataInformationRequestDao
				.findByUuid(selfdataInformationRequest.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + selfdataInformationRequest.getUuid());
		}
		selfdataInformationRequestMapper.dtoToEntity(selfdataInformationRequest, entity);
		validEntity(entity);
		selfdataInformationRequestDao.save(entity);
		return selfdataInformationRequestMapper.entityToDto(entity);
	}

	@Override
	@Transactional // readOnly = false
	public void deleteSelfdataInformationRequest(UUID uuid) {
		SelfdataInformationRequestEntity entity = selfdataInformationRequestDao.findByUuid(uuid);
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + uuid);
		}
		selfdataInformationRequestDao.delete(entity);
	}

	@Override
	public PagedSelfdataDatasetList searchSelfdataDatasets(SelfdataDatasetSearchCriteria criteria, Pageable pageable)
			throws AppServiceException {

		// Conversion du tri envoyé en paramètre en un tri applicable
		selfdataDatasetHelper.mapOrderToAppliedOrder(criteria);

		// Application de l'algorithme en fonction du critère de tri
		if (selfdataDatasetHelper.isSearchFromDatasets(criteria)) {
			return searchSelfdataDatasetsFromDatasets(criteria);
		} else {
			return searchSelfdataDatasetFromRequests(criteria, pageable);
		}
	}

	@Override
	public PagedSelfdataInformationRequestList searchMySelfdataInformationRequests(
			SelfdataInformationRequestSearchCriteria criteria, Pageable pageable) throws AppServiceException {

		AuthenticatedUser user = utilContextHelper.getAuthenticatedUser();
		if (user == null || user.getLogin() == null) {
			throw new AppServiceUnauthorizedException(
					"Impossible de récupérer les demandes de l'utilisateur connecté sans être authentifié");
		}

		SelfdataInformationRequestCustomSearchCriteria customCriteria = new SelfdataInformationRequestCustomSearchCriteria();
		customCriteria.setDatasetUuid(criteria.getDatasetUuid());
		customCriteria.setLogin(user.getLogin());
		if (CollectionUtils.isNotEmpty(criteria.getStatus())) {
			customCriteria.setStatus(criteria.getStatus());
		}
		Page<SelfdataInformationRequestEntity> page = selfdataInformationRequestCustomDao
				.searchSelfdataInformationRequests(customCriteria, pageable);

		List<SelfdataInformationRequest> requests = selfdataInformationRequestMapper.entitiesToDto(page.getContent());
		return new PagedSelfdataInformationRequestList().total(page.getTotalElements()).elements(requests);
	}

	@Override
	public GenericDataObject getGdataData(UUID datasetUuid) throws AppServiceException, GetClientRegistrationException {

		SelfdataApiParameters parameters = getApiParameters(datasetUuid);
		return selfdataDatasetApisHelper.getGdataData(parameters);
	}

	@Override
	public BarChartData getTpbcData(UUID datasetUuid, OffsetDateTime minDate, OffsetDateTime maxDate)
			throws AppServiceException, GetClientRegistrationException {

		SelfdataApiParameters parameters = getApiParameters(datasetUuid);
		parameters.setMinDate(minDate);
		parameters.setMaxDate(maxDate);
		return selfdataDatasetApisHelper.getTpbcData(parameters);
	}

	/**
	 * Récupération des pré-requis pour effectuer des appels vers WSO2 en mode selfdata
	 *
	 * @param datasetUuid l'UUID du JDD concerné par l'appel selfdata
	 * @return les éléments requis pour faire l'appel WSO2
	 * @throws AppServiceException si problème de récupération des paramètres
	 */
	private SelfdataApiParameters getApiParameters(UUID datasetUuid) throws AppServiceException {

		SelfdataApiParameters parameters = new SelfdataApiParameters();

		// Récupération du JDD
		Metadata metadata;
		try {
			metadata = datasetService.getDataset(datasetUuid);
		} catch (DataverseAPIException e) {
			throw new AppServiceNotFoundException((new EmptyResultDataAccessException(e.getMessage(), 1)));
		}

		AuthenticatedUser user = utilContextHelper.getAuthenticatedUser();
		if (user == null || StringUtils.isBlank(user.getLogin())) {
			throw new AppServiceUnauthorizedException("Impossible de récupérer les données TPBC sans être authentifié");
		}

		Application application;
		try {
			application = registrationHelper.getOrCreateApplicationForUser(user.getLogin());
		} catch (ApplicationOperationException e) {
			throw new AppServiceException(
					"Erreur lors de la récupération de l'application WSO2 de l'utilisateur connecté", e);
		}

		parameters.setMetadata(metadata);
		parameters.setUser(user);
		parameters.setApplication(application);
		return parameters;
	}

	/**
	 * Recherche des selfdata datasets en partant d'une recherche de JDDs
	 *
	 * @param criteria les critères de pagination et tri sur la recherche
	 * @return Une page de résultats
	 * @throws AppServiceException levée en cas d'exception avec dataverse
	 */
	private PagedSelfdataDatasetList searchSelfdataDatasetsFromDatasets(SelfdataDatasetSearchCriteria criteria)
			throws AppServiceException {

		// Init de la page renvoyée avec un statut nul
		PagedSelfdataDatasetList page = new PagedSelfdataDatasetList().total(0L).elements(new ArrayList<>());

		// Recherche des JDDs dans dataverse
		MetadataList datasetsContainer = selfdataDatasetHelper.searchSelfdataMetadata(criteria);

		// Si aucun résultat renvoi page vide
		if (datasetsContainer == null || CollectionUtils.isEmpty(datasetsContainer.getItems())) {
			return page;
		}

		// Récupération du total des éléments
		page.setTotal(datasetsContainer.getTotal());
		List<Metadata> datasets = datasetsContainer.getItems();

		// Je recherche les demandes correspondants aux JDDs trouvées
		List<SelfdataDatasetEntity> requests = selfdataDatasetHelper.searchCorrespondingRequests(datasets);
		Map<UUID, SelfdataDatasetEntity> lastRequestsByDatasetUuid = requests.stream()
				.collect(Collectors.toMap(SelfdataDatasetEntity::getDatasetUuid, Function.identity()));

		// Construction des selfdata-dataset à partir des JDDS et de la map des demandes
		for (Metadata dataset : datasets) {
			SelfdataDatasetEntity request = lastRequestsByDatasetUuid.get(dataset.getGlobalId());
			SelfdataDataset selfdataDataset = selfdataDatasetHelper.buildSelfdataDataset(dataset, request);
			page.getElements().add(selfdataDataset);
		}

		return page;
	}

	/**
	 * Recherche des selfdata datasets à partir des demandes
	 *
	 * @param criteria critère de tri et pagination de la recherche
	 * @param pageable critère de pagination
	 * @return une page de selfdata dataset
	 * @throws AppServiceException levée en cas d'exception avec dataverse
	 */
	private PagedSelfdataDatasetList searchSelfdataDatasetFromRequests(SelfdataDatasetSearchCriteria criteria,
			Pageable pageable) throws AppServiceException {

		// Init de la page
		PagedSelfdataDatasetList page = new PagedSelfdataDatasetList().total(0L).elements(new ArrayList<>());

		// Récupération des demandes
		List<SelfdataDatasetEntity> requests = selfdataDatasetHelper.searchMySelfDatasets(pageable);

		// Si il n'y a aucune demande pour l'utilisateur connecté on fait une recherche classique par dataset
		if (CollectionUtils.isEmpty(requests)) {
			String orderPrefix = selfdataDatasetHelper.isOrderAscending(criteria) ? "" : "-";
			criteria.setOrder(orderPrefix + SelfdataDatasetHelper.DEFAULT_SORT_COLUMN);
			return searchSelfdataDatasetsFromDatasets(criteria);
		}

		// Récupération des JDDs correspondants
		MetadataList correspondingMetadatasContainer = selfdataDatasetHelper.searchCorrespondingMetadatas(requests);
		List<Metadata> correspondingMetadatas = correspondingMetadatasContainer.getItems();

		// Si on ne trouve pas de JDDs correspondants on ne cherche que les JDDs selfdata
		if (CollectionUtils.isEmpty(correspondingMetadatas)) {
			LOGGER.warn("Aucun jeu de données ne correspond aux demandes de l'utilisateur connecte");
			return searchSelfdataDatasetsFromDatasets(criteria);
		}

		// Construction des selfdata-datasets à partir des demandes et des JDDs correspondants
		Map<UUID, Metadata> correspondingMetadatasByUuid = correspondingMetadatas.stream()
				.collect(Collectors.toMap(Metadata::getGlobalId, Function.identity()));
		for (SelfdataDatasetEntity request : requests) {

			// Recherche du JDD à partir du UUID dans la demandee
			Metadata dataset = correspondingMetadatasByUuid.get(request.getDatasetUuid());

			// Si on trouve pas le JDD correspondant la demande ne sera pas restituée
			LOGGER.warn("Le jeu de données {}, de la demande d'id {} n'existe pas/plus", request.getDatasetUuid(),
					request.getId());
			if (dataset != null) {
				SelfdataDataset selfdataDataset = selfdataDatasetHelper.buildSelfdataDataset(dataset, request);
				page.getElements().add(selfdataDataset);
			}
		}

		// Si on a récupéré assez de données pour remplir 1 page on va juste aller chercher le total
		// des JDDs selfdata pour compléter la page
		if (page.getElements().size() == criteria.getLimit()) {
			page.setTotal(selfdataDatasetHelper.countSelfdataMetadata());
		}
		// Sinon il faut aller chercher des JDDs selfdata liés a aucune demande
		// pour compléter la page, on obtient également le total avec cet appel
		else {
			criteria.setLimit(criteria.getLimit() - page.getElements().size());
			MetadataList remainingMetadatas = selfdataDatasetHelper.searchRemainingMetadatas(criteria,
					correspondingMetadatas);
			List<SelfdataDataset> missingsSelfdataDatasets = remainingMetadatas.getItems().stream()
					.map(selfdataDatasetHelper::buildSelfdataDataset).collect(Collectors.toList());
			page.getElements().addAll(missingsSelfdataDatasets);
			page.setTotal(remainingMetadatas.getTotal());
		}

		return page;
	}

	private void validEntity(SelfdataInformationRequestEntity entity) {
		// Pour le moment on ne fait aucune vérification supplémentaire sur les entités
	}

	@Override
	public List<MatchingData> getMySelfdataInformationRequestMatchingData(UUID datasetUuid)
			throws InvalidDataException, FormDefinitionException, UserNotFoundException {
		val user = utilContextHelper.getAuthenticatedUser();
		if (user == null) {
			throw new UserNotFoundException("Utilisateur non trouvé");
		}
		Map<String, Object> informationRequestMap = selfdataMatchingDataHelper
				.getInformationRequestMapByDatasetUuid(user.getLogin(), datasetUuid);

		Optional<Section> optionalMatchingDataSection = selfdataMatchingDataHelper
				.getMatchingDataSectionByDatasetUuid(datasetUuid);

		if (optionalMatchingDataSection.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("Pas de section données pivot pour l'asset de dataset id: %s", datasetUuid));
		} else {
			Section matchingDataSection = optionalMatchingDataSection.get();
			List<MatchingData> matchingData = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(matchingDataSection.getFields())) {
				for (Field field : matchingDataSection.getFields()) {
					MatchingData data = new MatchingData();
					data.setLabel(field.getDefinition().getLabel());
					data.setType(FieldType.fromValue(field.getDefinition().getType().getValue()));
					data.setValue(informationRequestMap.get(field.getDefinition().getName()).toString());
					matchingData.add(data);
				}
			}
			return matchingData;
		}
	}

	@Override
	public void recryptSelfdataInformationRequest(String previousAliasKey) {
		int offset = 0;
		int count = 0;
		log.info("Recrypt started...");
		Pageable pageable = PageRequest.of(offset, recryptPageSize, Direction.ASC, "id");
		log.info("Recrypt from item {} to {}", offset, offset + recryptPageSize);
		// pour toutes les pages jusqu'à ce qu'on ait plus de données
		while ((count = getMe().recryptSelfdataInformationRequest(previousAliasKey, pageable)) > 0) {
			log.info("Recrypt count items {}", count);
			offset += recryptPageSize;
			pageable = PageRequest.of(offset, recryptPageSize, Direction.ASC, "id");
			log.info("Recrypt from item {} to {}", offset, offset + recryptPageSize);
		}
		log.info("Recrypt done.");
	}

	/**
	 * @return l'implémentation de service transactionnée
	 */
	protected SelfdataServiceImpl getMe() {
		return (SelfdataServiceImpl) applicationContext.getBean(SelfdataService.class);
	}

	/**
	 * chiffrement des données pour une page - seule cette méthode est transactionnée
	 *
	 * @param previousAliasKey l'alias de la clé dépréciée
	 * @param pageable         la pagination
	 * @return le nombre d'éléments collectés
	 */
	@Transactional // readonly = false
	public int recryptSelfdataInformationRequest(String previousAliasKey, Pageable pageable) {
		// Collecte des demandes
		SelfdataInformationRequestCustomSearchCriteria searchCriteria = new SelfdataInformationRequestCustomSearchCriteria();
		Page<SelfdataInformationRequestEntity> items = selfdataInformationRequestCustomDao
				.searchSelfdataInformationRequests(searchCriteria, pageable);
		// itération sur les demandes
		for (SelfdataInformationRequestEntity item : items) {
			// pour chaque demande
			try {
				log.info("Recrypt handle {}", item.getUuid());
				selfdataMatchingDataHelper.recryptSelfdataInformationRequest(item, previousAliasKey);
			} catch (Exception e) {
				log.error("Recrypt failed to handle:" + item.getUuid(), e);
			}
		}
		return items.getNumberOfElements();
	}

}
