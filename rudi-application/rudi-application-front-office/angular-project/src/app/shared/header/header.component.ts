import {Component, Input} from '@angular/core';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {UserService} from '../../core/services/user.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../core/services/breakpoint-observer.service';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../core/services/authentication.service';
import {AuthenticationState} from '../../core/services/authentication/authentication-method';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {PropertiesMetierService} from '../../core/services/properties-metier.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

    /**
     * Permet de récupérer les dimensions de la page
     */
    @Input()
    mediaSize: MediaSize;

    /**
     * Est-ce que le collapsible est collapsé ?
     */
    isCollapsed = true;

    /**
     * Est-ce qu'on est "connecté" CAD user non anonymous
     */
    isConnectedAsUser = false;

    /**
     * Url d'accès à la documentation depuis le header
     */
    urlToDoc: string;

    constructor(
        private location: Location,
        public dialog: MatDialog,
        public router: Router,
        private readonly authenticationService: AuthenticationService,
        public readonly utilisateurService: UserService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly iconRegistry: MatIconRegistry,
        private readonly sanitizer: DomSanitizer,
        private readonly propertiesMetierService: PropertiesMetierService
    ) {
        // Quand l'utilisateur connecté change (on se connecte ou autre)
        this.authenticationService.authenticationChanged$.subscribe((state) => {
            // On est connecté si on est pas anonymous
            this.isConnectedAsUser = state === AuthenticationState.USER;
        });
        iconRegistry.addSvgIcon(
            'logo-bleu-orange',
            sanitizer.bypassSecurityTrustResourceUrl('assets/images/logo_bleu_orange.svg'));
        this.getUrlToDoc();
    }

    /**
     * Récupérer les classes CSS pour mobile/desktop
     */
    get ngClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('header-container');
    }

    /**
     * Evenet : sur clic du burger
     */
    handleClickBurger(): void {
        this.isCollapsed = !this.isCollapsed;
    }

    handleClickGoLogin(): void {
        this.router.navigate(['/login']);
    }

    /**
     * Event click sur le bouton : Mon Compte
     */
    handleClickGoMonCompte(): void {
        // Si on est co : go /account
        if (this.isConnectedAsUser) {
            this.router.navigate(['login/account']);
        }
        // pas co ? go login
        else {
            this.router.navigate(['/login']);
        }
    }

    handleClickGoReceivedLinkedDatasets(): Promise<boolean> {
        return this.router.navigate(['/personal-space/received-access-requests']);
    }

    handleClickGoToReuse(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-projects']);
    }

    getUrlToDoc(): void {
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (link: string) => {
                this.urlToDoc = link;
            }
        });
    }
}
