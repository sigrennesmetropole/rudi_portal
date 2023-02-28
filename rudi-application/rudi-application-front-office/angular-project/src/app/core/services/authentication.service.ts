import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {LogService} from './log.service';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {FormGroup} from '@angular/forms';
import {AuthenticationMethod, AuthenticationState} from './authentication/authentication-method';
import {ANONYMOUS_USERNAME, AnonymousAuthentication} from './authentication/anonymous-authentication';
import {LoginAuthentication} from './authentication/login-authentication';
import {catchError, map, switchMap} from 'rxjs/operators';
import {AccountService} from './account.service';

/**
 * Le header qui contient le token qui certifie si on est authentifié ou pas
 */
export const AUTHORIZATION_HEADER = 'Authorization';

/**
 * Le header qui va contenir le refresh token du token
 */
export const X_TOKEN_HEADER = 'X-TOKEN';

/**
 * La clé dans le storage qui va contenir le token d'autorisation
 */
export const SESSION_TOKEN = '_jwt';

/**
 * La clé dans le storage qui va contenir le token de refresh JWT
 */
export const X_TOKEN = '_xtoken';

const AUTHENTICATION_STATE_SESSION_STORAGE_KEY = 'authenticationState';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    public static FIELD_LOGIN_AUTHENTICATION = 'login';
    public static FIELD_PASSWORD_AUTHENTICATION = 'password';

    public static ERROR_ACCOUNT_NOT_ACTIVE = 'error_account_not_active_42';
    public static ERROR_SERVER_IS_NOT_ACTIVE = 'error_account_not_active_43';
    public static ERROR_SERVER_AUTHENTICATE = 'error_account_not_active_44';
    public static ERROR_SERVER_USER_LOCKED = '423';

    /**
     * Pour éviter les boucles infinies en cas d'erreur d'authentification avec ACL
     */
    private authenticationErrorsByUsername: { [name: string]: any } = {};

    /**
     * Permet de récupérer l'évènement d'authentification
     */
    private isAuthenticated: BehaviorSubject<boolean>;
    public readonly isAuthenticated$: Observable<boolean>;

    /**
     * Evenement qui indique qu'une authentification a eu lieu
     */
    private authenticationChanged: BehaviorSubject<AuthenticationState> = new BehaviorSubject<AuthenticationState>(AuthenticationService.getAuthenticationStateFromSessionStorage());
    public authenticationChanged$: Observable<AuthenticationState> = this.authenticationChanged.asObservable();

    /**
     * Constructeur
     */
    constructor(private readonly logService: LogService,
                private http: HttpClient,
                private accountService: AccountService,
                private readonly anonymousAuthenticationService: AnonymousAuthentication,
                private readonly loginAuthenticationService: LoginAuthentication
    ) {
        this.isAuthenticated = new BehaviorSubject(this.isAuthenticatedWithToken());
        this.isAuthenticated$ = this.isAuthenticated.asObservable();
    }

    /**
     * Est-ce que l'erreur (si a lieu) est une erreur de type 4XX (erreur d'authent)
     */
    public static isError4xx(errorCodeString: string): boolean {
        return Number(errorCodeString) >= 400 && Number(errorCodeString) < 500;
    }

    /**
     * Récupération du token d'authent
     * @private
     */
    private static getToken(): string {
        return sessionStorage.getItem(SESSION_TOKEN);
    }

    /**
     * Récupération du refresh token X-TOKEN
     * @private
     */
    private static getXToken(): string {
        return sessionStorage.getItem(X_TOKEN);
    }

    /**
     * Supprime tous les tokens stockés
     */
    public static clearTokens(): void {
        sessionStorage.removeItem(SESSION_TOKEN);
        sessionStorage.removeItem(X_TOKEN);
        sessionStorage.removeItem(AUTHENTICATION_STATE_SESSION_STORAGE_KEY);
    }

    /**
     * Supprime le token qui disait qu'on était authentifié das l'app car on ne l'est plus
     */
    public static clearAuhtenticatedTokenInSession(): void {
        sessionStorage.removeItem(SESSION_TOKEN);
    }

    private static getAuthenticationStateFromSessionStorage(): AuthenticationState | null {
        const authenticationStateAsString = sessionStorage.getItem(AUTHENTICATION_STATE_SESSION_STORAGE_KEY);
        if (authenticationStateAsString) {
            for (const authenticationStateEnumValue in AuthenticationState) {
                if (authenticationStateAsString === authenticationStateEnumValue) {
                    return AuthenticationState[authenticationStateEnumValue];
                }
            }

            console.warn(`authenticationState ${authenticationStateAsString} inconnu`);
            sessionStorage.removeItem(AUTHENTICATION_STATE_SESSION_STORAGE_KEY);
        }
        return null;
    }

    private static setAuthenticationStateIntoSessionStorage(authenticationState: AuthenticationState | null): void {
        if (authenticationState) {
            sessionStorage.setItem(AUTHENTICATION_STATE_SESSION_STORAGE_KEY, authenticationState.toString());
        } else {
            sessionStorage.removeItem(AUTHENTICATION_STATE_SESSION_STORAGE_KEY);
        }
    }

    /**
     * Récupération des tokens qui seront insérés dans toutes les requêtes vers le serveur
     */
    getHeadersForRequestInjection(): { [name: string]: string | string[] } {
        const token = AuthenticationService.getToken();
        return token ? {
            [AUTHORIZATION_HEADER]: token
        } : {};
    }

    /**
     * Récupération des tokens qui seront insérés pour une requête de refresh token
     */
    getHeadersForRefreshToken(): { [name: string]: string | string[] } {
        return {
            [X_TOKEN_HEADER]: AuthenticationService.getXToken()
        };
    }

    /**
     * Indique si le client (front) est authentifié à l'aide d'un token
     */
    isAuthenticatedWithToken(): boolean {
        return AuthenticationService.getToken() != null;
    }

    /**
     * Connexion de l'utilisateur
     * @param form formulaire envoyé pour la connexion
     */
    authenticate(form: FormGroup): Observable<void> {

        if (form == null || form.get(AuthenticationService.FIELD_LOGIN_AUTHENTICATION) == null
            || form.get(AuthenticationService.FIELD_PASSWORD_AUTHENTICATION) == null) {
            return throwError(new Error('paramètres manquant pour effectuer une authentification (formulaire nul ou incomplet)'));
        }

        return this.accountService.isAccountCreatedNotValidated(form.get(AuthenticationService.FIELD_LOGIN_AUTHENTICATION).value).pipe(
            catchError((error: HttpErrorResponse) => {
                console.error('error server is not active :', error.message);
                throw new Error(AuthenticationService.ERROR_SERVER_IS_NOT_ACTIVE);
            }),
            switchMap((isAccountCreatedNotValidated: boolean) => {
                if (isAccountCreatedNotValidated) {
                    throw new Error(AuthenticationService.ERROR_ACCOUNT_NOT_ACTIVE);
                } else {
                    // Quand on s'authent en tant que user normal et que ça échoue, ben on fait rien comme ça on perd pas l'authent anonyme
                    return this.authenticateWith(this.loginAuthenticationService, form).pipe(
                        catchError((error: HttpErrorResponse) => {
                            console.error('error server authent :', error.message);
                            if (error.status >= 400 && error.status < 500) {
                                // Si erreur connu, alors on affiche un message au client
                                throw new Error('' + error.status);
                            }
                            // Erreur innatendu du server
                            throw new Error(AuthenticationService.ERROR_SERVER_AUTHENTICATE);
                        })
                    );
                }
            })
        );
    }

    /**
     * Connexion de anonymous (peut être appelé de beaucoups d'endroits)
     */
    authenticateAsAnonymous(): Observable<void> {
        // Récupération du login anonymous
        const login = this.anonymousAuthenticationService.getLogin(null);

        // Quand on s'authent en tant que anonyme et que ça échoue, là c'est grave donc on clear les tokens etc.
        return this.authenticateWith(this.anonymousAuthenticationService).pipe(
            catchError((error: HttpErrorResponse) => {
                AuthenticationService.clearTokens();
                this.setAuthenticationErrorForUsername(login, error);
                throw new Error(AuthenticationService.ERROR_SERVER_AUTHENTICATE);
            })
        );
    }

    /**
     * Méthode qui fait l'authentification
     * @param authenticationMethod authent anonyme ou utilisateur
     * @param formGroup le formulaire d'authent vaut nul si anonyme
     * @private
     */
    private authenticateWith(authenticationMethod: AuthenticationMethod, formGroup?: FormGroup): Observable<void> {

        // On lance la requête d'authent
        return this.http.post(
            authenticationMethod.getBaseUrl(),
            authenticationMethod.getPayload(formGroup),
            {
                headers: new HttpHeaders().set('Content-Type', authenticationMethod.getContentType()),
                observe: 'response',
                reportProgress: false,
                responseType: 'json',
                withCredentials: false
            }
        )
            .pipe(
                // Fonction qui va être appelée si l'authent est : OK
                // tslint:disable-next-line:no-any car la requête renvoie un <any> car le swagger est pas mappé sur l'authent
                map((response: HttpResponse<any>) => {

                    // Si on réussit l'autent user on change l'état du service
                    this.authenticationState = authenticationMethod.getTargetState();

                    // On fait l'authent dans notre appli
                    this.storeTokensFrom(response.headers, true);
                })
            );
    }

    private set authenticationState(authenticationState: AuthenticationState | null) {
        this.authenticationChanged.next(authenticationState);
        AuthenticationService.setAuthenticationStateIntoSessionStorage(authenticationState);
    }

    private get authenticationState(): AuthenticationState | null {
        return this.authenticationChanged.value || AuthenticationService.getAuthenticationStateFromSessionStorage();
    }

    private setAuthenticationErrorForUsername(username: string, error): void {
        this.authenticationErrorsByUsername[username] = error;
    }

    private clearAllAuthenticationErrors(): void {
        this.authenticationErrorsByUsername = {};
    }

    authenticationFailedForUsername(username: string): boolean {
        return !!this.authenticationErrorsByUsername[username];
    }

    authenticationFailedForAnonymous(): boolean {
        return this.authenticationFailedForUsername(ANONYMOUS_USERNAME);
    }

    /**
     * Stockage des tokens dans la session
     * @param headers headers de la réponse HTTP
     * @param removeIfMissing supprime les tokens s'ils ne sont pas présents dans la réponse HTTP
     */
    public storeTokensFrom(headers: HttpHeaders, removeIfMissing = false): void {
        const authorizationHeader = headers.get(AUTHORIZATION_HEADER);
        if (authorizationHeader) {
            sessionStorage.setItem(SESSION_TOKEN, authorizationHeader);
        } else if (removeIfMissing) {
            sessionStorage.removeItem(SESSION_TOKEN);
        }

        const xtokenHeader = headers.get(X_TOKEN_HEADER);
        if (xtokenHeader) {
            sessionStorage.setItem(X_TOKEN, xtokenHeader);
        } else if (removeIfMissing) {
            sessionStorage.removeItem(X_TOKEN);
        }

        if (authorizationHeader || xtokenHeader) {
            this.clearAllAuthenticationErrors();
        }
    }

    /**
     * Déconnexion de l'utilisateur
     */
    logout(): Observable<void> {
        return this.accountService.accoutLogout(AuthenticationService.getXToken());
    }
}
