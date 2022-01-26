import {Component, Input} from '@angular/core';
import {RestrictedAccessFilterItem} from '../filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {Item} from '../filter-forms/array-filter-form.component';
import {DEBUT_NAME_PREFIX, FIN_NAME_PREFIX} from '../filter-forms/dates-filter-form/dates-filter-form.component';
import {FiltersService} from '../../../core/services/filters.service';

@Component({
    selector: 'app-filters-items-list',
    templateUrl: './filters-items-list.component.html',
    styleUrls: ['./filters-items-list.component.scss']
})
export class FiltersItemsListComponent {
    @Input() selectedDatesItems: Item[];
    @Input() selectedRestrictedAccessItems: RestrictedAccessFilterItem[];
    @Input() selectedProducerItems: Item[];
    @Input() selectedThemeItems: Item[];

    constructor(
        public readonly filtersService: FiltersService
    ) {
    }

    get hasSelectedItems(): boolean {
        return (
            this.selectedDatesItems?.length > 0 ||
            this.selectedRestrictedAccessItems?.length > 0 ||
            this.selectedProducerItems?.length > 0 ||
            this.selectedThemeItems?.length > 0
        );
    }

    deleteThemeFilter(theme: Item): void {
        this.filtersService.themesFilter.remove(theme.value);
    }

    deleteProducerFilter(producer: Item): void {
        this.filtersService.producerNamesFilter.remove(producer.name);
    }

    deleteDateFilter(date: Item): void {
        if (date.name.startsWith(DEBUT_NAME_PREFIX)) {
            this.deleteDateDebutFilter();
        } else if (date.name.startsWith(FIN_NAME_PREFIX)) {
            this.deleteDateFinFilter();
        }
    }

    deleteAllFilters(): void {
        this.filtersService.deleteAllFilters();
    }

    deleteRestrictedAccessFilter(): void {
        this.filtersService.restrictedAccessFilter.clear();
    }

    private deleteDateDebutFilter(): void {
        this.filtersService.datesFilter.dateDebut = null;
    }

    private deleteDateFinFilter(): void {
        this.filtersService.datesFilter.dateFin = null;
    }
}
