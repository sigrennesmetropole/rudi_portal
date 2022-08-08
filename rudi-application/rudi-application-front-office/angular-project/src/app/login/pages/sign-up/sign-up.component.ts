import {Component, OnInit} from '@angular/core';
import {AbstractControl, AbstractControlOptions, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {ConfirmedValidator} from './confirmed-validator';
import {Router} from '@angular/router';
import {AccountService} from '../../../core/services/account.service';
import {AuthenticationService} from '../../../core/services/authentication.service';
import {RouteHistoryService} from '../../../core/services/route-history.service';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {RudiValidators} from '../../../shared/validators/rudi-validators';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

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
    passwordMinLength = 10;
    passwordMaxLength = 100;

    constructor(private formBuilder: FormBuilder,
                private routeHistoryService: RouteHistoryService,
                private authenticationService: AuthenticationService,
                private snackBarService: SnackBarService,
                private translateService: TranslateService,
                private breakpointObserver: BreakpointObserverService,
                private router: Router,
                private accountService: AccountService,
                private propertiesService: PropertiesMetierService,
    ) {
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

        // Construction du formulaire d'inscription
        this.signupForm = this.formBuilder.group({
                nom: [''],
                prenom: [''],
                adresseEmail: ['', [RudiValidators.email]],
                password: ['', [Validators.required, Validators.minLength(this.passwordMinLength), Validators.maxLength(this.passwordMaxLength)]], // TODO il faudrait refuser les espaces
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

        // lancement d'appels REST multiples pour créer un compte puis s'authentifier
        this.accountService.createAccount(this.signupForm).subscribe(
            () => {
                this.loading = false;

                // Si on s'est bien authentifié on revient sur la page d'avant Si on peut go back on go back
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
            (errorString: string) => {
                this.loading = false;
                this.errorString = errorString;
            });
    }
}
