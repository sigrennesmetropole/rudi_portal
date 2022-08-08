import {Component, Input} from '@angular/core';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {UserService} from '../../core/services/user.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../core/services/breakpoint-observer.service';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../core/services/authentication.service';
import {AuthenticationState} from '../../core/services/authentication/authentication-method';

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

    constructor(
        private location: Location,
        public dialog: MatDialog,
        public router: Router,
        private readonly authenticationService: AuthenticationService,
        public readonly utilisateurService: UserService,
        private readonly breakpointObserver: BreakpointObserverService
    ) {
        // Quand l'utilisateur connecté change (on se connecte ou autre)
        this.authenticationService.authenticationChanged$.subscribe((state) => {
            // On est connecté si on est pas anonymous
            this.isConnectedAsUser = state === AuthenticationState.USER;
        });
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
        const targetRoute = this.isConnectedAsUser ? '/personal-space/received-access-requests' : '/login';
        return this.router.navigate([targetRoute]);
    }
}
