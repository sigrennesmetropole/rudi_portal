import {BehaviorSubject, Observable, of} from 'rxjs';
import {FiltersService} from '../filters.service';
import {Filters} from '../../../shared/models/filters';
import {switchMap} from 'rxjs/operators';

export abstract class Filter<T> {

  readonly value$: Observable<T>;

  protected constructor(private readonly filtersService: FiltersService, private readonly filters: BehaviorSubject<Filters>) {
    this.value$ = this.filters.asObservable().pipe(
      switchMap(currentFilters => of(this.getFilterFrom(currentFilters)))
    );
  }

  private getFilterFrom(currentFilters: Filters): T {
    return currentFilters[this.filtersKey];
  }

  protected abstract get filtersKey(): string;

  get value(): T {
    return this.getFilterFrom(this.filtersService.currentFilters);
  }

  set value(value: T) {
    if (!this.valuesAreEqual(value, this.value)) {
      const nextFilters = {
        ...this.filters.value
      };
      this.patchFilters(nextFilters, value);
      this.filters.next(nextFilters);
    }
  }

  clear(): void {
    this.value = this.getEmptyValue();
  }

  protected abstract getEmptyValue(): T;

  private patchFilters(filters: Filters, value: T): void {
    filters[this.filtersKey] = value;
  }

  patchFiltersWithEmptyValue(filters: Filters): void {
    this.patchFilters(filters, this.getEmptyValue());
  }

  get active(): boolean {
    return !!this.value;
  }

  protected valuesAreEqual(value1: T, value2: T): boolean {
    return value1 === value2;
  }

    isEmptyValue(value: T): boolean {
        return this.valuesAreEqual(value, this.getEmptyValue());
    }
}
