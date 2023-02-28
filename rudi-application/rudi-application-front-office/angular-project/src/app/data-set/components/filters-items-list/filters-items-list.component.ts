import {Component, Input} from '@angular/core';
import {Item} from '../filter-forms/array-filter-form.component';
import {DEBUT_NAME_PREFIX, FIN_NAME_PREFIX} from '../filter-forms/dates-filter-form/dates-filter-form.component';
import {FiltersService} from '../../../core/services/filters.service';
import {AccessStatusFilterItem} from '../filter-forms/access-status-filter-form/access-status-filter-form.component';

@Component({
    selector: 'app-filters-items-list',
    templateUrl: './filters-items-list.component.html',
    styleUrls: ['./filters-items-list.component.scss']
})
export class FiltersItemsListComponent {
    @Input() selectedDatesItems: Item[];
    @Input() selectedAccessStatusItems: AccessStatusFilterItem[];
    @Input() selectedProducerItems: Item[];
    @Input() selectedThemeItems: Item[];
    @Input() hasSelectedItems: boolean;

    constructor(
        public readonly filtersService: FiltersService
    ) {
    }

    /**
     * Filter not null value in selectedAccessStatusItems
     */
    get notNullSelectedAccessStatusItems(): AccessStatusFilterItem[] {
        return this.selectedAccessStatusItems = this.selectedAccessStatusItems.filter(selectedValue => selectedValue.value != null);
    }

    /**
     * Solution pour palier au cas particulier des filtres sur les status de restriction
     * A ameliorer plus tard, si d'autres filtres se rajoutent
     */
    hasSelectedAllOtherFiltre(): boolean {
        if (this.selectedThemeItems.length > 0) {
            return this.selectedThemeItems.some(value => value.value !== null);
        } else if (this.selectedProducerItems.length > 0) {
            return this.selectedProducerItems.some(value => value.value !== null);
        } else if (this.selectedDatesItems.length > 0) {
            return this.selectedDatesItems.some(value => value.value !== null);
        } else if (this.selectedAccessStatusItems.length > 0) {
            return this.selectedAccessStatusItems.some(value => value.value !== null);
        }
        return false;
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
        this.filtersService.accessStatusFilter.clear();
    }

    private deleteDateDebutFilter(): void {
        this.filtersService.datesFilter.dateDebut = null;
    }

    private deleteDateFinFilter(): void {
        this.filtersService.datesFilter.dateFin = null;
    }
}
