import {Component, OnInit, ViewChild} from '@angular/core';
import {AbstractControl, AbstractControlOptions, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {PASSWORD_REGEX} from '@core/const';
import {AccountService} from '@core/services/account.service';
import {AuthenticationService} from '@core/services/authentication.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {CAPTCHA_NOT_VALID_CODE, CaptchaCheckerService} from '@core/services/captcha-checker.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {RouteHistoryService} from '@core/services/route-history.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ErrorWithCause} from '@shared/models/error-with-cause';
import {RudiCaptchaComponent} from '@shared/rudi-captcha/rudi-captcha.component';
import {RudiValidators} from '@shared/validators/rudi-validators';
import {ConfirmedValidator} from './confirmed-validator';

@Component({
    selector: 'app-sign-up',
    templateUrl: './sign-up.component.html',
    styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

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
     * Pour savoir si on est en mode mobile ou desktop
     */
    mediaSize: MediaSize;
    passwordMinLength = 12;
    passwordMaxLength = 100;

    /**
     * Indique si le captcha doit s'activer sur cette page
     */
    enableCaptchaOnPage: true;

    /**
     * Error on captcha input
     */
    errorCaptchaInput = false;
    @ViewChild(RudiCaptchaComponent) rudiCaptcha: RudiCaptchaComponent;

    constructor(private formBuilder: FormBuilder,
                private routeHistoryService: RouteHistoryService,
                private authenticationService: AuthenticationService,
                private snackBarService: SnackBarService,
                private translateService: TranslateService,
                private breakpointObserver: BreakpointObserverService,
                private router: Router,
                private accountService: AccountService,
                private propertiesService: PropertiesMetierService,
                private readonly captchaCheckerService: CaptchaCheckerService,
                private readonly route: ActivatedRoute,
    ) {
    }

    /**
     * formControls permettant de verifier les validators dans le HTML
     */
    get formControls(): { [key: string]: AbstractControl } {
        return this.signupForm.controls;
    }

    /**
     * Teste si le formulaire est valide et le captcha bien rempli (ou non activé)
     */
    get isValid(): boolean {
        return this.signupForm.valid && (this.rudiCaptcha?.isFilled() || !this.enableCaptchaOnPage);
    }

    ngOnInit(): void {
        if (this.route.snapshot.data?.aclAppInfo) {
            this.enableCaptchaOnPage = this.route.snapshot.data.aclAppInfo.captchaEnabled;
        }

        this.mediaSize = this.breakpointObserver.getMediaSize();

        // Construction du formulaire d'inscription
        this.signupForm = this.formBuilder.group({
                nom: [''],
                prenom: [''],
                adresseEmail: ['', [RudiValidators.email]],
                password: ['',
                    [
                        Validators.required,
                        Validators.minLength(this.passwordMinLength),
                        Validators.maxLength(this.passwordMaxLength),
                        Validators.pattern(PASSWORD_REGEX)
                    ]
                ],
                confirmPassword: ['', [Validators.required]],
                cgu: [false, [Validators.requiredTrue]],
                subscribeToNotifications: [false]
            },
            {
                validators: ConfirmedValidator('password', 'confirmPassword')
            } as AbstractControlOptions
        );
    }

    /**
     * Methode permettant l'inscription
     */
    handleClickSignup(): void {
        // Reset des toggles
        this.loading = true;
        this.errorString = '';

        // Validation du captcha avant tout puis appel du nextStep
        this.captchaCheckerService.validateCaptchaAndDoNextStep(this.enableCaptchaOnPage, this.rudiCaptcha,
            this.accountService.createAccount(this.signupForm))
            .subscribe(
                () => {
                    this.loading = false;
                    this.routeHistoryService.goBackOrElseGoAccount();
                    this.propertiesService.get('rudidatarennes.contact').subscribe(rudidatarennesContactLink => {
                        this.snackBarService.openSnackBar({
                            message: `
                        ${this.translateService.instant('snackbarTemplate.successIncription')}
                        <a href="${rudidatarennesContactLink}">
                            ${this.translateService.instant('snackbarTemplate.successIncriptionLinkText')}
                        </a>
                    `,
                            keepBeforeSecondRouteChange: true
                        });
                    });
                },
                // Si erreur lors de la création du compte ou de l'authent auto
                (error: Error) => {
                    this.loading = false;
                    console.error(error);
                    if (error instanceof ErrorWithCause && error.code === CAPTCHA_NOT_VALID_CODE) {
                        this.errorCaptchaInput = true;
                        return;
                    }
                    this.errorString = error.message;
                });
    }
}
