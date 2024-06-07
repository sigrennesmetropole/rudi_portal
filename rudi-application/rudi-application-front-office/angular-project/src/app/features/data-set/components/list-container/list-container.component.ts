import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {FiltersService} from '@core/services/filters.service';
import {OrderValue} from '@core/services/filters/order-filter';
import {KonsultMetierService, MAX_RESULTS_PER_PAGE} from '@core/services/konsult-metier.service';
import {SidenavOpeningsService} from '@core/services/sidenav-openings.service';
import {ThemeCacheService} from '@core/services/theme-cache.service';
import {TranslateService} from '@ngx-translate/core';
import {Metadata, MetadataList} from 'micro_service_modules/api-kaccess';
import {SimpleSkosConcept} from 'micro_service_modules/kos/kos-model';
import {Observable, Subject, Subscription} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {AccessStatusFilterItem} from '../filter-forms/access-status-filter-form/access-status-filter-form.component';
import {Item} from '../filter-forms/array-filter-form.component';

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
    @Input() orders: OrderValue[];
    @Input() mediaSize: MediaSize;
    @Input() hidePagination = false;
    @Input() limit = MAX_RESULTS_PER_PAGE;
    @Input() themes: SimpleSkosConcept[];
    /**
     * Set fixed number of cards to be displayed in a row.
     * maximum = 12 (current limit in SCSS rule : .data-set-container-*-cards).
     * Default : automatic (based on screen width).
     */
    @Input() accessStatusForcedValue;
    @Input() accessStatusHiddenValues;
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
    metadataListTotal: number;
    private filtersServiceSubscription?: Subscription;
    producerNames: string[];
    private isDestroyed$: Subject<void> = new Subject<void>();
    selectedDatesItems: Item[] = [];
    selectedThemeItems: Item[] = [];
    selectedProducerItems: Item[] = [];
    selectedAccessStatusItems: AccessStatusFilterItem[] = [];


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

    get hasSelectedItems(): boolean {
        return (
            this.selectedDatesItems?.length > 0 ||
            this.selectedAccessStatusItems?.length > 0 ||
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
        this.filtersServiceSubscription.unsubscribe();
        this.isDestroyed$.next();
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.sidenavOpeningsService.sideNavOpening$.pipe(takeUntil(this.isDestroyed$)).subscribe(() => {
            this.sidenav?.open();
        });
        this.konsultMetierService.getProducerNames().subscribe(
            producerNames => this.producerNames = producerNames
        );
        this.filtersServiceSubscription = this.filtersService.searchFilter.value$.subscribe();
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeCacheService.getThemeLabelFor(metadata);
    }

    getMetadataListTotal($event: number): void {
        this.metadataListTotal = $event;
    }

    onChanges(search: string): void {
        this.filtersService.searchFilter.value = search;
    }
}
