import {Component, EventEmitter, Input, Output} from '@angular/core';
import {OrganizationBean} from '@app/strukture/strukture-model';
import {BreakpointObserverService, NgClassObject} from '@core/services/breakpoint-observer.service';
import {$e} from 'codelyzer/angular/styles/chars';

@Component({
    selector: 'app-list-organization-card',
    templateUrl: './list-organization-card.component.html',
    styleUrls: ['./list-organization-card.component.scss']
})
export class ListOrganizationCardComponent {
    @Input() organizations: OrganizationBean[];
    @Input() totalItems: number;
    @Input() itemsPerPage: number;
    @Input() currentPage: number;

    @Output() pageChangeEvent: EventEmitter<number>;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService
    ) {
        this.organizations = [];
        this.totalItems = 0;
        this.itemsPerPage = 10;
        this.currentPage = 1;

        this.pageChangeEvent = new EventEmitter();
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    onPageChange($event: number): void {
        this.currentPage = $event;
        this.pageChangeEvent.emit($event);
    }
}
