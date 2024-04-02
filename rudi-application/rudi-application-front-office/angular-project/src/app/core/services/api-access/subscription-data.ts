import {Metadata} from 'micro_service_modules/api-kaccess';
import {LinkedDataset} from 'micro_service_modules/projekt/projekt-model';

/**
 * Objet nécessaire pour réaliser une souscription dans RUDI
 */
export interface SubscriptionData {

    /**
     * On ne peut souscrire qu'à un JDD, il est obligatoire
     */
    metadata: Metadata;

    /**
     * Dans certains cas, il faut une demande associée (JDD restreint d'un projet par exemple)
     */
    linkedDataset?: LinkedDataset;
}
