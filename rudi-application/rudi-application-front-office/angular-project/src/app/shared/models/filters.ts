import {AccessStatusFiltersType} from '@core/services/filters/access-status-filters-type';
import {OrderValue} from '@core/services/filters/order-filter';

export interface Filters {
    search: string;
    themes: string[];
    keywords: string[];
    producerNames: string[];
    dates: {
        debut: string;
        fin: string;
    };
    order: OrderValue;
    accessStatus: AccessStatusFiltersType;

    /** Global ID du ou des jeux de données */
    globalIds: string[];
    producerUuids: string[],
}
