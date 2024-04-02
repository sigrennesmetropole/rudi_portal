import {Filters} from '@shared/models/filters';
import {BehaviorSubject} from 'rxjs';
import {FiltersService} from '../filters.service';
import {AccessStatusFiltersType} from './access-status-filters-type';
import {Filter} from './filter';

export class AccessStatusFilter extends Filter<AccessStatusFiltersType> {
    protected filtersKey = 'accessStatus';

    constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
        super(filtersService, filters);
    }

    getEmptyValue(): AccessStatusFiltersType {
        return null;
    }

}
