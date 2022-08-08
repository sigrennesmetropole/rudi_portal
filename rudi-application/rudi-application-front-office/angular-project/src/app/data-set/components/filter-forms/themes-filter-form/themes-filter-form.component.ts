import {Component} from '@angular/core';
import {ArrayFilterFormComponent, Item} from '../array-filter-form.component';
import {FiltersService} from '../../../../core/services/filters.service';
import {ArrayFilter} from '../../../../core/services/filters/array-filter';
import {SimpleSkosConcept} from '../../../../kos/kos-model';

@Component({
    selector: 'app-themes-filter-form',
    templateUrl: './themes-filter-form.component.html',
    styleUrls: ['./themes-filter-form.component.scss']
})
export class ThemesFilterFormComponent extends ArrayFilterFormComponent<SimpleSkosConcept> {

    constructor(filtersService: FiltersService) {
        super(filtersService);
    }

    get formArrayName(): string {
        return 'themes';
    }

    protected get formGroupName(): string {
        return 'thematic';
    }

    protected getItemFromValue(simpleSkosConcept: SimpleSkosConcept): Item {
        return {
            name: simpleSkosConcept.text,
            value: simpleSkosConcept.concept_code
        };
    }

    protected getFilterFrom(filtersService: FiltersService): ArrayFilter {
        return filtersService.themesFilter;
    }

}
