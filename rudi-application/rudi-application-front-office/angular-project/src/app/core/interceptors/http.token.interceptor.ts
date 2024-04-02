import {HttpClient, HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {BehaviorSubject, Observable, of, throwError} from 'rxjs';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {AuthenticationService} from '../services/authentication.service';
import {SnackBarService} from '../services/snack-bar.service';

/**
 * Code de retour HTTP : erreur métier
 */
const HTTP_CODE_BUSINESS_ERROR = 409;

/**
 * Temps d'affichage de la snackbar d'une business error
 */
const BUSINESS_ERROR_SNACKBAR_DURATION = 5000;

/**
 * Code de retour HTTP : token expiré
 */
const HTTP_CODE_TOKEN_EXPIRED = 498;

/**
 * Préfixe des clés de translate des erreurs métier
 */
const BUSINESS_ERROR_TRANSLATE_KEY_PREFIX = 'businessException';

/**
 * Wrapper sur le type <any>
 */
// tslint:disable-next-line:no-any : on utilise any car on intercepte TOUT type de requête
type HttpEventAny = HttpEvent<any>;

/**
 * Wrapper sur le type <any>
 */
// tslint:disable-next-line:no-any : on utilise any car on intercepte TOUT type de requête
type HttpRequestAny = HttpRequest<any>;

/**
 * Ajoute les informations d'authentification dans toutes les requêtes clientes
 */
@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {

    /**
     * URL de l'endpoint côté back pour le refresh token
     * @private
     */
    private static REFRESH_TOKEN_URL = '/refresh_token';

    /**
     * Sujet observant le statut : "Est-ce qu'un refresh token est en cours ?"
     * @private
     */
    private refreshIsRunning: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    constructor(private readonly router: Router,
                private readonly http: HttpClient,
                public dialog: MatDialog,
                private readonly authentificationService: AuthenticationService,
                private readonly translateService: TranslateService,
                private readonly snackBarService: SnackBarService) {
    }

    /**
     * Interception de toutes les requêtes HTTP
     * @param request la requête
     * @param next un handler
     */
    public intercept(request: HttpRequestAny, next: HttpHandler): Observable<HttpEventAny> {

        // On veut insérer le token JWT pour être authentifié avec les requêtes vers le back
        const requestWithHeaders = this.injectHeadersAndCloneRequest(request, next);

        // Si on fait un refresh on l'envoie direct
        if (request.url.includes(HttpTokenInterceptor.REFRESH_TOKEN_URL)) {
            return requestWithHeaders;
        }

        // Pour chaque autre appel HTTP on va voir si un refresh est en cours
        return this.waitForRefreshTokenOrDo(requestWithHeaders).pipe(
            // Mais on veut gérer les cas d'erreur de manière spécifique
            catchError((error: HttpErrorResponse) => this.handleHttpError(request, next, error)),
            // On map bien vers le bon type de retour
            map((project: HttpEventAny) => project)
        );
    }

    /**
     * Gestion d'une erreur HTTP provenant du backend RUDI
     * @param request la requête initiale
     * @param next le handler de l'erreur
     * @param error l'erreur HTTP
     */
    private handleHttpError(request: HttpRequestAny, next: HttpHandler, error: HttpErrorResponse): Observable<HttpEventAny> {

        // Si on a reçu une erreur métier
        if (error.status === HTTP_CODE_BUSINESS_ERROR) {
            const apiError = error.error;
            const translateKey = BUSINESS_ERROR_TRANSLATE_KEY_PREFIX + '.' + apiError.code;
            let errorMessage = this.translateService.instant(translateKey);
            if (errorMessage == null || errorMessage === '' || errorMessage === translateKey) {
                errorMessage = apiError.label;
            }

            this.snackBarService.openSnackBar({
                message: errorMessage,
                level: Level.ERROR,
            }, BUSINESS_ERROR_SNACKBAR_DURATION);
            return of();
        }
        // Si le token de validité a expiré et qu'on est pas entrain de refresh le token
        else if (error.status === HTTP_CODE_TOKEN_EXPIRED && !this.refreshIsRunning.getValue()) {

            // On doit effacer le token côté front
            AuthenticationService.clearAuhtenticatedTokenInSession();

            // on demande à refresh notre token, puis on relance l'appel initial
            return this.refreshToken().pipe(
                switchMap(() => this.injectHeadersAndCloneRequest(request, next)),
                map((project: HttpEventAny) => project)
            );
        }
        // Si le token a expiré MAIS que une requête de refresh a déjà été lancée du front
        else if (error.status === HTTP_CODE_TOKEN_EXPIRED && this.refreshIsRunning.getValue()) {

            // On attend que le refresh se finisse pour traiter la requête initiale
            return this.waitForRefreshTokenOrDo(of(request))
                .pipe(
                    // Le refresh s'est finit, donc on clone la requête initiale avec les nouveaux tokens JWT
                    switchMap((requestDelayed: HttpRequestAny) => this.injectHeadersAndCloneRequest(requestDelayed, next)),
                    map((clonedResult: HttpEventAny) => clonedResult)
                );
        }

        // toute autre erreur est gérée normalement
        return throwError(() => error);
    }

    /**
     * Prend la requête en entrée puis injecete les HEADERS pour être authentifié
     * @param request la requête de base
     * @param next le handler de la requête
     * @private
     */
    private injectHeadersAndCloneRequest(request: HttpRequestAny, next: HttpHandler): Observable<HttpEventAny> {
        return next.handle(
            request.clone({
                setHeaders: this.authentificationService.getHeadersForRequestInjection(),
                withCredentials: true
            })
        );
    }

    /**
     * Appel REST pour demander un refresh token
     * @private
     */
    private refreshToken(): Observable<void> {
        this.refreshIsRunning.next(true);
        return this.http.get(
            HttpTokenInterceptor.REFRESH_TOKEN_URL,
            {
                headers: this.authentificationService.getHeadersForRefreshToken(),
                observe: 'response',
                reportProgress: false,
                withCredentials: false
            }
        ).pipe(
            catchError((error) => {
                this.refreshIsRunning.next(false);
                return throwError(() => error);
            }),
            tap(() => this.refreshIsRunning.next(false)),
            map(null)
        );
    }

    /**
     * Vérifie si un refresh token est en cours, si oui, on attend la fin du refresh, sinon on fait l'appel
     * @param observable l'appel REST voulant être fait
     * @private
     */
    private waitForRefreshTokenOrDo(observable: Observable<unknown>): Observable<unknown> {

        // Est-ce que le refresh est en cours ?
        if (this.refreshIsRunning.getValue() === true) {

            // Oui, alors on va attendre la prochaine émission de changement de statut
            // sur le "refresh en cours", et quand l'émission vaut : "plus en cours"
            // on reprend le traitement initial
            return this.refreshIsRunning.asObservable().pipe(
                filter((isRunning: boolean) => {
                    return isRunning === false;
                }),
                take(1),
                switchMap(() => observable)
            );
        } else {
            return observable;
        }
    }
}
