import {Component, Input} from '@angular/core';
import {DEFAULT_PROJECT_ORDER} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {FiltersService} from '@core/services/filters.service';
import {Organization} from 'micro_service_modules/strukture/strukture-model';


@Component({
    selector: 'app-organization-informations',
    templateUrl: './organization-informations.component.html',
    styleUrls: ['./organization-informations.component.scss']
})
export class OrganizationInformationsComponent {
    @Input() isLoading: boolean;
    @Input() organization: Organization;
    limit = 9;
    mediaSize: MediaSize;
    metadataListTotal: number;
    searchIsRunning = true;
    projectListTotal = 0;
    order = DEFAULT_PROJECT_ORDER;
    reuseListTotal: number;


    constructor(private readonly filtersService: FiltersService,
                private readonly breakpointObserver: BreakpointObserverService,
    ) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    setMetadataListTotal($event: number): void {
        this.metadataListTotal = $event;
    }

    setReuseListTotal($event: number): void {
        this.reuseListTotal = $event;
    }
}
