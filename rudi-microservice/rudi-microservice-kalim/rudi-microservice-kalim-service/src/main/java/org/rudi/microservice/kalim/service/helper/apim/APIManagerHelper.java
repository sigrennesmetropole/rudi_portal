package org.rudi.microservice.kalim.service.helper.apim;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;

import org.apache.commons.collections.ListUtils;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;
import org.rudi.facet.apimaccess.bean.APILifecycleStatusState;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.generator.OpenApiTemplates;
import org.rudi.facet.apimaccess.helper.rest.CustomClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParameters;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.stereotype.Component;
import org.wso2.carbon.apimgt.rest.api.publisher.API;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Component
@SuppressWarnings({ "java:S107" })
@Slf4j
@RequiredArgsConstructor
public class APIManagerHelper {

	private static final String GENERATE_CLIENT_KEYS_ERROR = "Erreur lors de la génération des clientKeys pour le rudi ou anonymous";
	private final APIsService apIsService;
	private final ApplicationService applicationService;
	private final ProviderHelper providerHelper;
	private final MetadataDetailsHelper metadataDetailsHelper;
	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	private final OpenApiTemplates openApiTemplates;
	private final APIManagerProperties apiManagerProperties;

	/**
	 * Création des APIs liées aux métadonnées
	 *
	 * @param integrationRequest demande d'intégration
	 * @param metadata           médatonnées
	 * @throws APIManagerException erreur lors de la création des apis
	 * @return la liste des API créées
	 */
	public List<API> createAPI(IntegrationRequestEntity integrationRequest, Metadata metadata) throws APIManagerException {

		try {
			// création des clientKeys pour les users rudi et anonymous
			checkRudiAndAnonymousClientRegistrations();
		} catch (Exception e) {
			throw new APIManagerException(GENERATE_CLIENT_KEYS_ERROR, e);
		}

		final List<Media> medias = getValidMedias(metadata);
		final List<API> apiList = new ArrayList<>(medias.size());
		for (Media media : medias) {
			apiList.add(createApiForMedia(metadata, media, integrationRequest));
		}

		return apiList;
	}

	/**
	 * mise à des APIs liées aux métadonnées
	 *
	 * @param integrationRequest demande d'intégration
	 * @param metadata           médatonnées qui représentent le nouvel état
	 * @param actualMetadata	 métadonnées qui représentent l'état avant la modif
	 * @throws APIManagerException erreur lors de la mise à jour des APIs
	 */
	public void updateAPI(IntegrationRequestEntity integrationRequest, Metadata metadata, Metadata actualMetadata) throws APIManagerException {

		// On s'assure que la registration est OK quand on va faire des modifs dans WSO2
		try {
			checkRudiAndAnonymousClientRegistrations();
		} catch (Exception e) {
			throw new APIManagerException(GENERATE_CLIENT_KEYS_ERROR, e);
		}

		// On récupère les média valides dans les JDDs
		List<Media> nextMedias = getValidMedias(metadata);
		List<Media> previousMedias = getValidMedias(actualMetadata);

		// On récupère les 3 listes : les modifs, ajout et suppressions
		List<Media> modified = getModifiedMedias(previousMedias, nextMedias);
		List<Media> added = getAddedMedias(previousMedias, nextMedias);
		List<Media> deleted = getDeletedMedias(previousMedias, nextMedias);

		// Maj des APIs
		for (Media mediaUpdated : modified) {
			updateApiForMedia(metadata, mediaUpdated, integrationRequest);
		}

		// Création des APIs
		for (Media mediaAdded : added) {
			createApiForMedia(metadata, mediaAdded, integrationRequest);
		}

		// Archivage des APIs
		for (Media mediaDeleted : deleted) {
			archiveApiForMedia(mediaDeleted, integrationRequest);
		}
	}

	/**
	 * Archivage (avant suppression totale) des APIs d'un JDD (et de toutes les souscriptions à cette API)
	 * En réalité on tag les APIs a "désactivée" pour les ré-activer + tard si besoin est
	 *
	 * @param integrationRequest demande d'intégration
	 * @throws APIManagerException erreur lors de la suppression des APIs
	 */
	public void archiveAllAPI(IntegrationRequestEntity integrationRequest) throws APIManagerException {
		// Certaines API sont peut-être déjà archivées, on n'archive donc que celles qui sont au statut PUBLISHED
		processAllAPI(integrationRequest, apIsService::archiveAPI, apiSearchCriteria -> apiSearchCriteria.status(APILifecycleStatusState.PUBLISHED));
	}

