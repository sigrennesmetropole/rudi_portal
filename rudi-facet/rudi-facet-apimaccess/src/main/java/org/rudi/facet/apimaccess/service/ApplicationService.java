package org.rudi.facet.apimaccess.service;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptionSearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptions;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.exception.APIManagerException;

import java.io.IOException;
import java.util.UUID;

public interface ApplicationService {

    /**
     * Création d'une application
     *
     * @param application               application à créer
     * @param username                  username de l'utilisateur
     * @return                          Application
     * @throws APIManagerException      Erreur lors de la création de l'application
     */
    Application createApplication(Application application, String username) throws APIManagerException;

    /**
     * Récupérer une application
     *
     * @param applicationId             Identifiant de l'application
     * @param username                  username de l'utilisateur
     * @return                          Application
     * @throws APIManagerException      Erreur lors de la récupération de l'application
     */
    Application getApplication(String applicationId, String username) throws APIManagerException;

    /**
     * Recherche des applications
     *
     * @param applicationSearchCriteria     Critères de recherche
     * @param username                      username de l'utilisateur
     * @return                              Applications
     * @throws APIManagerException          Erreur lors de la recherche
     */
    Applications searchApplication(ApplicationSearchCriteria applicationSearchCriteria, String username) throws APIManagerException;

    /**
     * Recherche de la liste des applications et API souscrits par ces applications
     *
     * @param applicationAPISubscriptionSearchCriteria          Critères de recherches
     * @param username                                          username de l'utilisateur
     * @return                                                  ApplicationAPISubscriptions
     * @throws APIManagerException                              Erreur lors de la recherche
     */
    ApplicationAPISubscriptions searchApplicationAPISubscriptions(ApplicationAPISubscriptionSearchCriteria applicationAPISubscriptionSearchCriteria, String username)
            throws APIManagerException;

    /**
     * Souscription d'une application à une API
     *
     * @param applicationAPISubscription            paramètres de la souscription
     * @param username                              username de l'utilisateur
     * @return                                      ApplicationAPISubscription
     * @throws APIManagerException                  Erreur lors de la souscription
     */
    ApplicationAPISubscription subscribeAPI(ApplicationAPISubscription applicationAPISubscription, String username) throws APIManagerException;

    /**
     * Souscription de l'application par défaut de l'admin à une API
     * @param apiId                     Identifiant de l'API
     * @param username                  username de l'utilisateur
     * @return                          ApplicationAPISubscription
     * @throws APIManagerException      Erreur lors de la souscription
     */
    ApplicationAPISubscription subscribeAPIToDefaultUserApplication(String apiId, String username) throws APIManagerException;

    /**
     * Suppression de la souscription de l'application par défaut de l'admin à une API
     * @param apiId                     Identifiant de l'API
     * @param username                  username de l'utilisateur
     * @return                          ApplicationAPISubscription
     * @throws APIManagerException      Erreur lors de la suppression de la souscription
     */
    void unsubscribeAPIToDefaultUserApplication(String apiId, String username) throws APIManagerException;

    boolean hasSubscribeAPIToDefaultUserApplication(String apiId, String username) throws APIManagerException;

    /**
     * Permet de savoir si une application a souscrit à une api
     * @param apiId                 identifiant de l'api
     * @param applicationId         identifiant de l'application
     * @param username              username de l'utilisateur
     * @return                      Boolean
     * @throws APIManagerException  Erreur lors de la récupération de la souscription
     */
    boolean hasSubscribeAPI(String apiId, String applicationId, String username) throws APIManagerException;

    /**
     * Récupérer la souscription
     * @param subscriptionId            Identifiant de la souscription
     * @param username                  username de l'utilisateur
     * @return                          ApplicationAPISubscription
     * @throws APIManagerException      Erreur lors de la récupération de la souscription
     */
    ApplicationAPISubscription getSubscriptionAPI(String subscriptionId, String username) throws APIManagerException;

    /**
     * Mise à jour d'une souscription
     *
     * @param applicationAPISubscription        Paramètres de la souscription
     * @param username                          username de l'utilisateur
     * @return                                  ApplicationAPISubscription
     * @throws APIManagerException              Erreur lors de la mise à jour
     */
    ApplicationAPISubscription updateSubscriptionAPI(ApplicationAPISubscription applicationAPISubscription, String username) throws APIManagerException;

    /**
     * Se désabonner à une souscription
     *
     * @param subscriptionId            Identifiant de la souscription
     * @param username                  username de l'utilisateur
     * @throws APIManagerException      Erreur du désabonnement
     */
    void unsubscribeAPI(String subscriptionId, String username) throws APIManagerException;

    /**
     * Téléchargement des données d'une API
     *
     * @param globalId              Identifiant des métadonnées de l'API
     * @param mediaId               Identifiant du média de l'API
     * @param username              username de l'utilisateur
     * @return                      DocumentContent
     * @throws APIManagerException  Erreur lors de la récupération des informations
     */
    DocumentContent downloadAPIContent(UUID globalId, UUID mediaId, String username) throws APIManagerException, IOException;

    /**
     * Supprimer une application
     *
     * @param applicationId             Identifiant de l'application
     * @param username                  username de l'utilisateur
     * @throws APIManagerException      Erreur lors de la suppression de l'application
     */
    void deleteApplication(String applicationId, String username) throws APIManagerException;

    /**
     * @param globalId Identifiant des métadonnées
     * @param mediaId Identifiant du média des métadonnées
     * @return l'URL pour télécharger le média
     * @throws APIManagerException erreur reçue de l'API Manager
     */
	String buildAPIAccessUrl(UUID globalId, UUID mediaId) throws APIManagerException;

    /**
     * Est-ce que pour les identifiants fournis du JDD, on a bien une API de créée
     * @param globalId Identifiant des métadonnées
     * @param mediaId Identifiant du média des métadonnées
     * @return si oui ou non le JDD a une API
     */
    boolean hasApi(UUID globalId, UUID mediaId) throws APIManagerException;

}
