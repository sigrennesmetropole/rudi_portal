import {Injectable} from '@angular/core';
import {EMPTY, Observable, of} from 'rxjs';
import {AclService, User} from '../../acl/acl-api';
import {AuthenticationService} from './authentication.service';
import {switchMap} from 'rxjs/operators';
import {ANONYMOUS_USERNAME} from './authentication/anonymous-authentication';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    /**
     * Permet de récupérer l'évènement d'authentification
     */
    public readonly knownUser$: Observable<User>;

    /**
     * Constructeur
     * @param aclService
     * @param authenticationService
     */
    constructor(private readonly aclService: AclService,
                private readonly authenticationService: AuthenticationService) {
        this.knownUser$ = this.authenticationService.isAuthenticated$.pipe(
            switchMap(isAuthenticated => isAuthenticated ? this.getConnectedUser() : EMPTY)
        );
    }

    /**
     * Getter Utilisateur connecté
     * @returns {User}
     */
    getConnectedUser(): Observable<User> {
        return this.aclService.getMe().pipe(
            switchMap(UserService.getUserAsKnownUser)
        );
    }

    private static getUserAsKnownUser(user: User | undefined): Observable<User> {
        return of(UserService.isKnownUser(user) ? user : undefined);
    }

    /**
     * Permet de savoir si l'utilisateur est non anonyme
     * @param user
     * @private
     */
    private static isKnownUser(user: User | undefined): boolean {
        return user && user.login !== ANONYMOUS_USERNAME;
    }
}
