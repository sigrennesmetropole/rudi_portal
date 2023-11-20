import {Injectable} from '@angular/core';
import {Order} from '@app/organization/components/order/type';
import {OrganizationBean, OrganizationService, PagedOrganizationBeanList} from '@app/strukture/api-strukture';
import {BehaviorSubject, Subscription} from 'rxjs';
import {map, tap} from 'rxjs/operators';

export interface SearchOrganisationsRequest {
    userUuid?: string;
    offset?: number;
    itemPerPage?: number;
    sortOrder?: Order;
}

export const searchDefaultPageSize: number = 12;
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

    constructor(
        private organizationService: OrganizationService,
    ) {
        this.currentRequest = {
            itemPerPage: searchDefaultPageSize
        };

        this.currentPage$ = new BehaviorSubject(1);
        this.currentSortOrder$ = new BehaviorSubject(searchDefaultOrder);
        this.totalOrganizations$ = new BehaviorSubject(0);
        this.organizations$ = new BehaviorSubject([]);
    }

    initSubscriptions(userUuid?: string, itemPerPage?: number) {
        this.currentRequest.userUuid = null;
        this.currentRequest.itemPerPage = searchDefaultPageSize;
        if(userUuid){
            this.currentRequest.userUuid = userUuid;
        }
        if(itemPerPage){
            this.currentRequest.itemPerPage = itemPerPage;
        }
        this.subscription = new Subscription();
        this.subscription
            .add(this.initCurrentPageSubscription())
            .add(this.initCurrentSortOrderSubscription())
        ;
    }

    complete(): void {
        this.subscription.unsubscribe();
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

    private searchOrganisations(searchRequest: SearchOrganisationsRequest): void {
        this.organizationService.searchOrganizationsBeans(
            searchRequest.userUuid,
            searchRequest.offset,
            searchRequest.itemPerPage,
            searchRequest.sortOrder
        ).subscribe((data: PagedOrganizationBeanList) => {
            this.totalOrganizations$.next(data.total);
            this.organizations$.next(data.elements);
        });
    }
}
