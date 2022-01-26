import {Filters} from '../../../shared/models/filters';
import {BehaviorSubject} from 'rxjs';
import {FiltersService} from '../filters.service';
import {ArrayFilter} from './array-filter';

export class ProducerNamesFilter extends ArrayFilter {

  constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
    super(filtersService, filters);
  }

  protected get filtersKey(): string {
    return 'producerNames';
  }

}
