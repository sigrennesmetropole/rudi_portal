import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SafeResourceUrl} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize, NgClassObject} from '@core/services/breakpoint-observer.service';
import {FiltersService} from '@core/services/filters.service';
import {KonsultMetierService, MAX_RESULTS_PER_PAGE} from '@core/services/konsult-metier.service';
import {LogService} from '@core/services/log.service';
import {ThemeCacheService} from '@core/services/theme-cache.service';
import {Metadata, MetadataList} from 'micro_service_modules/api-kaccess';
import {BehaviorSubject, Subject} from 'rxjs';
import {debounceTime, takeUntil, tap} from 'rxjs/operators';

const FIRST_PAGE = 1;
const EMPTY_METADATA_LIST: MetadataList = {
    total: 0,
    items: []
};

@Component({
    selector: 'app-dataset-list',
    templateUrl: './dataset-list.component.html',
    styleUrls: ['./dataset-list.component.scss']
})
export class DatasetListComponent implements OnInit {
    // Indique si on affiche le loader pendant le chargement es JDD
    public isLoading = false;
    metadataList = EMPTY_METADATA_LIST;
    private currentPage = FIRST_PAGE;
    offset = 0;
    readonly maxPageDesktop = 9;
    /** minimum = 5 */
    readonly maxPageMobile = 5;
    private isDestroyed$ = new Subject<void>();
    @Input() producerUuid?: string;
    @Input() limit = MAX_RESULTS_PER_PAGE;
    @Input() mediaSize: MediaSize;
    @Input() resultsPerRow: number | undefined;
    /** On peut sélectionner une carte dans la liste ? */
    @Input() isSelectable = false;
    @Input() accessStatusHiddenValues;
    @Output() selectMetadata = new EventEmitter<Metadata>();
    @Output() dbSelectMetadata = new EventEmitter<Metadata>();
    @Output() metadataListTotal = new EventEmitter<number>();

    metadatasSearcher = new BehaviorSubject<null>(null);

    constructor(private readonly themeCacheService: ThemeCacheService,
                private readonly breakpointObserver: BreakpointObserverService,
                private readonly konsultMetierService: KonsultMetierService,
                private readonly filtersService: FiltersService,
                private readonly logService: LogService
    ) {
        themeCacheService.init();
    }

    ngOnInit(): void {
        this.filtersService.currentFilters.producerUuids = [this.producerUuid];
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.filtersService.filter$.pipe(takeUntil(this.isDestroyed$)).subscribe(() => {
            this.page = FIRST_PAGE;
        });

        // Dans une liste de JDD on ne peut faire une recherche que toutes les demi secondes
        this.metadatasSearcher.pipe(
            debounceTime(500),
            tap(() => this.searchMetadatas())
        ).subscribe();

        // Déclenchement de la recherche à l'arrivée sur le composant
        this.metadatasSearcher.next(null);
    }

    /**
     * @return ne renvoie jamais null (obligatoire pour le pipe paginate)
     */
    get metadataListItems(): Metadata[] {
        return this.metadataList.items ?? [];
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

        this.metadatasSearcher.next(null);
    }

    ngOnDestroy(): void {
        this.isDestroyed$.next();
    }

    /**
     * @returns Example : 'weather'
     */
    getThemePictoFor(metadata: Metadata): SafeResourceUrl {
        return this.themeCacheService.getThemePictoFor(metadata);
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeCacheService.getThemeLabelFor(metadata);
    }

    /**
     * Appel du service
     */
    searchMetadatas(): void {
        this.isLoading = true;
        this.konsultMetierService
            .searchMetadatas(this.filtersService.currentFilters, this.accessStatusHiddenValues, this.offset, this.limit)
            .subscribe((data) => {
                    this.metadataList = data ?? EMPTY_METADATA_LIST;
                    this.metadataListTotal.emit(data.total);
                    this.isLoading = false;
                },
                (error) => {
                    this.isLoading = false;
                    this.logService.error('getMetadatas failed', error.message);
                });
    }

    /**
     * Fonction permettant la gestion la pagination
     */
    handlePageChange(page: number): void {
        this.page = page;
        window.scroll(0, 0);
    }
}
