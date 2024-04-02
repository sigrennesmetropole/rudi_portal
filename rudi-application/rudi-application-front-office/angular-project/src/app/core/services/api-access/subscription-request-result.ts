import {Metadata} from 'micro_service_modules/api-kaccess';
import {SubscriptionRequestStatus} from './subscription-request-status.enum';

/**
 * Un résultat de requête de souscription
 */
export class SubscriptionRequestResult {

    /**
     * Le JDD auquel on a tenté de souscrire
     */
    metadata: Metadata;

    /**
     * L'état de la souscription
     */
    success: SubscriptionRequestStatus;

    /**
     * L'erreur ayant eu lieu si FAILED
     */
    error: Error;

    /**
     * Constructeur objet : tentative de souscription un JDD
     * @param metadata le JDD auquel on a tenté de souscrire
     * @param success le statut de la tentative
     * @param error erreur si échec
     */
    constructor(metadata: Metadata, success: SubscriptionRequestStatus, error?: Error) {
        this.metadata = metadata;
        this.success = success;
        this.error = error;
    }
}
