import {Component, Input, OnInit} from '@angular/core';
import {Organization} from '../../../strukture/strukture-model';
import {BreakpointObserverService, NgClassObject} from '../../../core/services/breakpoint-observer.service';
import {switchMap} from 'rxjs/operators';
import {UserService} from '../../../core/services/user.service';
import {OrganizationMetierService} from '../../../core/services/organization-metier.service';


@Component({
    selector: 'app-organization-tab',
    templateUrl: './organization-tab.component.html',
    styleUrls: ['./organization-tab.component.scss']
})


export class OrganizationTabComponent implements OnInit {
    myOrganizations: Organization[];
    isLoading: boolean;
    page: number;
    itemsPerPage = 9;
    errorLoading: boolean;

    constructor(private readonly breakpointObserver: BreakpointObserverService,
                private readonly utilisateurService: UserService,
                private readonly organizationMetierService: OrganizationMetierService) {
    }

    get showOrganization(): boolean {
        return this.myOrganizations.length > 0;
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    ngOnInit(): void {
        this.getMyOrganisations();
    }

    /**
     * Méthode qui permet de récupérer les organisations du user connecté
     */
    public getMyOrganisations(): void {
        this.isLoading = true;
        this.utilisateurService.getConnectedUser()
            .pipe(
                switchMap(connectedUser => {
                    // Recupération des organisations de l'utilisateur
                    return this.organizationMetierService.getMyOrganizations(connectedUser?.uuid);
                }))
            .subscribe(
                {
                    next: (organzations: Organization[] | undefined) => {
                        this.myOrganizations = organzations;
                        this.isLoading = false;
                        this.errorLoading = false;
                    },
                    error: (e) => {
                        console.error(e);
                        this.isLoading = false;
                        this.errorLoading = true;

                    }
                }
            );
    }
}
