import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {BreakpointObserverService, NgClassObject} from '../../core/services/breakpoint-observer.service';
import {MatPaginator, MatPaginatorIntl} from '@angular/material/paginator';

/**
 * Adaptation de mat-paginator pour un visuel identique à pagination-controls
 */
@Component({
    selector: 'app-paginator',
    templateUrl: './paginator.component.html',
    styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent extends MatPaginator {

    /**
     * Nombre maximum de pages affichées (boutons cliquables) dans les pagination-controls
     */
    @Input()
    maxSize = 9;

    constructor(
        intl: MatPaginatorIntl,
        changeDetectorRef: ChangeDetectorRef,
        private readonly breakpointObserver: BreakpointObserverService,
    ) {
        super(intl, changeDetectorRef);
    }

    /**
     * Nombre d'éléments affichés par page
     */
    get pageSize(): number {
        return super.pageSize;
    }

    /**
     * Nombre d'éléments affichés par page
     */
    @Input()
    set pageSize(value: number) {
        super.pageSize = value;
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('ngx-pagination');
    }

    handlePageChange(page: number): void {
        this.pageIndex = page - 1;
        this.refreshDataSource();
    }

    private refreshDataSource(): void {
        this._changePageSize(this.pageSize);
    }

}