	/**
	 * Suppression totale des APIs d'un JDD (et de toutes les souscriptions à cette API)
	 *
	 * @param integrationRequest demande d'intégration
	 * @throws APIManagerException erreur lors de la suppression des APIs
	 */
	public void deleteAllAPI(IntegrationRequestEntity integrationRequest) throws APIManagerException {
		processAllAPI(integrationRequest, this::retireAPIToDeleteAllSubscriptions);
		processAllAPI(integrationRequest, apIsService::deleteAPI);
	}

	private void processAllAPI(IntegrationRequestEntity integrationRequest, ApiIdProcessor processor) throws APIManagerException {
		processAllAPI(integrationRequest, processor, UnaryOperator.identity());
	}

	private void processAllAPI(IntegrationRequestEntity integrationRequest, ApiIdProcessor processor, @Nonnull UnaryOperator<APISearchCriteria> criteriaProcessor) throws APIManagerException {

		try {
			// création des clientKeys pour les users rudi et anonymous
			checkRudiAndAnonymousClientRegistrations();
		} catch (Exception e) {
			throw new APIManagerException(GENERATE_CLIENT_KEYS_ERROR, e);
		}

		// Recherche des API à l'aide du global ID du
		final var apiSearchCriteria = buildApiSearchCriteriaFrom(integrationRequest);
		final var processedApiSearchCriteria = criteriaProcessor.apply(apiSearchCriteria);
		final var apiInfoList = searchApiInfosByDatasetUUid(processedApiSearchCriteria);

		// Suppression de toutes ces APIs
		for (APIInfo apiInfo : apiInfoList) {
			processor.process(apiInfo.getId());
		}
	}

	@FunctionalInterface
	private interface ApiIdProcessor {
		void process(String apiId) throws APIManagerException;
	}

	/**
	 * Passe l'API au statut "RETIRED", ce qui supprime automatiquement toutes ses souscriptions.
	 * Seule alternative possible à {@link org.rudi.facet.apimaccess.service.ApplicationService#deleteAllSubscriptionsWithoutRetiringAPI(String)}
	 * lorsque l'API contient des souscriptions qui n'appartiennent pas seulement à rudi ou anonymous.
	 */
	public void retireAPIToDeleteAllSubscriptions(String apiId) throws APIManagerException {
		apIsService.updateAPILifecycleStatus(apiId, APILifecycleStatusAction.DEPRECATE);
		apIsService.updateAPILifecycleStatus(apiId, APILifecycleStatusAction.RETIRE);
	}

	/**
	 * Génère les paramètres de l'API à sauvegarder
	 *
	 * @param globalId     global id médadonnées
	 * @param nodeProvider noeud provider
	 * @param provider     provider
	 * @param media        media associé à l'api
	 * @return APIDescription
	 */
	APIDescription buildAPIDescriptionByMetadataIntegration(UUID globalId, NodeProvider nodeProvider, Provider provider, Media media) {
		val connector = media.getConnector();
		val uri = URI.create(connector.getUrl());
		return new APIDescription()
				.globalId(globalId)
				.providerUuid(provider.getUuid())
				.providerCode(provider.getCode())
				.endpointUrl(uri.isAbsolute() ? uri.toString() : nodeProvider.getUrl() + uri)
				.interfaceContract(connector.getInterfaceContract())
				.mediaUuid(media.getMediaId())
				.mediaType(((MediaFile) media).getFileType().getValue())
				.name(buildAPIName(media))
				.additionalProperties(buildAdditionalProperties(connector));
	}

	private Map<String, String> buildAdditionalProperties(@Nonnull Connector connector) {
		final var connectorParameters = connector.getConnectorParameters();
		if (connectorParameters != null) {
			return connectorParameters.stream()
					.collect(Collectors.toMap(ConnectorConnectorParameters::getKey, ConnectorConnectorParameters::getValue));
		}
		return null;
	}

	/**
	 * Génère le nom de l'API
	 *
	 * @param media media associée à l'API
	 * @return String
	 */
	private String buildAPIName(Media media) {
		return media.getMediaId().toString() + "_" + media.getConnector().getInterfaceContract();
	}

	/**
	 * Est-ce que le média fourni a bien ce qu'il faut pour créer une API WSO2
	 * @param media le média testé
	 * @return vrai/faux
	 */
	private boolean hasOpenApiTemplate(Media media) {
		final String interfaceContract = media.getConnector().getInterfaceContract();
		final boolean hasApiDefinitionTemplate = openApiTemplates.existsByInterfaceContract(interfaceContract);
		if (!hasApiDefinitionTemplate) {
			log.warn("Media with id \"{}\" uses interfaceContract \"{}\" which is not known among Open API templates and thus no WSO2 API will be created.", media.getMediaId(), interfaceContract);
		}
		return hasApiDefinitionTemplate;
	}

