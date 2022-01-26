import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthenticationService} from './authentication.service';
import {LogService} from './log.service';
import {catchError, switchMap} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AuthGuardService implements CanActivate, CanActivateChild {

    // Gestion d'un loader durant l'authentification
    public loader: boolean = false;

    constructor(
        public readonly authenticationService: AuthenticationService,
        private readonly router: Router,
        private readonly logService: LogService) {
    }

    /**
     * Accès aux urls de même niveau
     * Renvoie true si utilisateur a le droit et false pas le droit
     * @returns Observable<any>
     */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authenticationService.isAuthenticated$.pipe(
            switchMap(isAuthenticated => {
                if (isAuthenticated) {
                    return of(true);
                } else if (this.authenticationService.authenticationFailedForAnonymous()) {
                    return this.redirectToNotAuthorized();
                } else {
                    return this.authenticationService.authenticateAsAnonymous().pipe(
                        switchMap(anyValue => of(!!anyValue)),
                        catchError(error => {
                            this.logService.error('Authentication error', error);
                            return this.redirectToNotAuthorized();
                        })
                    );
                }
            })
        );
    }

    private redirectToNotAuthorized(): Observable<UrlTree> {
        return of(this.router.parseUrl('not-authorized'));
    }

    /**
     * Accès aux urls enfants même traitement que les url parents
     * @returns Observable<any>
     */
    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> |
        Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.canActivate(route, state);
    }


}
