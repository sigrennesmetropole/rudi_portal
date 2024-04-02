import {Injectable} from '@angular/core';
import {Order} from '@features/organization/components/order/type';
import {MetadataFacets} from 'micro_service_modules/api-kaccess';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {ProjectByOwner} from 'micro_service_modules/projekt/projekt-model';
import {OrganizationBean, OrganizationService, PagedOrganizationBeanList} from 'micro_service_modules/strukture/api-strukture';
import {BehaviorSubject, Subscription} from 'rxjs';
import {map, tap} from 'rxjs/operators';

export interface SearchOrganisationsRequest {
    userUuid?: string;
    offset?: number;
    itemPerPage?: number;
    sortOrder?: Order;
}

export const searchDefaultPageSize = 12;
const searchDefaultOrder: Order = '-openingDate';

@Injectable({
    providedIn: 'root'
})
export class SearchOrganizationsService {
    private subscription: Subscription;
    private currentRequest: SearchOrganisationsRequest;

    currentPage$: BehaviorSubject<number>;
    currentSortOrder$: BehaviorSubject<Order>;
    totalOrganizations$: BehaviorSubject<number>;
    organizations$: BehaviorSubject<OrganizationBean[]>;
    datasetCountLoading: BehaviorSubject<boolean>;
    projectsCountLoading: BehaviorSubject<boolean>;

    constructor(
        private organizationService: OrganizationService,
        private readonly projektService: ProjektService,
        private readonly konsultService: KonsultService
    ) {
        this.currentRequest = {
            itemPerPage: searchDefaultPageSize
        };

        this.currentPage$ = new BehaviorSubject(1);
        this.currentSortOrder$ = new BehaviorSubject(searchDefaultOrder);
        this.totalOrganizations$ = new BehaviorSubject(0);
        this.organizations$ = new BehaviorSubject([]);
        this.datasetCountLoading = new BehaviorSubject(false);
        this.projectsCountLoading = new BehaviorSubject(false);
    }

    initSubscriptions(userUuid?: string, itemPerPage?: number) {
        this.currentRequest.userUuid = null;
        this.currentRequest.itemPerPage = searchDefaultPageSize;
        if (userUuid) {
            this.currentRequest.userUuid = userUuid;
        }
        if (itemPerPage) {
            this.currentRequest.itemPerPage = itemPerPage;
        }
        this.subscription = new Subscription();
        this.subscription.add(this.initCurrentPageSubscription());
        this.subscription.add(this.initCurrentSortOrderSubscription());
    }

    complete(): void {
        this.subscription.unsubscribe();
    }

    private initCurrentSortOrderSubscription(): Subscription {
        return this.currentSortOrder$
            .pipe(
                tap((sortOrder: Order) => this.currentRequest.sortOrder = sortOrder),
                map((sortOrder: Order): SearchOrganisationsRequest => ({
                    ...this.currentRequest,
                    sortOrder
                }))
            )
            .subscribe((request: SearchOrganisationsRequest) => this.searchOrganisations(request));
    }

    private initCurrentPageSubscription(): Subscription {
        return this.currentPage$
            .pipe(
                tap((page: number) => this.currentRequest.offset = (page - 1) * this.currentRequest.itemPerPage),
                map((page: number): SearchOrganisationsRequest => ({
                    ...this.currentRequest,
                    offset: (page - 1) * this.currentRequest.itemPerPage
                }))
            )
            .subscribe((request: SearchOrganisationsRequest) => this.searchOrganisations(request));
    }


    private searchOrganisations(searchRequest: SearchOrganisationsRequest): void {
        this.organizationService.searchOrganizationsBeans(
            searchRequest.userUuid,
            searchRequest.offset,
            searchRequest.itemPerPage,
            searchRequest.sortOrder
        ).subscribe((data: PagedOrganizationBeanList) => {
            this.totalOrganizations$.next(data.total);
            this.organizations$.next(data.elements);

            this.updateOrganizationsProjectCount();
            this.updateOrganizationDatasetCount();
        });
    }

    private updateOrganizationsProjectCount(): void {
        this.projectsCountLoading.next(true);
        const organizations: OrganizationBean[] = [...this.organizations$.value];

        this.projektService.getNumberOfProjectsPerOwners({
            owner_uuids: organizations.map((e: OrganizationBean) => e.uuid)
        }).subscribe((projectByOwners: ProjectByOwner[]): void => {

            projectByOwners.forEach((projectByOwner: ProjectByOwner) => {
                const orga = organizations.find(organisation => organisation.uuid === projectByOwner.ownerUUID);
                if (!!orga) {
                    orga.projectCount = projectByOwner.projectCount;
                }
            });

            this.projectsCountLoading.next(false);
            this.organizations$.next(organizations);
        });
    }

    private updateOrganizationDatasetCount(): void {
        this.datasetCountLoading.next(true);
        const organizations: OrganizationBean[] = [...this.organizations$.value];

        this.konsultService.searchMetadataFacets(['producer_organization_id']).subscribe(
            (metadataFacets: MetadataFacets) => {
                const targetedFacet = metadataFacets.items.find(i => i.propertyName === 'producer_organization_id');
                organizations.forEach((organization: OrganizationBean) => {
                    const datasetCount = targetedFacet.values.find(value => value.value == organization.uuid);
                    if (!!datasetCount) {
                        organization.datasetCount = datasetCount.count;
                    }
                });
                this.datasetCountLoading.next(false);
                this.organizations$.next(organizations);
            });
    }
}
