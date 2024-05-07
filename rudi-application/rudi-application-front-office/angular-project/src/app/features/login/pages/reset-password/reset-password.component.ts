import {HttpErrorResponse} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {AbstractControl, AbstractControlOptions, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatSnackBar, MatSnackBarConfig} from '@angular/material/snack-bar';
import {ActivatedRoute, Router} from '@angular/router';
import {PASSWORD_REGEX} from '@core/const';
import {AccountService} from '@core/services/account.service';
import {AuthenticationService} from '@core/services/authentication.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {RouteHistoryService} from '@core/services/route-history.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ResetPasswordErrorBoxComponent} from '@shared/reset-password-error-box/reset-password-error-box.component';
import {first} from 'rxjs/operators';
import {ConfirmedValidator} from '../sign-up/confirmed-validator';

@Component({
    selector: 'app-reset-password-page',
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

    /**
     * Formulaire de saisie pour création
     */
    signupForm: FormGroup;

    /**
     * Le message d'erreur si erreur a lieu lors de la création
     */
    errorString = '';

    /**
     * Est-ce que le composant se charge ? (authent en cours)
     */
    loading = false;

    /**
     * Cache-t-on le mot de passe
     */
    hidePassword = true;

    /**
     * Cache-t-on le mot de passe
     */
    hideConfirmPassword = true;

    /**
     * Est-ce que l'erreur d'authent obtenue est de type : erreur d'authent HTTP
     */
    isError4xx = false;

    /**
     * Pour savoir si on est en mode mobile ou desktop
     */
    mediaSize: MediaSize;
    passwordMinLength = 12;
    passwordMaxLength = 100;


    get snackBarParam(): string | null {
        return this.route.snapshot.queryParams.snackBar;
    }

    constructor(
        private formBuilder: FormBuilder,
        private routeHistoryService: RouteHistoryService,
        private authenticationService: AuthenticationService,
        private snackBarService: SnackBarService,
        private translateService: TranslateService,
        private breakpointObserver: BreakpointObserverService,
        private router: Router,
        private snackbar: MatSnackBar,
        private readonly route: ActivatedRoute,
        private accountService: AccountService,
        private propertiesService: PropertiesMetierService,
    ) {
        // Construction du formulaire d'inscription
        this.signupForm = this.formBuilder.group({
                password: ['', [Validators.required, Validators.minLength(this.passwordMinLength),
                    Validators.maxLength(this.passwordMaxLength), Validators.pattern(PASSWORD_REGEX)]],
                confirmPassword: ['', [Validators.required]],
            },
            {
                validators: ConfirmedValidator('password', 'confirmPassword')
            } as AbstractControlOptions
        );

    }

    /**
     * formControls permettant de verifier les validators dans le HTML
     */
    get formControls(): { [key: string]: AbstractControl } {
        return this.signupForm.controls;
    }

    /**
     * Teste si le formulaire est valide
     */
    get isValid(): boolean {
        return this.signupForm.valid;
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.checkPasswordChangeToken();
    }

    private get password(): string {
        return this.signupForm.get('password').value;
    }

    handleClickResetPassword(): void {
        const token = this.route.snapshot.queryParams.token;
        this.loading = true;
        const passwordChange = {token, password: this.password};
        this.accountService.validatePasswordChange(passwordChange)
            .subscribe({
                next: () => {
                    this.loading = false;
                    this.routeHistoryService.resetHistory();
                    // Si on s'est bien authentifié on revient sur la page d'avant Si on peut go back on go back
                    this.router.navigate(['/login']);
                    this.propertiesService.get('rudidatarennes.contact').subscribe(rudidatarennesContactLink => {
                        this.snackBarService.openSnackBar({
                            message: `${this.translateService.instant('snackbarTemplate.successResetPassword')}`,
                            keepBeforeSecondRouteChange: true
                        });
                    });
                },
                error: (errorString: string) => {
                    this.loading = false;
                    this.errorString = errorString;
                }
            });
    }

    handleClickGoResetPassword(): void {
        this.router.navigate(['/login/reset-password']);
    }

    checkPasswordChangeToken(): void {
        // Recuperation du token dans la route
        this.loading = true;
        const token = this.route.snapshot.queryParams.token;
        const badRequestStatus402 = 422;
        const badRequestStatus404 = 404;

        this.accountService.checkPasswordChangeToken(token)
            .pipe(first())
            .subscribe({
                next: () => {
                    this.loading = false;
                },
                error: (err: HttpErrorResponse) => {
                    this.loading = false;
                    // Si l'utilisateur a dépassé le délai de 1 heures ou Si l'utilisateur a déjà cliqué sur le lien
                    if (err.status === badRequestStatus402 || badRequestStatus404) {
                        console.log('badRequestStatus ?', err.status);
                        this.router.navigate(['/login']);
                        const config = new MatSnackBarConfig();
                        config.panelClass = ['mat-elevation-z3', 'account-error-style'];
                        config.horizontalPosition = 'center';
                        this.snackbar.openFromComponent(ResetPasswordErrorBoxComponent, config);
                    }
                }
            });
    }
}
