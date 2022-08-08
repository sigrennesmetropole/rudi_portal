import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {AuthenticationService} from '../../../core/services/authentication.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RedirectService} from '../../../core/services/redirect.service';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    /**
     * Objet contenant les infos du formulaire
     */
    loginForm: FormGroup;

    /**
     * Pour savoir comment restituer le composant en mode desktop/mobile
     */
    mediaSize: MediaSize;

    /**
     * Est-ce qu'on affiche ou masque le mot de passe
     */
    hidePassword = true;

    /**
     * Est-ce que l'erreur d'authent obtenue est de type : erreur d'authent HTTP
     */
    isError4xx = false;

    /**
     * Est-ce que l'erreur d'authent obtenue est de type : erreur d'authent HTTP
     */
    errorAccountNotActif = false;

    /**
     * Est-ce que l'erreur d'authent obtenue est de type : User locked
     */
    errorUserLocked = false;

    /**
     * Est-ce que le composant se charge ? (authent en cours)
     */
    loading = false;

    /**
     *  erreur server check Account active
     */
    errorServerAccountNotActive = false;

    /**
     *  error Server Authenticate
     */
    errorServerAuthenticate = false;


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
     * SnackBar i18n key to display on init
     */
    get snackBarParam(): string | null {
        return this.route.snapshot.queryParams.snackBar;
    }

    /**
     * Constructeur
     */
    constructor(private formBuilder: FormBuilder,
                private breakpointObserver: BreakpointObserverService,
                private authentificationService: AuthenticationService,
                private router: Router,
                private redirectService: RedirectService,
                private readonly route: ActivatedRoute,
                private readonly snackBarService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly propertiesMetierService: PropertiesMetierService,
                ) {

    }

    ngOnInit(): void {

        // On récupère les infos sur la restitution
        this.mediaSize = this.breakpointObserver.getMediaSize();

        // Initialisation des controles du formulaire
        this.loginForm = this.formBuilder.group({
            login: ['', Validators.required],
            password: ['', Validators.required]
        });

        const snackBarParam = this.snackBarParam;
        if (snackBarParam) {
            this.snackBarService.openSnackBar(this.translateService.instant(snackBarParam));
        }
    }

    /**
     * Quand l'utilisateur clique sur s'inscrire
     */
    handleClickGoInscrire(): void {
        this.router.navigate(['/login/sign-up']);
    }

    /**
     * Quand l'utilisateur click sur le lien equipe technique Rudi
     */
    handleClickContactRudi(): void {
        this.propertiesMetierService.get('rudidatarennes.contact').subscribe(link => {
            window.location.href = link;
        });
    }

    /**
     * Quand l'utilisateur click sur le lien du message d'erreur après que son compte ait été bloqué
     */
    handleClickToResetPassword(): void {
        this.router.navigate(['reset-password']).then(r => r);
    }

    /**
     * Function permettant de s'authentifier
     */
    handleClickLogin(): void {
        this.isError4xx = false;
        this.errorServerAccountNotActive = false;
        this.errorServerAuthenticate = false;
        this.errorAccountNotActif = false;
        this.errorUserLocked = false;
        this.loading = true;
        this.authentificationService.authenticate(this.loginForm).subscribe({
                next: () => {
                    this.loading = false;
                    this.redirectService.followRedirectOrGoBack();
                },
                error: (error: Error) => {
                    this.loading = false;

                    // Si le code http 423 est renvoyé, le compte est bloqué
                    if (error.message === AuthenticationService.ERROR_SERVER_USER_LOCKED) {
                        this.errorUserLocked = true;
                    } // Sinon erreur server renvoyé
                    else {
                        this.isError4xx = AuthenticationService.isError4xx(error.message);

                    }

                    switch (error.message) {
                        case AuthenticationService.ERROR_ACCOUNT_NOT_ACTIVE :
                            this.errorAccountNotActif = true;
                            break;

                        case AuthenticationService.ERROR_SERVER_IS_NOT_ACTIVE :
                            this.errorServerAccountNotActive = true;
                            break;

                        case AuthenticationService.ERROR_SERVER_AUTHENTICATE :
                            this.errorServerAuthenticate = true;
                            break;
                    }
                }
            }
        );
    }
}
