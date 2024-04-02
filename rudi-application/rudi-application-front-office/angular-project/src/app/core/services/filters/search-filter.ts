import {Filters} from '@shared/models/filters';
import {BehaviorSubject} from 'rxjs';
import {FiltersService} from '../filters.service';
import {Filter} from './filter';

export class SearchFilter extends Filter<string> {
  protected filtersKey = 'search';

  constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
    super(filtersService, filters);
  }

  getEmptyValue(): string {
    return '';
  }

}
