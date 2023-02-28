import {Order} from '../../core/services/projekt-metier.service';

/**
 *  interface de tri d'une tabe avec une pagination backend
 */
export interface SortTableInterface {
    page?: number;
    order?: Order;
}
