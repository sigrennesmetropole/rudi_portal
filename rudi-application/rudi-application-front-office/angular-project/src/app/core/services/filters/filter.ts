import {Filters} from '@shared/models/filters';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {FiltersService} from '../filters.service';

export abstract class Filter<T> {

  readonly value$: Observable<T>;
  private _forcedValue: T;

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

  get forcedValue(): T {
    return this._forcedValue;
  }

  set forcedValue(value: T) {
    this._forcedValue = value;
    this.value = value;
  }

  clear(): void {
    this.value = this.forcedValue !== undefined ? this.forcedValue : this.getEmptyValue();
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
