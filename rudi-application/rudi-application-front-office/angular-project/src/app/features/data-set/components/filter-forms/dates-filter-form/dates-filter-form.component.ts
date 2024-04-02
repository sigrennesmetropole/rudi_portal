import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FiltersService} from '@core/services/filters.service';
import {Dates, DatesFilter} from '@core/services/filters/dates-filter';
import * as moment from 'moment';
import {Observable} from 'rxjs';
import {Item} from '../array-filter-form.component';
import {FilterFormComponent} from '../filter-form.component';

export const DEBUT_NAME_PREFIX = 'Début';
export const FIN_NAME_PREFIX = 'Fin';

@Component({
    selector: 'app-dates-filter-form',
    templateUrl: './dates-filter-form.component.html',
    styleUrls: ['./dates-filter-form.component.scss']
})
export class DatesFilterFormComponent extends FilterFormComponent<Dates, DatesFilter, Item> {

    constructor(
        protected readonly filtersService: FiltersService
    ) {
        super(filtersService);
        this.dates$ = this.filter.value$;
        this.initFormGroup();
    }

    protected get selectedItems(): Item[] {
        const dates: Dates = this.getValueFromFormGroup();
        const selectedItems = [];
        if (dates.debut) {
            selectedItems.push(DatesFilterFormComponent.valueToItem(dates.debut, DEBUT_NAME_PREFIX));
        }
        if (dates.fin) {
            selectedItems.push(DatesFilterFormComponent.valueToItem(dates.fin, FIN_NAME_PREFIX));
        }
        return selectedItems;
    }

    @Input() debutDivClass?: string;
    @Input() finDivClass?: string;

    dates$: Observable<Dates>;

    private static valueToItem(value: string, label: string): Item {
        return {
            name: `${label} ${moment(value).format('DD-MM-YYYY')}`,
            value
        };
    }

    getFilterFrom(filtersService: FiltersService): DatesFilter {
        return filtersService.datesFilter;
    }

    revert(): void {
        this.formGroup.setValue({
            startDate: this.filter.value.debut,
            endDate: this.filter.value.fin
        });
    }

    protected count(value: Dates): number {
        const dates = [
            value.debut,
            value.fin
        ];
        return dates.map(date => date ? 1 : 0)
            .reduce((previousValue, currentValue) => previousValue + currentValue, 0);
    }

    protected buildFormGroup(): FormGroup {
        return new FormGroup(
            {
                startDate: new FormControl(),
                endDate: new FormControl()
            }
        );
    }

    protected getValueFromFormGroup(): Dates {
        const startDate = this.formGroup.get('startDate').value;
        const endDate = this.formGroup.get('endDate').value;
        return {
            debut: startDate ? moment(startDate).startOf('day').format() : null,
            fin: endDate ? moment(endDate).endOf('day').format() : null
        };
    }

}
