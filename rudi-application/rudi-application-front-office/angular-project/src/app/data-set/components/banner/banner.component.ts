import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Item} from '../filter-forms/array-filter-form.component';
import {SimpleSkosConcept} from '../../../kos/kos-model';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import {AccessStatusFilterItem} from '../filter-forms/access-status-filter-form/access-status-filter-form.component';
import {AccessStatusFiltersType} from '../../../core/services/filters/access-status-filters-type';

@Component({
    selector: 'app-banner',
    templateUrl: './banner.component.html',
    styleUrls: ['./banner.component.scss'],
})
export class BannerComponent implements OnInit, OnDestroy {
    @Input() mediaSize: MediaSize;
    @Output() selectedDatesItemsChange = new EventEmitter<Item[]>();
    @Output() selectedProducerItemsChange = new EventEmitter<Item[]>();
    @Output() selectedThemeItemsChange = new EventEmitter<Item[]>();
    @Output() selectedAccessStatusItemsChange = new EventEmitter<AccessStatusFilterItem[]>();
    @Input() themes: SimpleSkosConcept[];
    @Input() producerNames: string[];
    @Input() accessStatusForcedValue: AccessStatusFiltersType;
    @Input() accessStatusHiddenValues: AccessStatusFiltersType[];

    constructor() {
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

}
