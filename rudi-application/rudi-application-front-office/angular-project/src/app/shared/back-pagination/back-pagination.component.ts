import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../core/services/breakpoint-observer.service';
import {BackPaginationSort} from './back-pagination-sort';
import {SortTableInterface} from './sort-table-interface';


const FIRST_PAGE = 1;

@Component({
    selector: 'app-back-pagination',
    templateUrl: './back-pagination.component.html',
    styleUrls: ['./back-pagination.component.scss']
})

export class BackPaginationComponent implements OnInit {


    @Input() total = 0;
    @Input() backPaginationSort = new BackPaginationSort();
    @Output()
    private loadData: EventEmitter<SortTableInterface> = new EventEmitter<SortTableInterface>();
    mediaSize: MediaSize;
    currentPage = FIRST_PAGE;
    readonly maxPageDesktop = 9;
    /** minimum = 5 */
    readonly maxPageMobile = 5;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    ngOnInit(): void {
    }

    /**
     * Fonction permettant la gestion la pagination
     */
    handlePageChange(page: number): void {
        this.page = page;
        this.load(this.backPaginationSort.currentSort, this.backPaginationSort.currentSortAsc, page);
        window.scroll(0, 0);
    }

    get page(): number {
        return this.currentPage;
    }

    set  page(value: number) {
        if (value < FIRST_PAGE) {
            console.warn('Page number cannot be less than ' + FIRST_PAGE);
            value = FIRST_PAGE;
        }
        this.currentPage = value;

    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    load(column: string, isAsc: boolean, page: number): void {
        this.loadData.emit(this.backPaginationSort.sort(column, isAsc, page));
    }
}
