import {Component, OnDestroy, OnInit} from '@angular/core';
import {OrganizationBean} from 'micro_service_modules/strukture/api-strukture';
import {BreakpointObserverService, NgClassObject} from '@core/services/breakpoint-observer.service';
import {OrganizationMetierService} from '@core/services/organization/organization-metier.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {UserService} from '@core/services/user.service';
import {SearchOrganizationsService} from '@shared/list-organization-card/search-organizations.service';
import {Observable} from 'rxjs';


@Component({
    selector: 'app-organization-tab',
    templateUrl: './organization-tab.component.html',
    styleUrls: ['./organization-tab.component.scss']
})


export class OrganizationTabComponent implements OnInit, OnDestroy {
    isLoading: boolean;
    page: number;
    errorLoading: boolean;
    itemsPerPage: number;

    organizations$: Observable<OrganizationBean[]>;
    totalOrganizations$: Observable<number>;
    projectCountLoading$: Observable<boolean>;
    datasetCountLoading$: Observable<boolean>;

    constructor(private readonly breakpointObserver: BreakpointObserverService,
                private readonly utilisateurService: UserService,
                private readonly organizationMetierService: OrganizationMetierService,
                private readonly propertiesMetierService: PropertiesMetierService,
                private readonly searchOrganizationsService: SearchOrganizationsService
    ) {
        this.itemsPerPage = 9;
        this.organizations$ = searchOrganizationsService.organizations$;
        this.totalOrganizations$ = searchOrganizationsService.totalOrganizations$;
        this.datasetCountLoading$ = searchOrganizationsService.datasetCountLoading;
        this.projectCountLoading$ = searchOrganizationsService.projectsCountLoading;
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    onPageChange(page: number): void {
        this.searchOrganizationsService.currentPage$.next(page);
    }

    ngOnInit(): void {
        this.isLoading = true;
        this.getMyOrganisations();
    }

    ngOnDestroy(): void {
        this.searchOrganizationsService.complete();
    }

    /**
     * Méthode qui permet de récupérer les organisations du user connecté
     */
    public getMyOrganisations(): void {
        this.utilisateurService.getConnectedUser()
            .subscribe(
                (user) => {
                    this.searchOrganizationsService.initSubscriptions(user?.uuid, this.itemsPerPage);
                    this.isLoading = false;
                    this.errorLoading = false;
                },
                (e) => {
                    console.error(e);
                    this.isLoading = false;
                    this.errorLoading = true;
                }
            );
    }

    /**
     * Quand l'utilisateur click sur le lien equipe technique Rudi
     */
    handleClickContactRudi(): void {
        this.propertiesMetierService.get('rudidatarennes.contact').subscribe(link => {
            window.location.href = link;
        });
    }
}
