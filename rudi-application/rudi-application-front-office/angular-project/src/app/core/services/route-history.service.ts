import {Injectable} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs/operators';
import {AuthenticationService} from './authentication.service';

@Injectable({
    providedIn: 'root'
})
export class RouteHistoryService {

    /**
     * L'historique des routes (à la profondeur 1, juste la page d'avant)
     * @private
     */
    private historique: NavigationEnd[] = [];

    /**
     * Constructeur du service : définit le comportement de gestion de l'historique à l'aide d'un observable
     * @param router service angular pour intéréagir avec les routes
     * @param authenticationService service de gestion de l'authent
     */
    constructor(private router: Router,
                private authenticationService: AuthenticationService) {
        // On observe les évènements de changement de route
        this.router.events.pipe(
            // On regarde juste les event de chargement de page = page chargée
            filter(e => e instanceof NavigationEnd)

            // pour chaque évènement
        ).subscribe((e: NavigationEnd) => {

            // On ajoute la nouvelle route chargée
            this.historique.push(e);

            // On se limite au niveau d'avant (l'historique se compose de la route actuelle et la route d'avant)
            if (this.historique.length > 2) {
                this.historique.shift();
            }
        });
    }

    /**
     * Est-ce que le contexte actuel permet de faire un go back
     */
    private isAbleToGoBack(): boolean {
        // Est-ce qu'on a au moins un historique
        return this.historique.length > 1;
    }

    /**
     * On demande aus ervie d'historique de naviguer vers la précédente page
     */
    private goBack(): Promise<boolean> {
        // Si on a les infos sur la page précédente
        if (this.isAbleToGoBack()) {
            // On reconstruit l'URL de destination
            // Si plus tard route avec paramètre GET peut-être que NavigationEnd ne conviendrait pas
            const url = this.historique[0].url;

            // On navigue vers la route à partir de la racine
            return this.router.navigate(['/' + url]);

            // Si on a pas les infos sur la page précédente on log le fait que c'est impossible
        } else {
            console.error('Impossible de naviguer vers la page précédente : historique de navigation vide');
            return Promise.resolve(false);
        }
    }

    /**
     * Navigation vers la page précédente sinon vers vers la page account
     */
    goBackOrElseGoAccount(): Promise<boolean> {
        // On go back si le service en est capbable
        if (this.isAbleToGoBack()) {
            return this.goBack();
        }
        // Sinon go /catalogue si on est connecté
        else {
            return this.router.navigate(['/catalogue']);
        }
    }

    resetHistory() {
        this.historique = [];
    }
}
