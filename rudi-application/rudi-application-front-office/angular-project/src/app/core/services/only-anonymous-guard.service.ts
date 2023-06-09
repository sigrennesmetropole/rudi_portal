import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AuthenticationState} from './authentication/authentication-method';

@Injectable({
    providedIn: 'root'
})
export class OnlyAnonymousGuardService implements CanActivate, CanActivateChild {

    constructor(
        public readonly authenticationService: AuthenticationService,
        public readonly router: Router
    ) {
    }

    /**
     * Accès aux urls de même niveau
     * Renvoie true si utilisateur a le droit et false pas le droit
     * @returns Observable<any>
     */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> |
        Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authenticationService.authenticationChanged$
            .pipe(
                map((stateAuth: AuthenticationState) => {
                        // Je suis un utilisateur réel : j'accède
                        if (stateAuth === AuthenticationState.ANONYMOUS) {
                            return true;
                        }

                        // je suis déjà authentifié ? je retourne vers login/account
                        this.router.navigate(['/personal-space/my-account']).then();
                        return false;
                    }
                )
            );
    }

    /**
     * Accès aux urls enfants même traitement que les url parents
     * @returns Observable<any>
     */
    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
        Observable<boolean | UrlTree> |
        Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.canActivate(route, state);
    }
}
