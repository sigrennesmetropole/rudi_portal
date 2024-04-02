import {Filters} from '@shared/models/filters';
import {BehaviorSubject} from 'rxjs';
import {FiltersService} from '../filters.service';
import {Filter} from './filter';

export type OrderValue =
    'name' |
    '-name' |
    'resource_title' |
    '-resource_title' |
    'producer.organization_name' |
    '-producer.organization_name' |
    'dataset_dates.updated' |
    '-dataset_dates.updated';

export const DEFAULT_VALUE: OrderValue = 'resource_title';

export class OrderFilter extends Filter<OrderValue> {
    protected filtersKey = 'order';

    constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
        super(filtersService, filters);
    }

    getEmptyValue(): OrderValue {
        return DEFAULT_VALUE;
    }

}
