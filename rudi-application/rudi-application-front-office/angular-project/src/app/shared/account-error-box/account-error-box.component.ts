import {Component, Inject, OnInit} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';

/**
 * Le error-box est une div générique capable d'afficher un message d'erreur et collé un message cliquable qui fait quelque-chose
 */
@Component({
    selector: 'app-account-error-box',
    templateUrl: './account-error-box.component.html',
    styleUrls: ['./account-error-box.component.scss']
})
export class AccountErrorBoxComponent implements OnInit {
    /**
     * Pour savoir comment restituer le composant en mode desktop/mobile
     */
    mediaSize: MediaSize;

    constructor(@Inject(MAT_SNACK_BAR_DATA) public data,
                private router: Router,
                private breakpointObserver: BreakpointObserverService,
                public snackBarRef: MatSnackBarRef<AccountErrorBoxComponent>) {
    }

    ngOnInit(): void {
        // On récupère les infos sur la restitution
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    /**
     * Au lcic sur le lien vers la page d'inscription, le snackbar se ferme
     */
    goToAccountRegistration(): void {
        this.router.navigate(['/sign-up']).then(() => this.snackBarRef.dismiss());
    }
}
