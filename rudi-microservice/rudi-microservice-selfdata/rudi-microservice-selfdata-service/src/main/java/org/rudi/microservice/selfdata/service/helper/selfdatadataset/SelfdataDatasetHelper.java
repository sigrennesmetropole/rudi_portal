package org.rudi.microservice.selfdata.service.helper.selfdatadataset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.selfdata.core.bean.SelfdataDataset;
import org.rudi.microservice.selfdata.core.bean.SelfdataDatasetSearchCriteria;
import org.rudi.microservice.selfdata.storage.dao.selfdatadataset.SelfdataDatasetCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatadataset.SelfdataDatasetCustomSearchCriteria;
import org.rudi.microservice.selfdata.storage.dao.selfdatadataset.SelfdataDatasetDao;
import org.rudi.microservice.selfdata.storage.entity.SelfdataDataset.SelfdataDatasetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.FUNCTIONAL_STATUS_FIELD;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.PROCESS_DEFINITION_KEY_FIELD;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.UPDATED_DATE_FIELD;
import static org.rudi.microservice.selfdata.storage.entity.SelfdataDataset.SelfdataDatasetEntity.TITLE_FIELD;

@Component
@RequiredArgsConstructor
public class SelfdataDatasetHelper {

	private final DatasetService datasetService;

	private final SelfdataDatasetDao selfdataDatasetDao;

	private final UtilContextHelper utilContextHelper;

	private final SelfdataDatasetCustomDao selfdataDatasetCustomDao;

	/**
	 * Tri par défaut à appliquer sur la recherche de selfdata dataset
	 */
	public static final String DEFAULT_SORT_COLUMN = RudiMetadataField.RESOURCE_TITLE.getLocalName();

	/**
	 * Map critères de tri en entrée de l'API -> critères à appliquer dans les algorithmes
	 */
	public static final Map<String, String> ORDERS_APPLIED_MAP = Map.of(
		TITLE_FIELD, RudiMetadataField.RESOURCE_TITLE.getLocalName()
	);

	/**
	 * L'ensemble des critères de tri qui concernent les JDDs
	 */
	private static final String[] DATASET_SORT_COLUMNS = new String[]{
			RudiMetadataField.RESOURCE_TITLE.getLocalName()
	};

	/**
	 * L'ensemble des critères de tri qui concernent les demandes
	 */
	private static final String[] REQUESTS_SORT_COLUMNS = new String[]{
			PROCESS_DEFINITION_KEY_FIELD,
			UPDATED_DATE_FIELD,
			FUNCTIONAL_STATUS_FIELD
	};

	/**
	 * Message d'erreur quand on appelle les algorithmes sans être authentifié
	 */
	private static final String NOT_AUTHENTICATED_ERROR_MESSAGE = "Impossible de rechercher les requêtes de" +
			" l'utilisateur connecté sans être connecté";

	/**
	 * Récupère le total des JDDs selfdata dans dataverse
	 *
	 * @return le nombre total de JDDs selfdata
	 * @throws AppServiceException levée si un problème avec dataverse
	 */
	public Long countSelfdataMetadata() throws AppServiceException {
		try {
			DatasetSearchCriteria criteria = new DatasetSearchCriteria().limit(0).offset(0).gdprSensitive(true);
			MetadataListFacets container = datasetService.searchDatasets(criteria, Collections.emptyList());
			return container.getMetadataList().getTotal();
		} catch (DataverseAPIException e) {
			throw new AppServiceException("Erreur lors du comptage du total de JDDs selfdata dans dataverse", e);
		}
	}

	/**
	 * Recherche les JDDs selfdata avec un critère de tri et de pagination
	 *
	 * @param criteria critères de tri et de pagination à appliquer aux JDDs
	 * @return Les éléments contenus dans une page de JDDs selfdata
	 * @throws AppServiceException levée si problème lors de la rcherche dataverse
	 */
	public MetadataList searchSelfdataMetadata(SelfdataDatasetSearchCriteria criteria) throws AppServiceException {

		DatasetSearchCriteria datasetCriteria = new DatasetSearchCriteria()
				.offset(criteria.getOffset())
				.limit(criteria.getLimit())
				.order(criteria.getOrder())
				.gdprSensitive(true);

		MetadataListFacets datasetsContainer;
		try {
			datasetsContainer = datasetService.searchDatasets(datasetCriteria, Collections.emptyList());
		} catch (DataverseAPIException e) {
			throw new AppServiceException("Erreur lors de la recherche des JDDs selfdata dans dataverse", e);
		}

		return datasetsContainer.getMetadataList();
	}

