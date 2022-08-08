import {Component, Inject, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../core/services/breakpoint-observer.service';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-reset-password-error-box',
  templateUrl: './reset-password-error-box.component.html',
  styleUrls: ['./reset-password-error-box.component.scss']
})
export class ResetPasswordErrorBoxComponent implements OnInit {
    /**
     * Pour savoir comment restituer le composant en mode desktop/mobile
     */
    mediaSize: MediaSize;

    constructor(@Inject(MAT_SNACK_BAR_DATA) public data,
                private router: Router,
                private breakpointObserver: BreakpointObserverService,
                public snackBarRef: MatSnackBarRef<ResetPasswordErrorBoxComponent>) {
    }

    ngOnInit(): void {
        // On récupère les infos sur la restitution
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    /**
     * Au clic sur le lien vers la page de reitialisation de password, le snackbar se ferme
     */
    goToAccountRegistration(): void {
        this.router.navigate(['login/forgot-password']).then(() => this.snackBarRef.dismiss());
    }

}
