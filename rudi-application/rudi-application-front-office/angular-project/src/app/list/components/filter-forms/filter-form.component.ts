import {Observable, of, Subscription} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {FiltersService} from '../../../core/services/filters.service';
import {switchMap} from 'rxjs/operators';
import {Filter} from '../../../core/services/filters/filter';
import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Item} from './array-filter-form.component';

/**
 * Regroupe les fonctions communes entre les filtres desktop et mobile
 */
@Component({
    template: '' // required by Angular
})
export abstract class FilterFormComponent<T, F extends Filter<T>, I extends Item> implements OnInit, OnDestroy {

    @Input() ulClass?: string;
    @Input() formClass?: string;
    readonly counter$: Observable<number>;
    @Output() submit = new EventEmitter<void>();
    @Output() selectedItemsChange = new EventEmitter<I[]>();
    private valueSubscription?: Subscription;

    protected constructor(
        protected readonly filtersService: FiltersService
    ) {
        this.counter$ = this.filter.value$.pipe(
            switchMap(value => of(this.count(value) || null))
        );
    }

    _formGroup?: FormGroup;

    get formGroup(): FormGroup {
        return this._formGroup;
    }

    protected get filter(): F {
        return this.getFilterFrom(this.filtersService);
    }

    ngOnInit(): void {
        this.valueSubscription = this.filter.value$.subscribe(() => {
            if (this.formGroup) {
                this.revert();
                this.selectedItemsChange.emit(this.selectedItems);
            }
        });
    }

    ngOnDestroy(): void {
        this.valueSubscription?.unsubscribe();
    }

    submitForm(): void {
        this.filter.value = this.getValueFromFormGroup();
        this.submit.emit();
    }

    protected abstract get selectedItems(): I[];

    /**
     * Revert current value to value from filter. <b>Please verify, in each implementation, that this.formGroup is defined !</b>
     */
    abstract revert(): void;

    protected abstract getFilterFrom(filtersService: FiltersService): F;

    protected initFormGroup(): void {
        this._formGroup = this.buildFormGroup();
        this.selectedItemsChange.emit(this.selectedItems);
    }

    protected abstract buildFormGroup(): FormGroup;

    protected abstract getValueFromFormGroup(): T;

    protected abstract count(value: T): number;

    get submitIsEnabled(): boolean {
        return this.someItemsAreBeingSelected || this.filter.active;
    }

    /**
     * @returns ture if some items are being selected even if they have not been submitted yet
     */
    get someItemsAreBeingSelected(): boolean {
        return !this.filter.isEmptyValue(this.getValueFromFormGroup());
    }
}

