import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {LogService} from "./log.service";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";
import {FormGroup} from "@angular/forms";
import {AuthenticationMethod} from "./authentication/authentication-method";
import {ANONYMOUS_USERNAME, AnonymousAuthentication} from './authentication/anonymous-authentication';
import {LoginAuthentication} from "./authentication/login-authentication";

export const AUTHORIZATION_HEADER = 'Authorization';
export const X_TOKEN_HEADER = 'X-TOKEN';
export const SESSION_TOKEN = "_jwt";
export const X_TOKEN = "_xtoken";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    /**
     * Pour éviter les boucles infinies en cas d'erreur d'authentification avec ACL
     */
    private authenticationErrorsByUsername: {[name: string]:any} = {};

    /**
     * Permet de récupérer l'évènement d'authentification
     */
    private isAuthenticated: BehaviorSubject<boolean>;
    public readonly isAuthenticated$: Observable<boolean>;

    /**
     * Constructeur
     */
    constructor(private readonly logService: LogService,
                private http: HttpClient,
                private readonly anonymousAuthenticationService: AnonymousAuthentication,
                private readonly loginAuthenticationService: LoginAuthentication
    ) {
        this.isAuthenticated = new BehaviorSubject(this.hasToken());
        this.isAuthenticated$ = this.isAuthenticated.asObservable()
    }

    getHeadersForAuthentication(): { [name: string]: string | string[] } {
        const token = AuthenticationService.getToken();
        return token ? {
            [AUTHORIZATION_HEADER]: token,
            [X_TOKEN_HEADER]: AuthenticationService.getXToken()
        } : {}
    }

    private static getToken(): string {
        return <string>sessionStorage.getItem(SESSION_TOKEN);
    }

    private static getXToken(): string {
        return <string>sessionStorage.getItem(X_TOKEN);
    }

    /**
     * retourne vrai si l'on est authentifié
     */
    hasToken(): boolean {
        const isAuthenticated = AuthenticationService.getToken() != null;
        this.logService.info(`AuthenticationService.isAuthenticated, `, isAuthenticated);
        return isAuthenticated;
    }


    /**
     * Connexion de l'utilisateur
     * @param form formulaire envoyé pour la connexion
     */
    authenticate(form: FormGroup): Observable<any> {
        return this.authenticateWith(this.loginAuthenticationService, form);
    }

    /**
     * Connexion de l'utilisateur
     */
    authenticateAsAnonymous(): Observable<any> {
        return this.authenticateWith(this.anonymousAuthenticationService);
    }

    private authenticateWith(authenticationMethod: AuthenticationMethod, formGroup?: FormGroup): Observable<any> {
        return new Observable<any>((observer) => {
            const login = authenticationMethod.getLogin(formGroup);

            // @TODO mutualisation des rootUrls avec les autres services
            this.http.request<HttpResponse<any>>('POST', authenticationMethod.getBaseUrl(), {
                body: authenticationMethod.getPayload(formGroup),
                headers: new HttpHeaders().set('Content-Type', authenticationMethod.getContentType()),
                observe: 'response',
                reportProgress: false,
                responseType: 'json',
                withCredentials: false
            }).subscribe(
                (response) => {
                    this.storeTokensFrom(response.headers, true);
                    this.isAuthenticated.next(true);
                    observer.next(response.body);
                    observer.complete();
                }, (error) => { //TODO: différencier l'erreur 4xx et l'erreur 5xx
                    this.logService.error(`Authentication failed with login ${login}`, '\n Cause :', error);
                    if (formGroup) {
                        this.logService.error('Authentication credentials :', formGroup.value);
                    }
                    AuthenticationService.clearTokens()
                    this.setAuthenticationErrorForUsername(login, error);
                    this.isAuthenticated.next(false);
                    observer.error(error);
                });
        });
    }

    private setAuthenticationErrorForUsername(username: string, error) {
        this.authenticationErrorsByUsername[username] = error;
    }

    private clearAllAuthenticationErrors() {
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
    public storeTokensFrom(headers: HttpHeaders, removeIfMissing = false) {
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

        //sessionStorage.setItem('_refreshToken', success.body.refreshToken);

        if (authorizationHeader || xtokenHeader) {
            this.clearAllAuthenticationErrors();
        }
    }

    /**
     * Supprime tous les tokens stockés
     */
    private static clearTokens() {
        sessionStorage.removeItem(SESSION_TOKEN)
        sessionStorage.removeItem(X_TOKEN)
        sessionStorage.removeItem('_refreshToken')
    }

}
