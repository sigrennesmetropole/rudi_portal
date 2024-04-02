import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {LogService} from '@core/services/log.service';
import {OrganizationMetierService} from '@core/services/organization/organization-metier.service';
import {UserService} from '@core/services/user.service';
import {User} from 'micro_service_modules/acl/acl-api';
import {Organization} from 'micro_service_modules/strukture/strukture-model';
import {switchMap, tap} from 'rxjs/operators';


@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html'
})
export class DetailComponent implements OnInit {

    public isLoading: boolean;
    public organization: Organization;
    public user: User;
    public _displayAdministrationTab: boolean;

    constructor(private readonly route: ActivatedRoute,
                private readonly organizationService: OrganizationMetierService,
                private readonly userService: UserService,
                private readonly logService: LogService) {
    }

    ngOnInit(): void {

        this.route.params.pipe(
            switchMap((params: Params) => {
                // Si uuid, on charge l'organization
                if (params.organizationUuid) {
                    this.isLoading = true;
                    return this.organizationService.getOrganizationByUuid(params.organizationUuid);
                } else {
                    // Sinon erreur on peut pas afficher la page
                    throw Error('Erreur pas d\'UUID de d\'organisation');
                }
            }),
            tap((organization: Organization) => this.isAdministrator(organization.uuid))
        ).subscribe(
            {
                next: (organization: Organization) => {
                    this.isLoading = false;
                    this.organization = organization;
                },
                error: err => {
                    this.logService.error(err);
                    this.isLoading = false;
                }
            }
        );
    }

    get displayAdministrationTab(): boolean {
        return this._displayAdministrationTab;
    }

    isAdministrator(organizationUuid: string): void {
        this.organizationService.isAdministrator(organizationUuid)
            .subscribe({
                next: (isAdministrator: boolean) => {
                    this._displayAdministrationTab = isAdministrator;
                },
                error: (error) => {
                    this.logService.error(error);
                }
            });
    }
}
