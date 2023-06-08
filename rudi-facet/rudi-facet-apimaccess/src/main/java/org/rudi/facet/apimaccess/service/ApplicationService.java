package org.rudi.facet.apimaccess.service;

import java.io.IOException;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.exception.APISubscriptionException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.springframework.util.MultiValueMap;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;
import org.wso2.carbon.apimgt.rest.api.devportal.SubscriptionList;

public interface ApplicationService {

	/**
	 * Création d'une application
	 *
	 * @param application application à créer
	 * @param username    username de l'utilisateur
	 * @return Application
	 * @throws APIManagerException Erreur lors de la création de l'application
	 */
	Application createApplication(Application application, String username) throws APIManagerException;

	/**
	 * Récupérer une application
	 *
	 * @param applicationId Identifiant de l'application
	 * @param username      username de l'utilisateur
	 * @return Application
	 * @throws APIManagerException Erreur lors de la récupération de l'application
	 */
	Application getApplication(String applicationId, String username) throws APIManagerException;

	/**
	 * Créer l'application RUDI (avec ses clés PRODUCTION ET SANDBOX) par défaut pour l'utilisateur
	 * ou la récupère si elle existe déjà.
	 */
	Application getOrCreateDefaultApplication(String username) throws ApplicationOperationException;

	/**
	 * Recherche des applications
	 *
	 * @param applicationSearchCriteria Critères de recherche
	 * @param username                  username de l'utilisateur
	 * @return Applications
	 * @throws APIManagerException Erreur lors de la recherche
	 */
	Applications searchApplication(ApplicationSearchCriteria applicationSearchCriteria, String username) throws APIManagerException;

	/**
	 * Recherche de la liste des applications et API souscrits par ces applications
	 *
	 * @param devPortalSubscriptionSearchCriteria Critères de recherches
	 * @param username                            username de l'utilisateur
	 * @return ApplicationAPISubscriptions
	 * @throws APIManagerException Erreur lors de la recherche
	 */
	SubscriptionList searchApplicationAPISubscriptions(DevPortalSubscriptionSearchCriteria devPortalSubscriptionSearchCriteria, String username)
			throws APIManagerException;

	/**
	 * Souscription d'une application à une API
	 *
	 * @param applicationAPISubscription paramètres de la souscription
	 * @param username                   username de l'utilisateur
	 * @return Subscription
	 * @throws APIManagerException Erreur lors de la souscription
	 */
	Subscription subscribeAPI(Subscription applicationAPISubscription, String username) throws APIManagerException;

	/**
	 * Souscription de l'application par défaut de l'admin à une API
	 *
	 * @param apiId    Identifiant de l'API
	 * @param username username de l'utilisateur
	 * @return Subscription
	 * @throws APIManagerException Erreur lors de la souscription
	 */
	Subscription subscribeAPIToDefaultUserApplication(String apiId, String username) throws APIManagerException;

	/**
	 * Ajoute les souscriptions par défaut sur une API
	 */
	void createDefaultSubscriptions(String apiId, boolean isRestricted) throws APISubscriptionException, ApplicationOperationException;

	/**
	 * Suppression de toutes les souscriptions à une application (en vue d'une suppression de l'API par exemple)
	 * sans modifier le statut de l'API (contrairement à org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper#retireAPIToDeleteAllSubscriptions)
	 *
	 * @param apiId Identifiant de l'API
	 * @throws APIManagerException Erreur lors de la suppression de la souscription
	 */
	void deleteAllSubscriptionsWithoutRetiringAPI(String apiId) throws APIManagerException;

	boolean hasSubscribeAPIToDefaultUserApplication(String apiId, String username) throws APIManagerException;

	/**
	 * Permet de savoir si une application a souscrit à une api
	 *
	 * @param apiId         identifiant de l'api
	 * @param applicationId identifiant de l'application
	 * @param username      username de l'utilisateur
	 * @return Boolean
	 * @throws APIManagerException Erreur lors de la récupération de la souscription
	 */
	boolean hasSubscribeAPI(String apiId, String applicationId, String username) throws APIManagerException;

	/**
	 * Récupérer la souscription
	 *
	 * @param subscriptionId Identifiant de la souscription
	 * @param username       username de l'utilisateur
	 * @return Subscription
	 * @throws APIManagerException Erreur lors de la récupération de la souscription
	 */
	Subscription getSubscriptionAPI(String subscriptionId, String username) throws APIManagerException;

	/**
	 * Mise à jour d'une souscription
	 *
	 * @param applicationAPISubscription Paramètres de la souscription
	 * @param username                   username de l'utilisateur
	 * @return Subscription
	 * @throws APIManagerException Erreur lors de la mise à jour
	 */
	Subscription updateSubscriptionAPI(Subscription applicationAPISubscription, String username) throws APIManagerException;

	/**
	 * Se désabonner à une souscription
	 *
	 * @param subscriptionId Identifiant de la souscription
	 * @param username       username de l'utilisateur
	 * @throws APIManagerException Erreur du désabonnement
	 */
	void unsubscribeAPI(String subscriptionId, String username) throws APIManagerException;

	/**
	 * Téléchargement des données d'une API
	 *
	 * @param globalId Identifiant des métadonnées de l'API
	 * @param mediaId  Identifiant du média de l'API
	 * @param username username de l'utilisateur
	 * @param parameters
	 * @return DocumentContent
	 * @throws APIManagerException Erreur lors de la récupération des informations
	 */
	DocumentContent downloadAPIContent(UUID globalId, UUID mediaId, String username, MultiValueMap<String, String> parameters) throws APIManagerException, IOException;

	/**
	 * Supprimer une application
	 *
	 * @param applicationId Identifiant de l'application
	 * @param username      username de l'utilisateur
	 * @throws APIManagerException Erreur lors de la suppression de l'application
	 */
	void deleteApplication(String applicationId, String username) throws APIManagerException;

	/**
	 * @param globalId Identifiant des métadonnées
	 * @param mediaId  Identifiant du média des métadonnées
	 * @return l'URL pour télécharger le média
	 * @throws APIManagerException erreur reçue de l'API Manager
	 */
	String buildAPIAccessUrl(UUID globalId, UUID mediaId) throws APIManagerException;

	ApplicationKey getApplicationKey(String applicationId, String username, EndpointKeyType keyType) throws ApplicationOperationException;

	/**
	 * Recherche de la liste des API souscrits par un utilisateur à partir de ses applications
	 *
	 * @param devPortalSubscriptionSearchCriteria Critères de recherches
	 * @param username                            username de l'utilisateur
	 * @return ApplicationAPISubscriptions
	 * @throws APIManagerException Erreur lors de la recherche
	 */
	SubscriptionList searchUserSubscriptions(DevPortalSubscriptionSearchCriteria devPortalSubscriptionSearchCriteria, String username)
			throws APIManagerException;

	/**
	 * Supprime toutes les souscriptions aux médias d'un JDD pour l'utilisateur donné
	 *
	 * @param username    user which subscribed
	 * @param datasetUuid uuid du JDD dont le user a souscrit aux médias
	 * @throws APIManagerException
	 */
	void deleteUserSubscriptionsForDatasetAPIs(String username, UUID datasetUuid) throws APIManagerException;

}
