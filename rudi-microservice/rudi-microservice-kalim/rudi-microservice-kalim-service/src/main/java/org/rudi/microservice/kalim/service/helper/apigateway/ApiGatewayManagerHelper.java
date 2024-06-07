package org.rudi.microservice.kalim.service.helper.apigateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.apigateway.exceptions.CreateApiException;
import org.rudi.facet.apigateway.exceptions.GetApiException;
import org.rudi.facet.apigateway.exceptions.UpdateApiException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataset.bean.InterfaceContract;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiMethod;
import org.rudi.microservice.apigateway.core.bean.ApiParameter;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.rudi.microservice.kalim.service.helper.ApiManagerHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiGatewayManagerHelper implements ApiManagerHelper {

	private static final Map<InterfaceContract, List<ApiMethod>> CONTRACT_API_METHODS = new EnumMap<>(
			InterfaceContract.class);

	private final org.rudi.facet.apigateway.helper.ApiGatewayHelper apiGatewayHelper;
	private final ProviderHelper providerHelper;
	private final OrganizationHelper organizationHelper;

	static {
		CONTRACT_API_METHODS.put(InterfaceContract.DOWNLOAD, List.of(ApiMethod.GET));
		CONTRACT_API_METHODS.put(InterfaceContract.GENERIC_DATA, List.of(ApiMethod.GET));
		CONTRACT_API_METHODS.put(InterfaceContract.TEMPORAL_BAR_CHART, List.of(ApiMethod.GET));
		CONTRACT_API_METHODS.put(InterfaceContract.WFS, List.of(ApiMethod.GET, ApiMethod.POST));
		CONTRACT_API_METHODS.put(InterfaceContract.WMS, List.of(ApiMethod.GET, ApiMethod.POST));
		CONTRACT_API_METHODS.put(InterfaceContract.WMTS, List.of(ApiMethod.GET, ApiMethod.POST));
	}

	private List<Throttling> throttlings;

	@Override
	public void createApis(IntegrationRequestEntity integrationRequest, Metadata metadata) throws APIManagerException {

		final List<Media> medias = getValidMedias(metadata);
		for (Media media : medias) {
			log.info("Create Api Add media {} {}", metadata.getGlobalId(), media.getMediaId());
			createOrUpdateApiForMedia(metadata, media, integrationRequest);
		}
	}

	@Override
	public void updateApis(IntegrationRequestEntity integrationRequest, Metadata metadata, Metadata actualMetadata)
			throws APIManagerException {
		// On récupère les média valides dans les JDDs
		List<Media> nextMedias = getValidMedias(metadata);
		List<Media> previousMedias = getValidMedias(actualMetadata);

		// On récupère les 3 listes : les modifs, ajout et suppressions
		List<Media> modified = getModifiedMedias(previousMedias, nextMedias);
		List<Media> added = getAddedMedias(previousMedias, nextMedias);
		List<Media> deleted = getDeletedMedias(previousMedias, nextMedias);

		// Maj des APIs
		for (Media mediaUpdated : modified) {
			log.info("Update API Update media {}", mediaUpdated.getMediaId());
			createOrUpdateApiForMedia(metadata, mediaUpdated, integrationRequest);
		}

		// Création des APIs
		for (Media mediaAdded : added) {
			log.info("Update API Add media {}", mediaAdded.getMediaId());
			createOrUpdateApiForMedia(metadata, mediaAdded, integrationRequest);
		}

		// Delete des APIs
		for (Media mediaDeleted : deleted) {
			log.info("Update API Delete media {}", mediaDeleted.getMediaId());
			deleteApiForMedia(metadata, mediaDeleted, integrationRequest);
		}

	}

	@Override
	public void deleteApis(IntegrationRequestEntity integrationRequest) throws APIManagerException {
		ApiSearchCriteria searchCriteria = new ApiSearchCriteria();
		searchCriteria.setGlobalId(integrationRequest.getGlobalId());
		try {
			Page<Api> apis = apiGatewayHelper.searchApis(searchCriteria, Pageable.unpaged());
			if (apis != null && !apis.isEmpty()) {
				apis.getContent().forEach(api -> {
					try {
						apiGatewayHelper.deleteApi(api.getApiId());
					} catch (Exception e) {
						log.warn("Failed to delete api:" + api.getApiId(), e);
					}
				});
			}
		} catch (Exception e) {
			throw new APIManagerException("Failed to delete api for dataset:" + integrationRequest.getGlobalId(), e);
		}

	}

	/**
	 * 
	 * @param metadata           les métadonnées
	 * @param media              le media
	 * @param integrationRequest la requete
	 * @throws APIManagerException
	 */
	protected void deleteApiForMedia(Metadata metadata, Media media, IntegrationRequestEntity integrationRequest)
			throws APIManagerException {
		try {
			apiGatewayHelper.deleteApi(metadata.getGlobalId(), media.getMediaId());
		} catch (Exception e) {
			throw new APIManagerException("Failed to delete api " + metadata.getGlobalId() + " " + media.getMediaId(),
					e);
		}
	}

	/**
	 * 
	 * @param metadata           les métadonnées
	 * @param media              le media
	 * @param integrationRequest la requete
	 * @return
	 * @throws APIManagerException
	 */
	protected Api createOrUpdateApiForMedia(Metadata metadata, Media media, IntegrationRequestEntity integrationRequest)
			throws APIManagerException {
		Api api = null;
		try {
			// Récupération des infos du fournisseur, du noeud, du producteur
			NodeProvider nodeProvider = providerHelper
					.requireNodeProviderByUUID(integrationRequest.getNodeProviderId());
			Provider provider = providerHelper
					.requireProviderByNodeProviderUUID(integrationRequest.getNodeProviderId());
			Organization producer = organizationHelper.getOrganization(metadata.getProducer().getOrganizationId());

			// on est résilient ...
			api = createOrUpdateApiForMedia(metadata, media, nodeProvider, provider, producer);
		} catch (Exception e) {
			throw new APIManagerException("Failed to update api " + metadata.getGlobalId() + " " + media.getMediaId(),
					e);
		}
		return api;
	}

	private Api createOrUpdateApiForMedia(Metadata metadata, Media media, NodeProvider nodeProvider, Provider provider,
			Organization producer) throws GetApiException, CreateApiException, UpdateApiException {
		Api api;
		api = apiGatewayHelper.getApiById(metadata.getGlobalId(), media.getMediaId());
		if (api == null) {
			log.info("Create API for media {}", media.getMediaId());
			api = createApi(metadata, media, nodeProvider, provider, producer);
			api = apiGatewayHelper.createApi(api);
		} else {
			log.info("Update API for media {}", media.getMediaId());
			updateApi(api, metadata, media, nodeProvider, provider, producer);
			api = apiGatewayHelper.updateApi(api);
		}
		return api;
	}

	@Override
	public List<Media> getValidMedias(Metadata metadata) {
		// Pour tous les médias du JDD
		return metadata.getAvailableFormats().stream()
				// On regarde s'ils sont valide d'un point de vue OpenAPI -> on a un template de swagger pour ce format
				.filter(this::hasOpenApiTemplate).collect(Collectors.toList());
	}

	@Override
	public List<Api> synchronizeMedias(Metadata metadata) {
		List<Api> handledApis = new ArrayList<>();
		ApiSearchCriteria searchCriteria = new ApiSearchCriteria();
		searchCriteria.setGlobalId(metadata.getGlobalId());
		try {
			Page<Api> existingApis = apiGatewayHelper.searchApis(searchCriteria, Pageable.unpaged());

			// Récupération des infos du fournisseur, du noeud, du producteur
			Provider provider = providerHelper
					.getProviderByUUID(metadata.getMetadataInfo().getMetadataProvider().getOrganizationId());
			NodeProvider nodeProvider = CollectionUtils.isNotEmpty(provider.getNodeProviders())
					? provider.getNodeProviders().get(0)
					: null;
			Organization producer = organizationHelper.getOrganization(metadata.getProducer().getOrganizationId());

			for (Media media : metadata.getAvailableFormats()) {
				synchronizeMedia(handledApis, metadata, provider, nodeProvider, producer, media);
			}
			if (!existingApis.isEmpty()) {
				for (Api existingApi : existingApis) {
					synchronizeMedia(metadata, handledApis, existingApi);
				}
			}

		} catch (Exception e) {
			log.warn("Failed to synchronize medias for dataset " + metadata.getGlobalId(), e);
		}
		return handledApis;
	}

	private void synchronizeMedia(Metadata metadata, List<Api> handledApis, Api existingApi) {
		try {
			Api api = lookupApi(handledApis, existingApi.getGlobalId(), existingApi.getMediaId());
			if (api == null) {
				apiGatewayHelper.deleteApi(existingApi.getApiId());
			}
		} catch (Exception e) {
			log.warn("Failed to synchronize dataset " + metadata.getGlobalId() + " " + existingApi.getMediaId(), e);
		}
	}

	private void synchronizeMedia(List<Api> handledApis, Metadata metadata, Provider provider,
			NodeProvider nodeProvider, Organization producer, Media media) {
		try {
			Api api = createOrUpdateApiForMedia(metadata, media, nodeProvider, provider, producer);
			if (api != null) {
				handledApis.add(api);
			}
		} catch (Exception e) {
			log.warn("Failed to synchronize dataset " + metadata.getGlobalId() + " " + media.getMediaId(), e);
		}
	}

	/**
	 * Est-ce que le média fourni a bien ce qu'il faut pour créer une API WSO2
	 *
	 * @param media le média testé
	 * @return vrai/faux
	 */
	private boolean hasOpenApiTemplate(Media media) {
		return InterfaceContract.nullableFromCode(media.getConnector().getInterfaceContract()) != null;
	}

	private Api createApi(Metadata metadata, Media media, NodeProvider nodeProvider, Provider provider,
			Organization producer) {
		Api api = new Api();
		updateApi(api, metadata, media, nodeProvider, provider, producer);
		return api;
	}

	private void updateApi(Api api, Metadata metadata, Media media, NodeProvider nodeProvider, Provider provider,
			Organization producer) {
		api.setGlobalId(metadata.getGlobalId());
		api.setMediaId(media.getMediaId());
		if (provider != null) {
			api.setProviderId(provider.getUuid());
		}
		if (nodeProvider != null) {
			api.setNodeProviderId(nodeProvider.getUuid());
		}
		if (producer != null) {
			api.setProducerId(producer.getUuid());
		}
		api.setContract(media.getConnector().getInterfaceContract());
		api.setUrl(media.getConnector().getUrl());
		api.setMethods(computeMethods(media.getConnector().getInterfaceContract()));
		api.setThrottlings(computeThrottlings(metadata, media));
		api.setParameters(buildAdditionalParameters(media.getConnector(), api.getParameters()));
	}

	/**
	 * un jour il faudra même le throttling correspondant au media...
	 * 
	 * @param metadata les metadonnées
	 * @param media    le média
	 * @return
	 */
	protected List<Throttling> computeThrottlings(Metadata metadata, Media media) {
		if (throttlings == null) {
			try {
				Page<Throttling> throttlingPage = apiGatewayHelper.searchThrottlings(new ThrottlingSearchCriteria(),
						Pageable.unpaged());
				throttlings = throttlingPage.getContent();
			} catch (Exception e) {
				log.warn("Failed to get throttlings...", e);
			}
		}
		return throttlings;
	}

	private List<ApiParameter> buildAdditionalParameters(Connector connector, List<ApiParameter> parameters) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		if (connector != null && CollectionUtils.isNotEmpty(connector.getConnectorParameters())) {
			for (ConnectorConnectorParametersInner connectorParameter : connector.getConnectorParameters()) {
				ApiParameter apiParameter = lookupApiParameter(parameters, connectorParameter.getKey());
				if (apiParameter == null) {
					apiParameter = new ApiParameter();
					apiParameter.setName(connectorParameter.getKey());
					parameters.add(apiParameter);
				}
				apiParameter.setValue(connectorParameter.getValue());
			}
			Iterator<ApiParameter> it = parameters.iterator();
			while (it.hasNext()) {
				ApiParameter parameter = it.next();
				if (lookupConnectorParameter(connector.getConnectorParameters(), parameter.getName()) == null) {
					it.remove();
				}
			}
		} else {
			parameters.clear();
		}
		return parameters;
	}

	private ApiParameter lookupApiParameter(List<ApiParameter> parameters, String key) {
		ApiParameter result = null;
		if (CollectionUtils.isNotEmpty(parameters)) {
			result = parameters.stream().filter(p -> p.getName().equals(key)).findFirst().orElse(null);
		}
		return result;
	}

	private ConnectorConnectorParametersInner lookupConnectorParameter(
			List<ConnectorConnectorParametersInner> parameters, String key) {
		ConnectorConnectorParametersInner result = null;
		if (CollectionUtils.isNotEmpty(parameters)) {
			result = parameters.stream().filter(p -> p.getKey().equals(key)).findFirst().orElse(null);
		}
		return result;
	}

	private List<ApiMethod> computeMethods(String interfaceContractValue) {
		InterfaceContract interfaceContract = InterfaceContract.nullableFromCode(interfaceContractValue);
		if (interfaceContract != null) {
			return CONTRACT_API_METHODS.get(interfaceContract);
		} else {
			return List.of(ApiMethod.values());
		}
	}

	/**
	 * Renvoie la liste des média qui sont une MAJ d'un ancien média existant
	 *
	 * @param previousMedias la liste des précédents médias du JDD
	 * @param nextMedias     la nouvelle liste des médias du JDD
	 * @return la liste des média qui sont modifiés
	 */
	private List<Media> getModifiedMedias(List<Media> previousMedias, List<Media> nextMedias) {
		return intersects(previousMedias, nextMedias);
	}

	/**
	 * Récupère les médias qui sont à supprimer
	 *
	 * @param previousMedias la liste des médias du JDD avant la modif
	 * @param nextMedias     la liste des médias après la modif
	 * @return liste de médias
	 */
	private List<Media> getDeletedMedias(List<Media> previousMedias, List<Media> nextMedias) {

		// Les supprimés sont ceux qui sont dans la liste avant et PLUS dans la liste d'après
		return substractSecondMediasInFirst(previousMedias, nextMedias);
	}

	/**
	 * Récupère les médias qui sont à ajouter
	 *
	 * @param previousMedias la liste des médias du JDD avant la modif
	 * @param nextMedias     la liste des médias après la modif
	 * @return liste de médias
	 */
	private List<Media> getAddedMedias(List<Media> previousMedias, List<Media> nextMedias) {

		// Les ajoutés sont ceux qui sont dans la liste après et ABSENT de la liste d'avant
		return substractSecondMediasInFirst(nextMedias, previousMedias);
	}

	/**
	 * Récupère les médias qui sont dans la première liste mais pas dans la seconde
	 *
	 * @param first  la première liste
	 * @param second la seconde liste
	 * @return une liste de médias
	 */
	private List<Media> substractSecondMediasInFirst(List<Media> first, List<Media> second) {
		Map<String, Media> nameMediaMapFirst = first.stream()
				.collect(Collectors.toMap(this::buildAPIName, Function.identity()));

		List<String> apiNamesFirst = first.stream().map(this::buildAPIName).collect(Collectors.toList());
		List<String> apiNamesSecond = second.stream().map(this::buildAPIName).collect(Collectors.toList());

		Collection<String> apiNamesInFirstNotInSecond = CollectionUtils.subtract(apiNamesFirst, apiNamesSecond);
		return apiNamesInFirstNotInSecond.stream().map(nameMediaMapFirst::get).collect(Collectors.toList());
	}

	/**
	 * Récupère l'intersection entre les 2 listes de médias
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	private List<Media> intersects(List<Media> first, List<Media> second) {
		Map<String, Media> nameMediaMapFirst = first.stream()
				.collect(Collectors.toMap(this::buildAPIName, Function.identity()));

		List<String> apiNamesFirst = first.stream().map(this::buildAPIName).collect(Collectors.toList());
		List<String> apiNamesSecond = second.stream().map(this::buildAPIName).collect(Collectors.toList());

		Collection<String> apiNamesInFirstNotInSecond = CollectionUtils.intersection(apiNamesFirst, apiNamesSecond);
		return apiNamesInFirstNotInSecond.stream().map(nameMediaMapFirst::get).collect(Collectors.toList());
	}

	/**
	 * Génère le nom de l'API
	 *
	 * @param media media associée à l'API
	 * @return String
	 */
	private String buildAPIName(Media media) {
		final var interfaceContract = InterfaceContract.fromCode(media.getConnector().getInterfaceContract());
		return media.getMediaId().toString() + "_" + interfaceContract.getUrlPath();
	}

	private Api lookupApi(List<Api> apis, UUID globalId, UUID mediaId) {
		Api result = null;
		if (CollectionUtils.isNotEmpty(apis)) {
			result = apis.stream().filter(api -> api.getGlobalId().equals(globalId) && api.getMediaId().equals(mediaId))
					.findFirst().orElse(null);
		}
		return result;
	}
}