	/**
	 * Recherche des demandes de l'utilisateur connecté correspondants aux JDDs fournis
	 *
	 * @param datasets les JDDs selfdata dont on cherche les demandes
	 * @return l'ensemble des demandes qui concernent les JDDs fournis
	 */
	public List<SelfdataDatasetEntity> searchCorrespondingRequests(List<Metadata> datasets)
			throws AppServiceUnauthorizedException {

		List<UUID> datasetUuids = datasets.stream().map(Metadata::getGlobalId).collect(Collectors.toList());

		AuthenticatedUser user = utilContextHelper.getAuthenticatedUser();
		if (user == null) {
			throw new AppServiceUnauthorizedException(NOT_AUTHENTICATED_ERROR_MESSAGE);
		}

		SelfdataDatasetCustomSearchCriteria criteria = new SelfdataDatasetCustomSearchCriteria();
		criteria.setDatasetUuids(datasetUuids);
		criteria.setLogin(user.getLogin());
		return selfdataDatasetCustomDao.searchSelfdataDatasets(criteria);
	}

	/**
	 * Recherche des dernières demandes de l'utilisateur connecté avec un critère de tri et de pagination
	 * C'est à dire qu'on récupère les demandes par JDDs distincts et celles-ci sont les dernières réalisées
	 * par l'utilisateur connecté
	 *
	 * @param pageable le tri et la pagination à appliquer aux demandes
	 * @return l'ensemble des demandes de l'utilisateur triées
	 */
	public List<SelfdataDatasetEntity> searchMySelfDatasets(Pageable pageable)
			throws AppServiceUnauthorizedException {

		AuthenticatedUser user = utilContextHelper.getAuthenticatedUser();
		if (user == null) {
			throw new AppServiceUnauthorizedException(NOT_AUTHENTICATED_ERROR_MESSAGE);
		}

		return selfdataDatasetDao.findByInitiator(user.getLogin(), pageable);
	}

	/**
	 * Recherche des JDDs correspondant à un ensemble de demandes
	 *
	 * @param requests des demandes selfdata
	 * @return des JDDs auxquels on a fait ces demandes
	 * @throws AppServiceException levée si un problème de recherche dataverse
	 */
	public MetadataList searchCorrespondingMetadatas(List<SelfdataDatasetEntity> requests)
			throws AppServiceException {

		List<UUID> datasetUuids = requests.stream().map(SelfdataDatasetEntity::getDatasetUuid)
				.collect(Collectors.toList());

		DatasetSearchCriteria datasetCriteria = new DatasetSearchCriteria()
				.gdprSensitive(true)
				.globalIds(datasetUuids);

		MetadataListFacets datasetsContainer;
		try {
			datasetsContainer = datasetService.searchDatasets(datasetCriteria, Collections.emptyList());
		} catch (DataverseAPIException e) {
			throw new AppServiceException("Erreur lors de la récupération des JDDs correspondants aux demandes", e);
		}

		return datasetsContainer.getMetadataList();
	}

	/**
	 * Recherche des jeux de données manquants pour combler une page de selfdata-dataset contenant des jeux de données
	 * ayant une demande mais n'étant pas assez nombreux pour remplir une page
	 *
	 * @param criteria         Critères de recherche de base
	 * @param alreadyRetrieved les JDDs déja récupérés pour filtrer
	 * @return des JDDs sans demandes pour combler une page + avoir la taille totale
	 * @throws AppServiceException levée en cas de problème avec dataverse
	 */
	public MetadataList searchRemainingMetadatas(SelfdataDatasetSearchCriteria criteria, List<Metadata> alreadyRetrieved)
			throws AppServiceException {

		String orderApplied = DEFAULT_SORT_COLUMN;
		if (!isOrderAscending(criteria)) {
			orderApplied = "-" + orderApplied;
		}

		DatasetSearchCriteria datasetCriteria = new DatasetSearchCriteria()
				.order(orderApplied)
				.offset(criteria.getOffset())
				.limit(criteria.getLimit())
				.gdprSensitive(true);

		MetadataListFacets datasetsContainer;
		try {
			datasetsContainer = datasetService.searchDatasets(datasetCriteria, Collections.emptyList());
		} catch (DataverseAPIException e) {
			throw new AppServiceException(
					"Erreur lors de la récupération des JDDs sans demandes pour compléter la page", e);
		}

		List<UUID> alreadyRetrievedUuids = alreadyRetrieved.stream()
				.map(Metadata::getGlobalId).collect(Collectors.toList());

		List<Metadata> missings = datasetsContainer.getMetadataList()
				.getItems().stream().filter(metadata -> !alreadyRetrievedUuids.contains(metadata.getGlobalId()))
				.collect(Collectors.toList());

		datasetsContainer.getMetadataList().setItems(missings);
		return datasetsContainer.getMetadataList();
	}

