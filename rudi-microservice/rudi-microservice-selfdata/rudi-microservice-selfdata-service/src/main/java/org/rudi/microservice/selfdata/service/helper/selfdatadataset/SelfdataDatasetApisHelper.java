package org.rudi.microservice.selfdata.service.helper.selfdatadataset;

import java.time.OffsetDateTime;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.apimaccess.api.application.ApplicationOperationAPI;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.exception.APIEndpointException;
import org.rudi.facet.apimaccess.exception.APIsOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationKeysNotFoundException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationTokenGenerationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.RudiClientRegistrationRepository;
import org.rudi.facet.apimaccess.service.APIsService;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.selfdata.SelfdataMediaHelper;
import org.rudi.microservice.selfdata.core.bean.BarChartData;
import org.rudi.microservice.selfdata.core.bean.GenericDataObject;
import org.rudi.microservice.selfdata.service.exception.InvalidSelfdataApisException;
import org.rudi.microservice.selfdata.service.exception.MissingApiForMediaException;
import org.rudi.microservice.selfdata.service.exception.TechnicalWso2CallException;
import org.rudi.microservice.selfdata.service.exception.UserNotSubscribedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.APIList;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SelfdataDatasetApisHelper {

	@Value("${tpbc.param.minDate:min-date}")
	private String minDateParam;

	@Value("${tpbc.param.maxDate:max-date}")
	private String maxDateParam;

	private final SelfdataMediaHelper selfdataMediaHelper;
	private final RudiClientRegistrationRepository rudiClientRegistrationRepository;
	private final ApplicationOperationAPI applicationOperationAPI;
	private final APIsService apIsService;

	/**
	 * Récupère le média GDATA pour consommer les données personnelles de l'utilisateur connecté
	 *
	 * @param metadata le JDD selfdata
	 * @return le média GDATA du JDD
	 * @throws AppServiceException si erreur de malformation du JDD
	 */
	protected Media getGdataMedia(Metadata metadata) throws AppServiceException {

		if (metadata == null) {
			throw new AppServiceException("Impossible de chercher un média GDATA si aucun JDD fourni");
		}

		// Erreur 404 si JDD pas dans le bon état => ce n'est pas sensé arriver
		if (!selfdataMediaHelper.hasMandatoryMediasForAutomaticTreatment(metadata)) {
			throw new InvalidSelfdataApisException(metadata.getGlobalId());
		}

		// Recherche du média GDATA
		return selfdataMediaHelper.getGdataMedia(metadata.getAvailableFormats());
	}

	/**
	 * Récupère le média TPBC pour consommer les données personnelles de l'utilisateur connecté
	 *
	 * @param metadata le JDD selfdata
	 * @return le média TPBC du JDD
	 * @throws AppServiceException si erreur de malformation du JDD
	 */
	protected Media getTpbcMedia(Metadata metadata) throws AppServiceException {

		if (metadata == null) {
			throw new AppServiceException("Impossible de chercher un média TPBC si aucun JDD fourni");
		}

		// Erreur 404 si JDD pas dans le bon état => ce n'est pas sensé arriver
		if (!selfdataMediaHelper.hasMandatoryMediasForAutomaticTreatment(metadata)) {
			throw new InvalidSelfdataApisException(metadata.getGlobalId());
		}

		// Recherche du média TPBC
		return selfdataMediaHelper.getTpbcMedia(metadata.getAvailableFormats());
	}

	/**
	 * Recherche dans le cache des souscriptions WSO2, si le user fourni a bien souscrit pour consommer les APIs
	 *
	 * @param user l'utilisateur testé (utilisateur connecté)
	 * @return la souscription, nul si non souscrit
	 * @throws AppServiceException si pas authentifié
	 */
	protected ClientRegistration searchCachedRegistration(AuthenticatedUser user) throws AppServiceException, GetClientRegistrationException {

		// Contrôle de l'authent
		if (user == null || StringUtils.isBlank(user.getLogin())) {
			throw new AppServiceUnauthorizedException(
					"Impossible de récupérer les données GDATA sans être authentifié");
		}

		// Vérification de l'enregistrement dans WSO2
		return rudiClientRegistrationRepository.findByUsername(user.getLogin());
	}

	/**
	 * Appelle WSO2 pour récupérer les données personnelles
	 *
	 * @param parameters l'ensemble des pré-requis pour réaliser l'appel vers WSO2
	 * @return les données personnelles au format GDATA
	 * @throws AppServiceException si erreur technique
	 */
	public GenericDataObject getGdataData(SelfdataApiParameters parameters) throws AppServiceException, GetClientRegistrationException {

		if (parameters == null || parameters.getMetadata() == null || parameters.getUser() == null
				|| StringUtils.isBlank(parameters.getUser().getLogin()) || parameters.getApplication() == null
				|| StringUtils.isBlank(parameters.getApplication().getApplicationId())) {
			throw new AppServiceException("Paramètres obligatoires manquants");
		}

		Metadata metadata = parameters.getMetadata();
		AuthenticatedUser user = parameters.getUser();
		Application application = parameters.getApplication();

		// Recherche du média GDATA
		Media gdataMedia = getGdataMedia(metadata);
		if (gdataMedia == null) {
			throw new AppServiceException(
					"Malgré les contrôles, pas de média GDATA sur le JDD d'uuid : " + metadata.getGlobalId());
		}

		// orchestration des appels vers WSO2
		ClientResponse response = callWso2Api(user, gdataMedia, metadata, application, new LinkedMultiValueMap<>());

		return response.bodyToMono(GenericDataObject.class).block();
	}

	/**
	 * Appelle WSO2 pour récupérer les données personnelles
	 *
	 * @param parameters l'ensemble des pré-requis pour réaliser l'appel vers WSO2
	 * @return les données personnelles au format TPBC
	 * @throws AppServiceException si erreur technique
	 */
	public BarChartData getTpbcData(SelfdataApiParameters parameters) throws AppServiceException, GetClientRegistrationException {

		if (parameters == null || parameters.getMetadata() == null || parameters.getUser() == null
				|| StringUtils.isBlank(parameters.getUser().getLogin()) || parameters.getApplication() == null
				|| StringUtils.isBlank(parameters.getApplication().getApplicationId())) {
			throw new AppServiceException("Paramètres obligatoires manquants");
		}

		OffsetDateTime minDate = parameters.getMinDate();
		OffsetDateTime maxDate = parameters.getMaxDate();

		if (minDate != null && maxDate != null && minDate.isAfter(maxDate)) {
			throw new AppServiceBadRequestException("Période saisie non valide (dateMax < dateMin)");
		}

		Metadata metadata = parameters.getMetadata();
		AuthenticatedUser user = parameters.getUser();
		Application application = parameters.getApplication();

		// Recherche du média TPBC
		Media tpbcMedia = getTpbcMedia(metadata);
		if (tpbcMedia == null) {
			throw new AppServiceException(
					"Malgré les contrôles, pas de média TPBC sur le JDD d'uuid : " + metadata.getGlobalId());
		}

		// Gestion des paramètres de date en filtrage
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		if (minDate != null) {
			queryParams.add(minDateParam, minDate.toString());
		}
		if (maxDate != null) {
			queryParams.add(maxDateParam, maxDate.toString());
		}

		// orchestration des appels vers WSO2
		ClientResponse response = callWso2Api(user, tpbcMedia, metadata, application, queryParams);

		return response.bodyToMono(BarChartData.class).block();
	}

	/**
	 * Appel technique vers WSO2 pour récupérer des données
	 *
	 * @param connectedUser l'utilisateur "connecté" pour récuperer ses données personnelles
	 * @param media         le media concerné par les données personnelles
	 * @param metadata      le JDD contenant le média
	 * @param application   l'application WSO2 de l'utilisateur connecté
	 * @param queryParams   paramètres dans l'URI optionnels à transmettre à l'API
	 * @return une réponse wrappant les données personnelles
	 * @throws AppServiceException problème d'appel vers WSO2
	 */
	private ClientResponse callWso2Api(AuthenticatedUser connectedUser, Media media, Metadata metadata,
			Application application, MultiValueMap<String, String> queryParams) throws AppServiceException, GetClientRegistrationException {

		// Recherche de la registration dans le cache pour appeler WSO2
		ClientRegistration registration = searchCachedRegistration(connectedUser);
		if (registration == null) {
			throw new UserNotSubscribedException();
		}

		// Demande à WSO2 des infos sur l'API pour l'appeler
		APISearchCriteria criteria = new APISearchCriteria().globalId(metadata.getGlobalId())
				.mediaUuid(media.getMediaId());
		APIList apiList;
		try {
			apiList = apIsService.searchAPI(criteria);
		} catch (APIsOperationException e) {
			throw new AppServiceException(
					"Erreur lors de la recherche de l'API correspondant au média d'uuid " + media.getMediaId(), e);
		}

		// Check : a-t-on une API ?
		if (apiList == null || CollectionUtils.isEmpty(apiList.getList())) {
			throw new MissingApiForMediaException(metadata.getGlobalId(), media.getMediaId());
		}

		// Appel de l'API dans WSO2
		APIInfo apiInfo = apiList.getList().get(0);
		ClientResponse response;
		try {
			response = applicationOperationAPI.getAPIResponse(apiInfo.getContext(), apiInfo.getVersion(),
					application.getApplicationId(), connectedUser.getLogin(), queryParams);
		} catch (ApplicationOperationException | ApplicationKeysNotFoundException | APIEndpointException
				| ApplicationTokenGenerationException e) {
			throw new TechnicalWso2CallException(e);
		}

		return response;
	}
}
