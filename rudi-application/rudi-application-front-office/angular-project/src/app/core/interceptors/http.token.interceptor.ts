import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {AuthenticationService} from '../services/authentication.service';
import {MatDialog} from '@angular/material/dialog';
import {catchError} from 'rxjs/operators';

/**
 * Code de retour HTTP : token expiré
 */
const HTTP_CODE_TOKEN_EXPIRED = 498;

/**
 * Ajoute les informations d'authentification dans toutes les requêtes clientes
 */
@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {

    constructor(private readonly router: Router,
                public dialog: MatDialog,
                private readonly authentificationService: AuthenticationService) {
    }

    /**
     * Interception de toutes les requêtes HTTP
     * @param request la requête
     * @param next un handler
     */
    // tslint:disable-next-line:no-any : on utilise any car on intercepte TOUT type de requête
    public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        // On veut insérer le token JWT pour être authentifié avec les requêtes vers le back
        return next.handle(
            request.clone({
                setHeaders: this.authentificationService.getHeadersForRequestInjection(),
                withCredentials: true
            })
        )
            // Mais on veut gérer les cas d'erreur de manière spécifique
            .pipe(
                catchError((error: HttpErrorResponse) => {

                    // Si le token de validité a expiré
                    if (error.status === HTTP_CODE_TOKEN_EXPIRED) {
                        // On doit effacer le token côté front
                        AuthenticationService.clearAuhtenticatedTokenInSession();

                        // on fait la demande interceptée avec en + une demande de refresh token au back
                        // le serveur s'occupe de nous jeter ou pas
                        return next.handle(request.clone({setHeaders: this.authentificationService.getHeadersForRefreshToken()}));
                    }

                    return throwError(error);
                })
        );
    }
}
