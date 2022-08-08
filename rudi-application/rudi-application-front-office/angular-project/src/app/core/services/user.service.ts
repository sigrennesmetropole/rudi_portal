import {Injectable} from '@angular/core';
import {EMPTY, forkJoin, Observable, of} from 'rxjs';
import {AclService, User} from '../../acl/acl-api';
import {AuthenticationService} from './authentication.service';
import {map, switchMap, take} from 'rxjs/operators';
import {AuthenticationState} from './authentication/authentication-method';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    /**
     * Constructeur
     */
    constructor(private readonly aclService: AclService,
                private readonly authenticationService: AuthenticationService) {
    }

    /**
     * Permet de renvoyer le user spécifique ou rien si le client est connecté en anonymous
     * @param user le user "technique" récupéré du back-end
     * @private
     */
    private getUserAsKnownUser(user: User | undefined): Observable<User> {
        return this.authenticationService.authenticationChanged$.pipe(
            map((state: AuthenticationState) => {
                return state === AuthenticationState.USER ? user : undefined;
            })
        );
    }

    /**
     * Demande au serveur un objet qui représente l'utilisateur "technique" actuellement connecté
     * comme le front peut être en mode anonymous et qu'on s'intéresse qu'à ceux "spécifiques"
     * la méthode renvoie "undefined" en cas d'appel anonymous
     * @see getConnectedUserOrEmpty
     */
    getConnectedUser(): Observable<User|undefined> {
        return this.aclService.getMe().pipe(
            switchMap((user: User) => this.getUserAsKnownUser(user)),
            // getUserAsKnownUser est un observable qui ne se complète jamais, donc pipe take(1) pour complete()
            take(1)
        );
    }

    /**
     * @return l'utilisateur connecté ou l'Observable EMPTY si aucun utilisateur n'est connecté
     * mais jamais undefined contrairement à {@link #getConnectedUser getConnectedUser}.
     * @see getConnectedUser
     */
    getConnectedUserOrEmpty(): Observable<User> {
        return this.getConnectedUser().pipe(
            switchMap(connectedUser => {
                if (connectedUser) {
                    return of(connectedUser);
                } else {
                    console.warn('Aucun utilisateur n\'est actuellement connecté');
                    return EMPTY;
                }
            }),
        );
    }

}
