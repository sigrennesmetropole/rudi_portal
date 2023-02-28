import {Observable} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';

/**
 * Paramètres à fournir pour réaliser un traitement avec gestion d'erreur
 */
export interface ActionFallbackParams<T> {

    /**
     * l'action qu'on souhaite réaliser
     */
    action: Observable<T>;

    /**
     * Traitement à réaliser quand l'action a échouée
     */
    fallback: Observable<unknown>;

    /**
     * Message d'erreur à afficher quand l'action a échoué et que la fallback s'est bien passée
     */
    fallbackSuccessMessage: string;

    /**
     * Message d'erreur à afficher quand l'action ET la fallback ont échoués (pas de chance)
     */
    fallbackErrorMessage: string;
}

/**
 * Permet de réaliser une action et une autre action en cas d'échec de la première
 */
export class ActionFallbackUtils<T> {

    constructor(private readonly params: ActionFallbackParams<T>) {
    }

    /**
     * Réalise l'action configurée, en appelant une fallback en cas d'échec
     */
    doActionFallbackOnfailure(): Observable<T> {
        return this.params.action.pipe(
            catchError((actionError: Error) => {
                console.error(actionError);
                return this.params.fallback.pipe(
                    catchError((fallbackError: Error) => {
                        console.error(fallbackError);
                        throw Error(this.params.fallbackErrorMessage);
                    }),
                    tap(() => {
                        throw Error(this.params.fallbackSuccessMessage);
                    })
                );
            }),

            map((returned: T) => returned)
        );
    }
}
