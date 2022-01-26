import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {OrderValue} from '../../../core/services/filters/order-filter';
import {KonsultMetierService, MAX_RESULTS_PER_PAGE} from '../../../core/services/konsult-metier.service';
import {Metadata, MetadataList} from '../../../api-kaccess';
import {Router} from '@angular/router';
import {ProvidersMetierService} from '../../../core/services/providers-metier.service';
import {KosMetierService} from '../../../core/services/kos-metier.service';
import {Item} from '../../components/filter-forms/array-filter-form.component';
import {SidenavOpeningsService} from '../../../core/services/sidenav-openings.service';
import {FiltersService} from '../../../core/services/filters.service';
import {RestrictedAccessFilterItem} from '../../components/filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {MatSidenav} from '@angular/material/sidenav';
import {switchMap, takeUntil} from 'rxjs/operators';
import {SimpleSkosConcept} from '../../../kos/kos-model';


const FIRST_PAGE = 1;
const EMPTY_METADATA_LIST: MetadataList = {
    total: 0,
    items: []
};

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit, OnDestroy {
    @ViewChild('sidenav') sidenav: MatSidenav;
    readonly maxPageLg = 9;
    /** minimum = 5 */
    readonly maxPageSm = 5;

    offset = 0;
    limit = MAX_RESULTS_PER_PAGE;
    mediaSize: MediaSize;
    metadataList = EMPTY_METADATA_LIST;
    searchIsRunning = false;
    searche$: Observable<string>;
    orders: OrderValue[] = [
        'name', // solution temporaire pour le tri en renommant "title" à "name"
        '-name', // solution temporaire pour le tri en renommant "title" à "name"
    ];
    themes: SimpleSkosConcept[];
    producerNames: string[];

    selectedDatesItems: Item[] = [];
    selectedThemeItems: Item[] = [];
    selectedProducerItems: Item[] = [];
    selectedRestrictedAccessItems: RestrictedAccessFilterItem[] = [];

    private isDestroyed$ = new Subject<void>();
    private currentPage = FIRST_PAGE;
    private readonly themeLabelsByCode: { [key: string]: string } = {};
    private readonly themePictosByCode: { [key: string]: string } = {};

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly kosMetierService: KosMetierService,
        private readonly router: Router,
        private filtersService: FiltersService,
        public breakpointObserver: BreakpointObserverService,
        private readonly providersMetierService: ProvidersMetierService,
        private readonly sidenavOpeningsService: SidenavOpeningsService
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
        this.konsultMetierService.getThemeCodes().pipe(
            switchMap(themeCodes => themeCodes.length > 0 ? this.kosMetierService.getThemes(themeCodes) : of([]))
        ).subscribe(concepts => {
            this.themes = concepts;
            concepts.forEach(concept => {
                this.themeLabelsByCode[concept.concept_code] = concept.text;
                this.themePictosByCode[concept.concept_code] = KosMetierService
                    .getMiniAssetNameFromConceptIcon(concept.concept_icon);
            });
        });
    }

    openSidenav(): void {
        this.sidenavOpeningsService.openSidenav();
    }

    ngOnDestroy(): void {
        this.isDestroyed$.next();
    }

    /**
     * Fonction de permet de naviguer vers le detail d'un jdd
     */
    onClickOnRowDataSet(metaData: Metadata): void {
        if (metaData.global_id) {
            this.router.navigate(['/detail/' + metaData.global_id]);
        }
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
        this.searchIsRunning = true;
        this.konsultMetierService
            .searchMetadatas(MAX_RESULTS_PER_PAGE, this.offset, this.filtersService.currentFilters)
            .subscribe((data) => {
                    this.metadataList = data ? data : EMPTY_METADATA_LIST;
                },
                (error) => {
                    console.error('getMetadatas failed', error.message);
                },
                () => {
                    this.searchIsRunning = false;
                });
    }

    submitFilters(): void {
        this.sidenav.close();
    }

    deleteAllFilters(): void {
        this.filtersService.deleteAllFilters();
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeLabelsByCode[metadata.theme] || `[${metadata.theme}]`;
    }

    /**
     * @returns Example : 'weather'
     */
    getThemePictoFor(metadata: Metadata): string {
        return this.themePictosByCode[metadata.theme];
    }
}
