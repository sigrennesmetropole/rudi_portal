import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {KonsultMetierService, MAX_RESULTS_PER_PAGE} from '../../../core/services/konsult-metier.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../../core/services/breakpoint-observer.service';
import {Observable, Subject} from 'rxjs';
import {OrderValue} from '../../../core/services/filters/order-filter';
import {SimpleSkosConcept} from '../../../kos/kos-model';
import {Item} from '../filter-forms/array-filter-form.component';
import {RestrictedAccessFilterItem} from '../filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {Metadata, MetadataList} from '../../../api-kaccess';
import {FiltersService} from '../../../core/services/filters.service';
import {SidenavOpeningsService} from '../../../core/services/sidenav-openings.service';
import {ThemeCacheService} from '../../../core/services/theme-cache.service';
import {takeUntil} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {Router} from '@angular/router';

const FIRST_PAGE = 1;
const EMPTY_METADATA_LIST: MetadataList = {
    total: 0,
    items: []
};

@Component({
    selector: 'app-list-container',
    templateUrl: './list-container.component.html',
    styleUrls: ['./list-container.component.scss']
})
export class ListContainerComponent implements OnInit, OnDestroy {
    @ViewChild('sidenav') sidenav: MatSidenav;
    readonly maxPageDesktop = 9;
    /** minimum = 5 */
    readonly maxPageMobile = 5;
    @Input() orders: OrderValue[];
    @Input() mediaSize: MediaSize;
    @Input() hidePagination = false;
    @Input() limit = MAX_RESULTS_PER_PAGE;
    /**
     * Set fixed number of cards to be displayed in a row.
     * maximum = 12 (current limit in SCSS rule : .data-set-container-*-cards).
     * Default : automatic (based on screen width).
     */
    @Input() resultsPerRow: number|undefined;
    @Input() restrictedAccessForcedValue;
    /** On peut sélectionner une carte dans la liste ? */
    @Input() isSelectable = false;
    @Output() selectMetadata = new EventEmitter<Metadata>();
    @Output() dbSelectMetadata = new EventEmitter<Metadata>();
    offset = 0;
    // Indique si on affiche le loader pendant le chargement es JDD
    public isLoading = false;
    metadataList = EMPTY_METADATA_LIST;
    searchIsRunning = false;
    searche$: Observable<string>;
    get themes(): SimpleSkosConcept[] {
        return this.themeCacheService.themes;
    }

    producerNames: string[];
    selectedDatesItems: Item[] = [];
    selectedThemeItems: Item[] = [];
    selectedProducerItems: Item[] = [];

    selectedRestrictedAccessItems: RestrictedAccessFilterItem[] = [];
    private isDestroyed$ = new Subject<void>();
    private currentPage = FIRST_PAGE;

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly router: Router,
        private readonly translateService: TranslateService,
        private readonly filtersService: FiltersService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly sidenavOpeningsService: SidenavOpeningsService,
        private readonly themeCacheService: ThemeCacheService,
    ) {
        this.searche$ = this.filtersService.searchFilter.value$;
    }

    get page(): number {
        return this.currentPage;
    }

    set page(value: number) {
        if (value < FIRST_PAGE) {
            console.warn('Page number cannot be less than ' + FIRST_PAGE);
            value = FIRST_PAGE;
        }
        this.currentPage = value;
        this.offset = (this.currentPage - 1) * this.limit;

        this.searchMetadatas();
    }

    get isFiltered(): boolean {
        return this.filtersService.isFiltered;
    }
    get hasSelectedItems(): boolean {
        return (
            this.selectedDatesItems?.length > 0 ||
            this.selectedRestrictedAccessItems?.length > 0 ||
            this.selectedProducerItems?.length > 0 ||
            this.selectedThemeItems?.length > 0
        );
    }

    /**
     * @return ne renvoie jamais null (obligatoire pour le pipe paginate)
     */
    get metadataListItems(): Metadata[] {
        return this.metadataList.items ?? [];
    }

    openSidenav(): void {
        this.sidenavOpeningsService.openSidenav();
    }

    ngOnDestroy(): void {
        this.isDestroyed$.next();
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.filtersService.filter$.pipe(takeUntil(this.isDestroyed$)).subscribe(() => {
            this.page = FIRST_PAGE;
        });
        this.sidenavOpeningsService.sideNavOpening$.pipe(takeUntil(this.isDestroyed$)).subscribe(() => {
            this.sidenav.open();
        });
        this.konsultMetierService.getProducerNames().subscribe(
            producerNames => this.producerNames = producerNames
        );
    }

    /**
     * Fonction permettant la gestion la pagination
     */
    handlePageChange(page: number): void {
        this.page = page;
        window.scroll(0, 0);
    }


    /**
     * Appel du service
     */
    searchMetadatas(): void {
        this.isLoading = true;
        this.searchIsRunning = true;
        this.konsultMetierService
            .searchMetadatas(this.filtersService.currentFilters, this.offset, this.limit)
            .subscribe((data) => {
                    this.metadataList = data ?? EMPTY_METADATA_LIST;
                },
                (error) => {
                    this.isLoading = false;
                    console.error('getMetadatas failed', error.message);
                },
                () => {
                    this.isLoading = false;
                    this.searchIsRunning = false;
                });
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeCacheService.getThemeLabelFor(metadata);
    }

    /**
     * @returns Example : 'weather'
     */
    getThemePictoFor(metadata: Metadata): string {
        return this.themeCacheService.getThemePictoFor(metadata);
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

}