	/**
	 * Création des clientKeys pour les users rudi et anonymous
	 *
	 * @throws SSLException Erreur lors de la création des clientKeys
	 */
	private void checkRudiAndAnonymousClientRegistrations() throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		checkClientRegistration(apiManagerProperties.getRudiUsername(), apiManagerProperties.getRudiPassword());
		checkClientRegistration(apiManagerProperties.getAnonymousUsername(), apiManagerProperties.getAnonymousPassword());
	}

	private void checkClientRegistration(String username, String password) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		customClientRegistrationRepository.findRegistrationOrRegister(username, password);
	}

	private APISearchCriteria buildApiSearchCriteriaFrom(IntegrationRequestEntity integrationRequest) {
		val provider = providerHelper.getProviderByNodeProviderUUID(integrationRequest.getNodeProviderId());
		return new APISearchCriteria()
				.globalId(integrationRequest.getGlobalId())
				.providerUuid(provider.getUuid())
				.providerCode(provider.getCode());
	}

	/**
	 * Recherche d'infos des APIs WSO2 à l'aide d'un critère de recherche
	 * @return une liste contenant les infos des APIs correspondantes
	 * @throws APIManagerException levée en cas d'erreur d'appel à WSO2
	 */
	private List<org.wso2.carbon.apimgt.rest.api.publisher.APIInfo> searchApiInfosByDatasetUUid(final APISearchCriteria apiSearchCriteria) throws APIManagerException {
		final var apis = apIsService.searchAPI(apiSearchCriteria);
		return apis.getList();
	}

	/**
	 * Création d'une API WSO2 pour un média
	 * @param metadata le JDD qui contient le média
	 * @param media le média qui va créer l'API
	 * @param integrationRequest la requête d'intégration du JDD
	 * @throws APIManagerException levée si erreur WSO2
	 * @return l'API créée
	 */
	private API createApiForMedia(Metadata metadata, Media media, IntegrationRequestEntity integrationRequest) throws APIManagerException {

		// Récupération des infos sur le fournisseur de données
		NodeProvider nodeProvider = providerHelper.getNodeProviderByUUID(integrationRequest.getNodeProviderId());
		Provider provider = providerHelper.getProviderByNodeProviderUUID(integrationRequest.getNodeProviderId());

		// Construction de l'API chez WSO2
		APIDescription apiDescription = buildAPIDescriptionByMetadataIntegration(metadata.getGlobalId(), nodeProvider, provider, media);
		API api = apIsService.createOrUnarchiveAPI(apiDescription);

		// Souscription à l'APi par RUDI pour pouvoir l'utiliser
		try {
			applicationService.createDefaultSubscriptions(api.getId(), metadataDetailsHelper.isRestricted(metadata));
		} catch (APIManagerException e) {
			apIsService.deleteAPI(api.getId());
			throw e;
		}

		return api;
	}

	/**
	 * MAJ d'une API WSO2 pour un média
	 * @param metadata le JDD contenant le média
	 * @param media le média lié à l'API WSO2
	 * @param integrationRequest la requête d'intégration du JDD
	 * @throws APIManagerException levée si erreur WSO2
	 */
	private void updateApiForMedia(Metadata metadata, Media media, IntegrationRequestEntity integrationRequest) throws APIManagerException {

		// Récupération des infos du fournisseur
		NodeProvider nodeProvider = providerHelper.getNodeProviderByUUID(integrationRequest.getNodeProviderId());
		Provider provider = providerHelper.getProviderByNodeProviderUUID(integrationRequest.getNodeProviderId());

		// Construction de la nouvelle description de l'API puis MAJ chez WSO2
		APIDescription apiDescription = buildAPIDescriptionByMetadataIntegration(metadata.getGlobalId(), nodeProvider, provider, media);
		apIsService.updateAPIByName(apiDescription);
	}

	private void archiveApiForMedia(Media media, IntegrationRequestEntity integrationRequest) throws APIManagerException {
		// Récupération des infos du fournisseur
		val nodeProvider = providerHelper.getNodeProviderByUUID(integrationRequest.getNodeProviderId());
		val provider = providerHelper.getProviderByNodeProviderUUID(integrationRequest.getNodeProviderId());

		// Construction de la nouvelle description de l'API puis suppression chez WSO2
		val apiDescription = buildAPIDescriptionByMetadataIntegration(integrationRequest.getGlobalId(), nodeProvider, provider, media);
		apIsService.archiveAPIByName(apiDescription);

		// Suppression des souscriptions à l'API
		// Ce n'est plus nécessaire car lorsque l'API est archivée (BLOCKED), les clients ne peuvent plus appeler l'API
	}

	/**
	 * Filtrage des média "valides" pour RUDI dans un JDD
	 * @param metadata le JDD qui contient les médias
	 * @return la liste des médias sur lesquels on va vraiment faire quelque chose
	 */
	private List<Media> getValidMedias(Metadata metadata) {

		// Pour tous les médias du JDD
		return metadata.getAvailableFormats().stream()
				// On ne gère que les type FILE pour l'instant + on regarde s'ils sont valide d'un point de vue OpenAPI
				.filter(media -> media instanceof MediaFile && hasOpenApiTemplate(media))
				.collect(Collectors.toList());
	}

	/**
	 * Renvoie la liste des média qui sont une MAJ d'un ancien média existant
	 * @param previousMedias la liste des précédents médias du JDD
	 * @param nextMedias la nouvelle liste des médias du JDD
	 * @return la liste des média qui sont modifiés
	 */
	private List<Media> getModifiedMedias(List<Media> previousMedias, List<Media> nextMedias) {

		// la liste des média : modifiés CAD pas nouveaux ni supprimés mais différents
		List<Media> modified = new ArrayList<>();

		// pour chaque vieux média
		for(Media previousMedia : previousMedias) {

			// On regarde si il a été modifié dans la liste suivante
			Optional<Media> modifiedMediaContainer = nextMedias.stream().filter(media -> isModification(previousMedia, media)).findFirst();
			modifiedMediaContainer.ifPresent(modified::add);
		}

		return modified;
	}

	/**
	 * Récupère les médias qui sont dans la première liste mais pas dans la seconde
	 * @param first la première liste
	 * @param second la seconde liste
	 * @return une liste de médias
	 */
	private List<Media> substractSecondMediasInFirst(List<Media> first, List<Media> second) {

		Map<String, Media> nameMediaMapFirst = first.stream().collect(Collectors.toMap(this::buildAPIName, Function.identity()));

		List<String> apiNamesFirst = first.stream().map(this::buildAPIName).collect(Collectors.toList());
		List<String> apiNamesSecond = second.stream().map(this::buildAPIName).collect(Collectors.toList());

		List<String> apiNamesInFirstNotInSecond = ListUtils.subtract(apiNamesFirst, apiNamesSecond);
		return apiNamesInFirstNotInSecond.stream().map(nameMediaMapFirst::get).collect(Collectors.toList());
	}

	/**
	 * Récupère les médias qui sont à supprimer
	 * @param previousMedias la liste des médias du JDD avant la modif
	 * @param nextMedias la liste des médias après la modif
	 * @return liste de médias
	 */
	private List<Media> getDeletedMedias(List<Media> previousMedias, List<Media> nextMedias) {

		// Les supprimés sont ceux qui sont dans la liste avant et PLUS dans la liste d'après
		return substractSecondMediasInFirst(previousMedias, nextMedias);
	}

	/**
	 * Récupère les médias qui sont à ajouter
	 * @param previousMedias la liste des médias du JDD avant la modif
	 * @param nextMedias la liste des médias après la modif
	 * @return liste de médias
	 */
	private List<Media> getAddedMedias(List<Media> previousMedias, List<Media> nextMedias) {

		// Les ajoutés sont ceux qui sont dans la liste après et ABSENT de la liste d'avant
		return substractSecondMediasInFirst(nextMedias, previousMedias);
	}

	/**
	 * Indique si le média next représente une modification du média previous
	 * @param previous l'ancienne version du média
	 * @param next la nouvelle version du média
	 * @return si ils sont une modification l'un de l'autre
	 */
	private boolean isModification(Media previous, Media next) {

		// Connecteurs égaux si ils ont la même URL et le même interface contract
		boolean sameConnector = previous.getConnector().getInterfaceContract().equals(next.getConnector().getInterfaceContract())
				&& previous.getConnector().getUrl().equals(next.getConnector().getUrl());

		// Modification si noms identiques mais connecteurs différents ou attributs différents
		return buildAPIName(previous).equals(buildAPIName(next)) && (!sameConnector || !previous.equals(next));
	}
}
