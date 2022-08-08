import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {of, Subject} from 'rxjs';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {OrderValue} from '../../../core/services/filters/order-filter';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {Router} from '@angular/router';
import {ProvidersMetierService} from '../../../core/services/providers-metier.service';
import {KosMetierService} from '../../../core/services/kos-metier.service';
import {SidenavOpeningsService} from '../../../core/services/sidenav-openings.service';
import {FiltersService} from '../../../core/services/filters.service';
import {MatSidenav} from '@angular/material/sidenav';
import {switchMap, takeUntil} from 'rxjs/operators';
import {SimpleSkosConcept} from '../../../kos/kos-model';
import {Item} from '../../components/filter-forms/array-filter-form.component';
import {RestrictedAccessFilterItem} from '../../components/filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit, OnDestroy {
    @ViewChild('sidenav') sidenav: MatSidenav;
    restrictedAccessForcedValue; // positionné à undefined en attendant le mode mobile de ../../components/list-container

    mediaSize: MediaSize;
    themes: SimpleSkosConcept[];
    orders: OrderValue[] = [
        'resource_title',
        '-resource_title',
        'dataset_dates.updated',
        '-dataset_dates.updated',
        'producer.organization_name',
        '-producer.organization_name',
    ];

    producerNames: string[];
    selectedDatesItems: Item[] = [];
    selectedThemeItems: Item[] = [];
    selectedProducerItems: Item[] = [];

    selectedRestrictedAccessItems: RestrictedAccessFilterItem[] = [];
    private isDestroyed$ = new Subject<void>();

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly kosMetierService: KosMetierService,
        private readonly router: Router,
        private readonly filtersService: FiltersService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly providersMetierService: ProvidersMetierService,
        private readonly sidenavOpeningsService: SidenavOpeningsService
    ) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
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
        });
    }

    ngOnDestroy(): void {
        this.isDestroyed$.next();
    }

    submitFilters(): void {
        this.sidenav.close();
    }

    deleteAllFilters(): void {
        this.filtersService.deleteAllFilters();
    }

}
