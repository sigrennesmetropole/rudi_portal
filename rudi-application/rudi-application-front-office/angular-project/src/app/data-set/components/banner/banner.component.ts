import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Item} from '../filter-forms/array-filter-form.component';
import {RestrictedAccessFilterItem} from '../filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {SimpleSkosConcept} from '../../../kos/kos-model';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';

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
    @Output() selectedRestrictedAccessItemsChange = new EventEmitter<RestrictedAccessFilterItem[]>();
    @Input() themes: SimpleSkosConcept[];
    @Input() producerNames: string[];
    @Input() restrictedAccessForcedValue: boolean;

    constructor(
    ) {
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

}