	/**
	 * Détermine si on doit commencer la recherche en partant des jeux de données
	 * ou des demandes d'accès
	 *
	 * @param criteria les critères notamment ceux de tri pour savoir d'où on part
	 * @return si on attaque par les metadata
	 */
	public boolean isSearchFromDatasets(SelfdataDatasetSearchCriteria criteria) throws AppServiceBadRequestException {
		if (criteria.getOrder() == null) {
			criteria.setOrder(DEFAULT_SORT_COLUMN);
			return false;
		}

		String currentOrder = criteria.getOrder();
		if (!isOrderAscending(criteria)) {
			currentOrder = currentOrder.substring(1);
		}

		boolean inDataset = Arrays.asList(DATASET_SORT_COLUMNS).contains(currentOrder);
		boolean inRequests = Arrays.asList(REQUESTS_SORT_COLUMNS).contains(currentOrder);

		if (!inDataset && !inRequests) {
			throw new AppServiceBadRequestException("Demande de tri sur une colonne inconnue");
		}

		return inDataset;
	}

	/**
	 * Conversion du critère de tri envoyé en un critère qui sera réellement utilisé dans les algos
	 * @param criteria les critères de tri et de pagination
	 */
	public void mapOrderToAppliedOrder(SelfdataDatasetSearchCriteria criteria) {

		if(StringUtils.isEmpty(criteria.getOrder())) {
			criteria.setOrder(DEFAULT_SORT_COLUMN);
			return;
		}

		String currentOrder = criteria.getOrder();
		String orderPrefix = "";
		if(!isOrderAscending(criteria)) {
			orderPrefix = "-";
			currentOrder = currentOrder.substring(1);
		}

		String orderApplied = ORDERS_APPLIED_MAP.get(currentOrder);
		if(orderApplied != null) {
			criteria.setOrder(orderPrefix + orderApplied);
		}
	}

	/**
	 * Est-ce que le critère de tri est dans la direction ascendante ?
	 * @param criteria les critères de tri / pagination
	 * @return si ascendant
	 */
	public boolean isOrderAscending(SelfdataDatasetSearchCriteria criteria) {
		if(criteria == null || criteria.getOrder() == null) {
			throw new IllegalStateException("Impossible de déterminer une direction au tri s'il n'existe pas");
		}

		return !criteria.getOrder().startsWith("-");
	}

	/**
	 * Construit un objet SelfdataDataset à partir d'un JDD et d'une demande de la vue selfdata
	 *
	 * @param metadata le JDD selfdata
	 * @param request  la demande liée
	 * @return un selfdataDataset à afficher
	 */
	public SelfdataDataset buildSelfdataDataset(Metadata metadata, SelfdataDatasetEntity request) {
		SelfdataDataset selfdataDataset = buildSelfdataDataset(metadata);

		if (request != null) {
			selfdataDataset.setProcessDefinitionKey(request.getProcessDefinitionKey());
			selfdataDataset.setUpdatedDate(request.getUpdatedDate());
			selfdataDataset.setFunctionalStatus(request.getFunctionalStatus());
		}
		return selfdataDataset;
	}

	/**
	 * Construit un objet SelfdataDataset à partir d'un JDD
	 *
	 * @param metadata le JDD selfdata
	 * @return un selfdataDataset à afficher
	 */
	public SelfdataDataset buildSelfdataDataset(Metadata metadata) {
		SelfdataDataset selfdataDataset = new SelfdataDataset();
		if (metadata != null) {
			selfdataDataset.setTitle(metadata.getResourceTitle());
			selfdataDataset.setDatasetUuid(metadata.getGlobalId());
		}

		return selfdataDataset;
	}
}
