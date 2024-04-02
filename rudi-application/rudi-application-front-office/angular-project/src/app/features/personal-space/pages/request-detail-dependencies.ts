import {Moment} from 'moment/moment';

/**
 * les dépendances de la page globale : détail de la demande
 */
export interface RequestDetailDependencies {
    ownerName?: string;
    ownerEmail?: string;
    receivedDate?: Moment;
    datasetTitle?: string;
    expiredDate?: Moment;
    taskStatus?: string;
    processDefinitionKey?: string;

}
