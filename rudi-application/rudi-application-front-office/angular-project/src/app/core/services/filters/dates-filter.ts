import {Filter} from './filter';
import {BehaviorSubject} from 'rxjs';
import {Filters} from '../../../shared/models/filters';
import {FiltersService} from '../filters.service';

export interface Dates {
  debut: string;
  fin: string;
}

const EMPTY_FILTER: Dates = {
  debut: null,
  fin: null,
};

export class DatesFilter extends Filter<Dates> {

  constructor(filtersService: FiltersService, filters: BehaviorSubject<Filters>) {
    super(filtersService, filters);
  }

  protected get filtersKey(): string {
    return 'dates';
  }

  protected getEmptyValue(): Dates {
    return EMPTY_FILTER;
  }

  get active(): boolean {
    return !!this.value.debut || !!this.value.fin;
  }

  set dateDebut(dateDebut: string) {
    const newValue = {...this.value};
    newValue.debut = dateDebut;
    this.value = newValue;
  }

  get dateDebut(): string {
    return this.value.debut;
  }

  set dateFin(dateFin: string) {
    const newValue = {...this.value};
    newValue.fin = dateFin;
    this.value = newValue;
  }

  get dateFin(): string {
    return this.value.fin;
  }

    protected valuesAreEqual(value1: Dates, value2: Dates): boolean {
        return JSON.stringify(value1) === JSON.stringify(value2);
    }

}
