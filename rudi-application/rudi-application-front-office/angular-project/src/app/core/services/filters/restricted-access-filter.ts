import {Filter} from './filter';
import {BehaviorSubject} from 'rxjs';
import {Filters} from '../../../shared/models/filters';
import {FiltersService} from '../filters.service';

export class RestrictedAccessFilter extends Filter<boolean> {
  protected filtersKey = 'restrictedAccess';

  constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
    super(filtersService, filters);
  }

  getEmptyValue(): boolean {
    return null;
  }

}
