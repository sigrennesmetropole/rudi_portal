import {Component, OnInit} from '@angular/core';
import {FilterFormComponent} from '../filter-form.component';
import {FiltersService} from '../../../../core/services/filters.service';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestrictedAccessFilter} from '../../../../core/services/filters/restricted-access-filter';
import {Item} from '../array-filter-form.component';
import {TranslateService} from '@ngx-translate/core';

export interface RestrictedAccessFilterItem extends Item {
    name: string;
    value: boolean;
    checked: boolean;
}

const FALSE_KEY = 'filterBox.restrictedAccess.false';
const TRUE_KEY = 'filterBox.restrictedAccess.true';
const ALL_KEY = 'filterBox.restrictedAccess.all';

@Component({
    selector: 'app-restricted-access-filter-form',
    templateUrl: './restricted-access-filter-form.component.html',
    styleUrls: ['./restricted-access-filter-form.component.scss']
})
export class RestrictedAccessFilterFormComponent extends FilterFormComponent<boolean, RestrictedAccessFilter, RestrictedAccessFilterItem> implements OnInit {
    items: RestrictedAccessFilterItem[];
    readonly formControlName = 'formControl';

    constructor(
        protected readonly filtersService: FiltersService,
        private readonly translateService: TranslateService
    ) {
        super(filtersService);
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.translateService.get([FALSE_KEY, TRUE_KEY, ALL_KEY]).subscribe(translations => {
            this.items = [
                {
                    name: (translations[FALSE_KEY] as string),
                    value: false,
                    checked: false
                },
                {
                    name: (translations[TRUE_KEY] as string),
                    value: true,
                    checked: false
                },
                {
                    name: (translations[ALL_KEY] as string),
                    value: null,
                    checked: false
                }
            ];
            this.initFormGroup();
        });
    }

    protected buildFormGroup(): FormGroup {
        return new FormGroup({
            [this.formControlName]: new FormControl(this.filter.value, Validators.required),
        });
    }

    protected count(value: boolean): number {
        return (value || value === false) ? 1 : 0;
    }

    protected getFilterFrom(filtersService: FiltersService): RestrictedAccessFilter {
        return filtersService.restrictedAccessFilter;
    }

    protected getValueFromFormGroup(): boolean {
        return this.control.value;
    }

    protected get selectedItems(): RestrictedAccessFilterItem[] {
        const currentFilterValue: boolean = this.filter.value;
        return this.items.filter(item => item.value === currentFilterValue);
    }

    private get control(): AbstractControl {
        return this.formGroup.get(this.formControlName);
    }

    revert(): void {
        if (this.formGroup) {
            this.control.patchValue(this.filter.value);
        }
    }
}
