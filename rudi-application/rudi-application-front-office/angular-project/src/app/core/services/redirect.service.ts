import {Injectable} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RouteHistoryService} from './route-history.service';

/**
 * Permet de revenir vers une route stockée dans le query parameter <code>'redirectTo'</code>.
 */
@Injectable({
    providedIn: 'root'
})
export class RedirectService {

    /**
     * Route vers laquelle aller lorsqu'on demande de suivre la redirection. Exemple : <code>'/projets/declarer-une-reutilisation'</code>
     */
    redirectTo: string;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private routeHistoryService: RouteHistoryService
    ) {
        this.activatedRoute.queryParamMap.subscribe(queryParamMap => {
            this.redirectTo = queryParamMap.get('redirectTo');
        });
    }

    static getQueryParamsToRedirectTo(route: string): Params {
        return {
            redirectTo: route
        };
    }
    goToTop(): void {
        window.scroll({
            top: 0,
            left: 0,
            behavior: 'smooth'
        });
    }

    /**
     * Suit la route indiquée dans le query param <code>redirectRoute</code> si présent,
     * retour arrière dans l'historique sinon
     * @see RouteHistoryService
     */
    followRedirectOrGoBack(): Promise<boolean> {
        if (this.redirectTo) {
            const redirectUrlTree = this.router.parseUrl(this.redirectTo);
            return this.router.navigateByUrl(redirectUrlTree);
        } else {
            return this.routeHistoryService.goBackOrElseGoAccount();
        }
    }
}
