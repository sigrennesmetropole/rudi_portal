import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {FiltersService} from '@core/services/filters.service';
import {AccessStatusFilter} from '@core/services/filters/access-status-filter';
import {AccessStatusFiltersType} from '@core/services/filters/access-status-filters-type';
import {TranslateService} from '@ngx-translate/core';
import {Item} from '../array-filter-form.component';
import {FilterFormComponent} from '../filter-form.component';

export interface AccessStatusFilterItem extends Item {
    name: string;
    value: AccessStatusFiltersType;
    checked: boolean;
}

const OPENED_KEY = 'filterBox.restrictedAccess.false';
const RESTRICTED_KEY = 'filterBox.restrictedAccess.true';
const GDPR_SENSITIVE_KEY = 'filterBox.restrictedAccess.gdprSensitive';
const ALL_KEY = 'filterBox.restrictedAccess.all';

@Component({
    selector: 'app-access-status-filter-form',
    templateUrl: './access-status-filter-form.component.html',
    styleUrls: ['./access-status-filter-form.component.scss']
})
export class AccessStatusFilterFormComponent extends FilterFormComponent<AccessStatusFiltersType, AccessStatusFilter, AccessStatusFilterItem> implements OnInit {
    items: AccessStatusFilterItem[];
    readonly formControlName = 'formControl';

    constructor(
        protected readonly filtersService: FiltersService,
        private readonly translateService: TranslateService
    ) {
        super(filtersService);
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.translateService.get([OPENED_KEY, RESTRICTED_KEY, GDPR_SENSITIVE_KEY, ALL_KEY]).subscribe(translations => {
            this.items = [
                {
                    name: (translations[OPENED_KEY] as string),
                    value: AccessStatusFiltersType.Opened,
                    checked: false
                },
                {
                    name: (translations[RESTRICTED_KEY] as string),
                    value: AccessStatusFiltersType.Restricted,
                    checked: false
                },
                {
                    name: (translations[GDPR_SENSITIVE_KEY] as string),
                    value: AccessStatusFiltersType.GdprSensitive,
                    checked: false
                },
                {
                    name: (translations[ALL_KEY] as string),
                    value: null,
                    checked: false
                }
            ].filter(this.filterItemsWithoutHiddenValuesPredicate);
            this.initFormGroup();
        });
    }

    protected buildFormGroup(): FormGroup {
        return new FormGroup({
            [this.formControlName]: new FormControl(this.filter.value, Validators.required),
        });
    }

    protected count(value: AccessStatusFiltersType): number {
        return (value) ? 1 : 0;
    }

    protected getFilterFrom(filtersService: FiltersService): AccessStatusFilter {
        return filtersService.accessStatusFilter;
    }

    protected getValueFromFormGroup(): AccessStatusFiltersType {
        return this.control.value;
    }

    protected get selectedItems(): AccessStatusFilterItem[] {
        const currentFilterValue = this.filter.value;
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
