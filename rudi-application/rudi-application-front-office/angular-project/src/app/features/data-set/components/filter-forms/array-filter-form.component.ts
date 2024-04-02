import {Component, Input} from '@angular/core';
import {AbstractControl, FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {FiltersService} from '@core/services/filters.service';
import {ArrayFilter} from '@core/services/filters/array-filter';
import {FilterFormComponent} from './filter-form.component';

export interface Item {
    name: string;
    value: any;
}

@Component({
    template: '' // required by Angular
})
export abstract class ArrayFilterFormComponent<T> extends FilterFormComponent<string[], ArrayFilter, Item> {
    items: Item[];

    protected constructor(
        protected readonly filtersService: FiltersService
    ) {
        super(filtersService);
    }

    @Input() set values(values: T[] | undefined) {
        if (values) {
            this.items = values.map(value => this.getItemFromValue(value));
            this.initFormGroup();
        }
    }

    abstract get formArrayName(): string;

    protected abstract get formGroupName(): string;

    private get controls(): AbstractControl[] {
        const formArray = this.formGroup.get(this.formArrayName) as FormArray;
        return formArray.controls;
    }

    /**
     * Reset selected item from filter value
     */
    revert(): void {
        this.controls.forEach(control => {
            const value = this.getValue(control);
            const checked = this.valueIsChecked(value);
            control.patchValue({checked});
        });
    }

    getValue(control: AbstractControl): string {
        const item = control.value;
        return item.value;
    }

    isChecked(item: Item): boolean {
        return this.valueIsChecked(item.value);
    }

    protected abstract getItemFromValue(value: T): Item;

    protected buildFormGroup(): FormGroup {
        const formArray = new FormArray([]);
        this.items.forEach(item => {
            formArray.push(new FormGroup({
                name: new FormControl(item.name),
                checked: new FormControl(this.isChecked(item)),
                value: new FormControl(item.value)
            }));
        });
        return new FormGroup({
            [this.formGroupName]: new FormControl(null, Validators.required),
            [this.formArrayName]: formArray,
        });
    }

    protected getValueFromFormGroup(): string[] {
        return this.selectedItems
            .map(item => item.value);
    }

    protected get selectedItems(): Item[] {
        return this.formGroup.value[this.formArrayName]
            .filter(item => item.checked);
    }

    protected count(value: string[]): number {
        return value.length;
    }

    private valueIsChecked(value: string): boolean {
        return this.filter.contains(value);
    }

}
