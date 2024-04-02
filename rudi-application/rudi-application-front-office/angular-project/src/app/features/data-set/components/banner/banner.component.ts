import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MediaSize} from '@core/services/breakpoint-observer.service';
import {AccessStatusFiltersType} from '@core/services/filters/access-status-filters-type';
import {SimpleSkosConcept} from 'micro_service_modules/kos/kos-model';
import {AccessStatusFilterItem} from '../filter-forms/access-status-filter-form/access-status-filter-form.component';
import {Item} from '../filter-forms/array-filter-form.component';

@Component({
    selector: 'app-banner',
    templateUrl: './banner.component.html',
    styleUrls: ['./banner.component.scss'],
})
export class BannerComponent {
    @Input() mediaSize: MediaSize;
    @Output() selectedDatesItemsChange = new EventEmitter<Item[]>();
    @Output() selectedProducerItemsChange = new EventEmitter<Item[]>();
    @Output() selectedThemeItemsChange = new EventEmitter<Item[]>();
    @Output() selectedAccessStatusItemsChange = new EventEmitter<AccessStatusFilterItem[]>();
    @Input() themes: SimpleSkosConcept[];
    @Input() producerNames: string[];
    @Input() accessStatusForcedValue: AccessStatusFiltersType;
    @Input() accessStatusHiddenValues: AccessStatusFiltersType[];
}
