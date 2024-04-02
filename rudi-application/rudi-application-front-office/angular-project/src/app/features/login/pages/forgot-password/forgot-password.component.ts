import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AccountService} from '@core/services/account.service';
import {AuthenticationService} from '@core/services/authentication.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {RedirectService} from '@core/services/redirect.service';
import {RouteHistoryService} from '@core/services/route-history.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {forkJoin} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';

@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
    /**
     * Objet contenant les infos du formulaire
     */
    loginForm: FormGroup;

    /**
     * Pour savoir comment restituer le composant en mode desktop/mobile
     */
    mediaSize: MediaSize;

    /**
     * Est-ce que le composant se charge ? (authent en cours)
     */
    loading = false;

    /**
     * SnackBar i18n key to display on init
     */
    get snackBarParam(): string | null {
        return this.route.snapshot.queryParams.snackBar;
    }

    constructor(private formBuilder: FormBuilder,
                private breakpointObserver: BreakpointObserverService,
                private authentificationService: AuthenticationService,
                private router: Router,
                private redirectService: RedirectService,
                private readonly route: ActivatedRoute,
                private readonly snackBarService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly accountService: AccountService,
                private readonly routeHistoryService: RouteHistoryService,
                private readonly propertiesService: PropertiesMetierService,
    ) {
    }

    ngOnInit(): void {
        // On récupère les infos sur la restitution
        this.mediaSize = this.breakpointObserver.getMediaSize();

        // Initialisation des controles du formulaire
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required]
        });

        const snackBarParam = this.snackBarParam;
        if (snackBarParam) {
            this.snackBarService.openSnackBar(this.translateService.instant(snackBarParam));
        }
    }

    /**
     * getter sur le form pour l'utiliser dans le HTML
     */
    get formControls(): { [key: string]: AbstractControl } {
        return this.loginForm.controls;
    }

    /**
     * Savoir si le formulaire est valide
     */
    get isValid(): boolean {
        return this.loginForm.valid;
    }

    get redirectToParam(): string | null {
        return this.route.snapshot.queryParams.redirectTo;
    }

    /**
     * Quand l'utilisateur clique sur s'inscrire
     */
    handleClickGoInscrire(): void {
        this.router.navigate(['/login/sign-up']);
    }

    private get email(): string {
        return this.loginForm.get('login').value;
    }

    handleClickResetPassword() {
        this.loading = true;

        this.accountService.requestPasswordChange(this.email).pipe(
            switchMap(() => this.routeHistoryService.goBackOrElseGoAccount()),
            switchMap(() => forkJoin({
                messageBeforeLink: this.translateService.get('resetPassword.snackbar.messageBeforeLink'),
                linkHref: this.propertiesService.get('rudidatarennes.contact'),
                linkLabel: this.translateService.get('resetPassword.snackbar.linkLabel'),
            })),
            map(({messageBeforeLink, linkHref, linkLabel}) =>
                this.snackBarService.openSnackBar({
                    message: `${messageBeforeLink}<a href="${linkHref}">${linkLabel}</a>.`
                })
            ),
        ).subscribe({
            error: () => {
                this.loading = false;
                this.translateService.get('error.internalError').subscribe(message => {
                    this.snackBarService.openSnackBar({
                        message: `${message}`,
                        level: Level.ERROR
                    });
                });
            },
            complete: () => {
                this.loading = false;
            }
        });
    }
}
