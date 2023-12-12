import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {AuthenticationService} from '../services/authentication.service';

@Injectable()
export class ResponseTokenInterceptor implements HttpInterceptor {
    constructor(
        private readonly authenticationService: AuthenticationService
    ) {
    }

    /**
     * Récupération du token rénouvelé dans le header de la reponse.
     */
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            tap((httpResponse: HttpEvent<any>) => {
                if (httpResponse instanceof HttpResponse) {
                    this.authenticationService.storeTokensFrom(httpResponse.headers);
                }
            })
        );
    }
}
