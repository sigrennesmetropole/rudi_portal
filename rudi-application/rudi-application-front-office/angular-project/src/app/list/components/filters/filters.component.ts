import {Component, EventEmitter, Input, Output, ViewEncapsulation} from '@angular/core';
import {Item} from '../filter-forms/array-filter-form.component';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import {RestrictedAccessFilterItem} from '../filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {SimpleSkosConcept} from '../../../kos/kos-model';

@Component({
    selector: 'app-filters',
    templateUrl: './filters.component.html',
    styleUrls: ['./filters.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class FiltersComponent {

    @Input() mediaSize: MediaSize;
    @Output() selectedDatesItemsChange = new EventEmitter<Item[]>();
    @Output() selectedProducerItemsChange = new EventEmitter<Item[]>();
    @Output() selectedThemeItemsChange = new EventEmitter<Item[]>();
    @Output() selectedRestrictedAccessItemsChange = new EventEmitter<RestrictedAccessFilterItem[]>();
    @Input() themes: SimpleSkosConcept[];
    @Input() producerNames: string[];

}
