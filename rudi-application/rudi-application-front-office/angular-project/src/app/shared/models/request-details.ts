import {Moment} from 'moment';

/**
 * éléments de précision sur la demande d'accès à un JDD restreint
 */
export interface RequestDetails {

    /**
     * Explication de la demande d'accès
     */
    comment?: string;

    /**
     * Date de fin d'accès souhaitée
     */
    endDate?: Moment;
}
