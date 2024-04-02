import {Component, OnDestroy, OnInit, Renderer2, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material/sidenav';
import {Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {FiltersService} from '@core/services/filters.service';
import {OrderValue} from '@core/services/filters/order-filter';
import {KonsultMetierService} from '@core/services/konsult-metier.service';
import {KosMetierService} from '@core/services/kos-metier.service';
import {ProvidersMetierService} from '@core/services/providers-metier.service';
import {SidenavOpeningsService} from '@core/services/sidenav-openings.service';
import {SimpleSkosConcept} from 'micro_service_modules/kos/kos-model';
import {of, Subject} from 'rxjs';
import {switchMap, takeUntil} from 'rxjs/operators';
import {AccessStatusFilterItem} from '../../components/filter-forms/access-status-filter-form/access-status-filter-form.component';
import {Item} from '../../components/filter-forms/array-filter-form.component';

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit, OnDestroy {
    @ViewChild('sidenav') sidenav: MatSidenav;
    accessStatusfilterForcedValue; // positionné à undefined en attendant le mode mobile de ../../components/list-container

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

    selectedAccessStatusFilterItems: AccessStatusFilterItem[] = [];
    private isDestroyed$ = new Subject<void>();

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly kosMetierService: KosMetierService,
        private readonly router: Router,
        private readonly filtersService: FiltersService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly providersMetierService: ProvidersMetierService,
        private readonly sidenavOpeningsService: SidenavOpeningsService,
        private readonly renderer: Renderer2
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
        this.renderer.removeClass(document.body, 'menu');
    }

    submitFilters(): void {
        this.sidenav.close();
    }

    deleteAllFilters(): void {
        this.filtersService.deleteAllFilters();
    }

}
