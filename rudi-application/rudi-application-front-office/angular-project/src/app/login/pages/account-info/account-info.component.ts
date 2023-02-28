import {Component, OnInit} from '@angular/core';
import {UserService} from '../../../core/services/user.service';
import {TranslateService} from '@ngx-translate/core';
import {BreakpointObserverService} from '../../../core/services/breakpoint-observer.service';
import {OrganizationMetierService} from '../../../core/services/organization-metier.service';
import {switchMap} from 'rxjs/operators';
import {Organization} from '../../../strukture/strukture-model';
import {User} from '../../../acl/acl-model';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
    selector: 'app-account-info',
    templateUrl: './account-info.component.html',
    styleUrls: ['./account-info.component.scss']
})
export class AccountInfoComponent implements OnInit {
    public user: User | undefined;
    myOrganizations: Organization[];
    email: string;
    mediaSize: any;
    isLoading: boolean;
    urlToDoc: string = 'https://doc.rudi.bzh';

    constructor(
        private readonly translateService: TranslateService,
        private readonly utilisateurService: UserService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly organizationMetierService: OrganizationMetierService,
        private readonly propertiesMetierService: PropertiesMetierService
    ) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    ngOnInit(): void {
        // Recupère l'url de la doc depuis le back-end
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (rudiDocLink: string) => {
                this.urlToDoc = rudiDocLink;
            }
        });
        this.isLoading = true;
        // récupération de l'évènement d'authentification
        this.utilisateurService.getConnectedUser()
            .pipe(
                switchMap(connectedUser => {
                    this.user = connectedUser;
                    // Recupération des organisations de l'utilisateur
                    return this.organizationMetierService.getMyOrganizations(connectedUser?.uuid);
                }))
            .subscribe(
                {
                    next: (organzations: Organization[] | undefined) => {
                        this.myOrganizations = organzations;
                        this.email = this.getEmail();
                    },
                    complete: () => {
                        this.isLoading = false;
                    },
                    error: (e) => {
                        console.error(e);
                        this.isLoading = false;
                    }
                }
            );
    }

    getEmail(): string {
        return this.utilisateurService.lookupEMailAddress(this.user);
    }

    // Formatter la liste des organisations
    getOrganizations(organizations: Organization[]): string {
        return organizations?.map(value => value.name).join(', ');
    }
}
