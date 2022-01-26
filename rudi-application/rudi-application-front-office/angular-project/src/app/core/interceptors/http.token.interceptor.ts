import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {AuthenticationService} from '../services/authentication.service';
import {MatDialog} from '@angular/material/dialog';

/**
 * Ajoute les informations d'authentification dans toutes les requêtes clientes
 */
@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {
    constructor(private readonly router: Router,
                public dialog: MatDialog,
                private readonly authentificationService: AuthenticationService) {
    }


    public intercept(request: HttpRequest<any>, next: HttpHandler) {
        return next.handle(request.clone({
            setHeaders: this.authentificationService.getHeadersForAuthentication(),
            withCredentials: true
        }))
            .pipe(catchError((err: HttpErrorResponse) => {
                if (err.status === 403 || err.status === 401) {
                    // this.dialog.open(LoginDialogComponent, {});
                    this.router.navigate(['/home']);
                }
                return throwError(err);
            }));
    }
}
