import {Component} from '@angular/core';
import {FiltersService} from '@core/services/filters.service';
import {ArrayFilter} from '@core/services/filters/array-filter';
import {ArrayFilterFormComponent, Item} from '../array-filter-form.component';

@Component({
    selector: 'app-producer-names-filter-form',
    templateUrl: './producer-names-filter-form.component.html',
    styleUrls: ['./producer-names-filter-form.component.scss']
})
export class ProducerNamesFilterFormComponent extends ArrayFilterFormComponent<string> {

    constructor(
        protected readonly filtersService: FiltersService
    ) {
        super(filtersService);
    }

    get formArrayName(): string {
        return 'prods';
    }

    protected get formGroupName(): string {
        return 'producer';
    }

    protected getItemFromValue(value: string): Item {
        return {
            name: value,
            value
        };
    }

    protected getFilterFrom(filtersService: FiltersService): ArrayFilter {
        return filtersService.producerNamesFilter;
    }

}
