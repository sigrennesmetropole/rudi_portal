import {Filter} from './filter';
import {BehaviorSubject} from 'rxjs';
import {Filters} from '../../../shared/models/filters';
import {FiltersService} from '../filters.service';
import {AccessStatusFiltersType} from './access-status-filters-type';

export class AccessStatusFilter extends Filter<AccessStatusFiltersType> {
    protected filtersKey = 'accessStatus';

    constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
        super(filtersService, filters);
    }

    getEmptyValue(): AccessStatusFiltersType {
        return null;
    }

}
