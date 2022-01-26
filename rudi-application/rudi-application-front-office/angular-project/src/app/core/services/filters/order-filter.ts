import {Filters} from '../../../shared/models/filters';
import {BehaviorSubject} from 'rxjs';
import {FiltersService} from '../filters.service';
import {Filter} from './filter';

export type OrderValue =
    'name' |
    '-name' |
    'title' |
    '-title' |
    'producername' |
    '-producername' |
    'updatedate' |
    '-updatedate';

export const DESC_PREFIX = '-';

export const DEFAULT_VALUE: OrderValue = 'name';

export class OrderFilter extends Filter<OrderValue> {
    protected filtersKey = 'order';

    constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
        super(filtersService, filters);
    }

    getEmptyValue(): OrderValue {
        return DEFAULT_VALUE;
    }

}
