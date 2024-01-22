import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Order} from '@app/organization/components/order/type';
import {OrganizationBean} from '@app/strukture/api-strukture';
import {MediaSize} from '@core/services/breakpoint-observer.service';
import {searchDefaultPageSize, SearchOrganizationsService} from '@shared/list-organization-card/search-organizations.service';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-list-container-organization',
    templateUrl: './list-container.component.html',
    styleUrls: ['./list-container.component.scss']
})
export class ListContainerComponent implements OnInit, OnDestroy {

    itemsPerPage: number;
    organizations$: Observable<OrganizationBean[]>;
    totalOrganizations$: Observable<number>;
    datasetCountLoading$: Observable<boolean>;
    projectCountLoading$: Observable<boolean>;
    currentPage$: Observable<number>;

    @Input() mediaSize: MediaSize;

    constructor(
        private searchOrganizationsService: SearchOrganizationsService,
    ) {
        this.itemsPerPage = searchDefaultPageSize;
        this.organizations$ = searchOrganizationsService.organizations$;
        this.totalOrganizations$ = searchOrganizationsService.totalOrganizations$;
        this.datasetCountLoading$ = searchOrganizationsService.datasetCountLoading;
        this.projectCountLoading$ = searchOrganizationsService.projectsCountLoading;
        this.currentPage$ = searchOrganizationsService.currentPage$;
    }

    onChangesSearchTerms($event: string): void {
        throw new Error('Search not implemented yet');
    }

    onOrderChange($event: Order): void {
        this.searchOrganizationsService.currentSortOrder$.next($event);
    }

    onPageChange(page: number): void {
        this.searchOrganizationsService.currentPage$.next(page);
    }

    ngOnDestroy(): void {
        this.searchOrganizationsService.complete();
    }

    ngOnInit(): void {
        this.searchOrganizationsService.initSubscriptions();
    }
}
