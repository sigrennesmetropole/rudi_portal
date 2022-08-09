import {Injectable} from '@angular/core';
import {from, Observable, of, throwError} from 'rxjs';
import {OwnerType, Project, ProjektService} from '../../projekt/projekt-api';
import {UserService} from './user.service';
import {catchError, filter, map, mapTo, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {AclService, User} from '../../acl/acl-api';
import {ApiKeys, ApiKeysType, Credentials, KonsultService} from '../../api-konsult';
import {HttpErrorResponse} from '@angular/common/http';
import {OrganizationService} from '../../strukture/api-strukture';
import {Metadata} from '../../api-kaccess';
import {ErrorWithCause} from '../../shared/models/error-with-cause';
import {LinkedDatasetMetadatas} from './project-dependencies.service';

/**
 * Nombre maximal de souscriptions qu'on peut appeler en parallèle
 */
const MAX_CONCURRENT_SUBSCRIPTION = 5;

/**
 * Les différents états de tentative de souscription à un JDD
 */
enum SubscriptionRequestStatus {
    SUCCESS, IGNORED, FAILURE
}

/**
 * Un résultat de requête de souscription
 */
export class SubscriptionRequestResult {

    /**
     * Le JDD auquel on a tenté de souscrire
     */
    metadata: Metadata;

    /**
     * L'état de la souscription
     */
    success: SubscriptionRequestStatus;

    /**
     * L'erreur ayant eu lieu si FAILED
     */
    error: Error;

    /**
     * Constructeur objet : tentative de souscription un JDD
     * @param metadata le JDD auquel on a tenté de souscrire
     * @param success le statut de la tentative
     * @param error erreur si échec
     */
    constructor(metadata: Metadata, success: SubscriptionRequestStatus, error?: Error) {
        this.metadata = metadata;
        this.success = success;
        this.error = error;
    }
}

/**
 * Un rapport de souscriptions à un ensembles de JDDs
 */
export class SubscriptionRequestReport {

    /**
     * Les JDDs auxquels on a réussi à souscrire
     */
    subscribed: SubscriptionRequestResult[] = [];

    /**
     * Les JDDS auxquels on avait déjà souscrit
     */
    ignored: SubscriptionRequestResult[] = [];

    /**
     * Les JDDs pour lesquels on a pas réussi à souscrire
     */
    failed: SubscriptionRequestResult[] = [];
}

/**
 * Wrapper pour savoir si l'utilisateur peut souscrire à un JDD
 */
export class LinkWithSubscribability {

    /**
     * le JDD lié à la demande pour la souscription
     */
    link: LinkedDatasetMetadatas;

    /**
     * Est-ce que l'user peut souscrire à ce JDD
     */
    canSubscribe: boolean;

    /**
     * Constructeur paramétré
     * @param link objet enrichi wrappé
     * @param canSubscribe si on peut souscrire ou pas
     */
    constructor(link: LinkedDatasetMetadatas, canSubscribe: boolean) {
        this.link = link;
        this.canSubscribe = canSubscribe;
    }
}

@Injectable({
    providedIn: 'root'
})
export class ApiAccessService {

    constructor(
        private readonly userService: UserService,
        private readonly aclService: AclService,
        private readonly konsultService: KonsultService,
        private readonly projektService: ProjektService,
        private readonly organizationService: OrganizationService
    ) {

    }

    /**
     * Récupération du consumer key et consumer secret pour le projet de l'espace personnel consulté
     * effectue les différentes opérations de vérification / création des accès WSO2
     * @param password le mot de passe USER ou ORGANISATION
     * @param myProject le projet personnel pour lequel on souhaite récupérer les accès
     */
    getConsumerKeys(password: string, myProject: Project): Observable<ApiKeys> {
        // Récupération des crédentials pour démarrer la chaîne
        return this.enableApiForSubscribtion(password, myProject).pipe(
            switchMap((credentials: Credentials) => {
                // on a les accès on récupère les clés
                return this.getKeys(ApiKeysType.Production, credentials);
            })
        );
    }

    /**
     * Récupère les credentials à utiliser pour les API access en fonction du contexte
     * @param password mot de passe lié au contexte (USER ou ORGANISATION)
     * @param myProject le projet qui définit le contexte
     * @private
     */
    getCredentialsForContext(password: string, myProject: Project): Observable<Credentials> {

        if (!password || !myProject) {
            return throwError('Paramètres obligatoires manquants');
        }

        // On veut récupérer le login pour vérifier les accès
        let loginObs: Observable<string>;

        // Soit le login USER dans le cadre d'un projet porté par un user
        if (myProject.owner_type === OwnerType.User) {
            loginObs = this.userService.getConnectedUser().pipe(
                map((user: User) => {
                    if (!user) {
                        throw Error('Aucun utilisateur actuellement connecté');
                    }

                    if (user.uuid !== myProject.owner_uuid) {
                        throw Error('Erreur l\'utilisateur connecté n\'est pas le porteur du projet actuel');
                    }

                    return user.login;
                })
            );
        }
        // Soit le login ORGANISATION dans le cadre d'un projet porté par une organisation
        else if (myProject.owner_type === OwnerType.Organization) {
            loginObs = this.organizationService.getOrganizationUserFromOrganizationUuid(myProject.owner_uuid).pipe(
                map((user: User) => {
                    if (!user) {
                        throw Error('L\'utilisateur de l\'organisation n\'existe pas');
                    }

                    return user.login;
                })
            );
        }

        // Retour d'un objet credentials
        return loginObs.pipe(map((login: string) => {
            return {login, password};
        }));
    }

    /**
     * Réalise la souscription aux média des JDDs fournis
     * @param password le mot de passe utilisateur OU organisation
     * @param project le projet dans lequel on est pour souscrire
     * @param metadatas les JDDs choisis pour souscrire
     */
    public doSubscriptionProcessToDatasets(password: string, project: Project,
                                           metadatas: Metadata[]): Observable<SubscriptionRequestReport> {

        return this.enableApiForSubscribtion(password, project).pipe(
            catchError((error: Error) => {
                throw new ErrorWithCause('Une erreur a eu lieu lors de l\'activation des accès aux APIs', error);
            }),
            switchMap(() => {
                return from(metadatas).pipe(
                    mergeMap((metadata: Metadata) => {
                        return this.subscribeToDataset(metadata).pipe(
                            catchError((subscribeError: Error) => {
                                return of(new SubscriptionRequestResult(metadata, SubscriptionRequestStatus.FAILURE, subscribeError));
                            })
                        );
                    }, MAX_CONCURRENT_SUBSCRIPTION),
                    reduce((report: SubscriptionRequestReport, result: SubscriptionRequestResult) => {
                        if (result.success === SubscriptionRequestStatus.SUCCESS) {
                            report.subscribed.push(result);
                        } else if (result.success === SubscriptionRequestStatus.IGNORED) {
                            report.ignored.push(result);
                        } else if (result.success === SubscriptionRequestStatus.FAILURE) {
                            report.failed.push(result);
                        }
                        return report;
                    }, new SubscriptionRequestReport())
                );
            })
        );
    }

    /**
     * Effectue une souscription aux médias d'un jeu de données
     * @param metadata le JDD à souscrire
     */
    public subscribeToDataset(metadata: Metadata): Observable<SubscriptionRequestResult> {

        return this.konsultService.hasSubscribeToDataset(metadata.global_id).pipe(
            catchError((hasSubscribeError: HttpErrorResponse) => {
                throw new ErrorWithCause('Erreur lors de la vérification de la souscription au JDD', hasSubscribeError);
            }),
            switchMap((hasSubscribed: boolean) => {
                if (!hasSubscribed) {
                    return this.konsultService.subscribeToDataset(metadata.global_id).pipe(
                        // Si erreur HTTP pendant la souscription stop chaînage
                        catchError((subscribeError: HttpErrorResponse) => {
                            throw new ErrorWithCause('Erreur lors de la souscription au JDD', subscribeError);
                        }),

                        mapTo(new SubscriptionRequestResult(metadata, SubscriptionRequestStatus.SUCCESS))
                    );
                }

                return of(new SubscriptionRequestResult(metadata, SubscriptionRequestStatus.IGNORED));
            })
        );
    }

    /**
     * Filtre les demandes fournies pour ne récupérer que les demandes concernant des JDDs auxquels on peut souscrire
     * @param linkAndMetadatas l'ensemble des demandes + JDD d'un projet
     * @param project le projet pour checker si on a les droits pour souscrire
     */
    public filterSubscribableMetadatas(linkAndMetadatas: LinkedDatasetMetadatas[], project: Project): Observable<LinkedDatasetMetadatas[]> {
        return from(linkAndMetadatas).pipe(
            mergeMap((linkAndMetadata: LinkedDatasetMetadatas) => {
                return this.projektService.checkOwnerHasAccessToDataset(project.owner_uuid, linkAndMetadata.dataset.global_id).pipe(
                    map((hasAccess: boolean) => new LinkWithSubscribability(linkAndMetadata, hasAccess))
                );
            }),
            filter((linkWithSubscribability: LinkWithSubscribability) => linkWithSubscribability.canSubscribe),
            reduce((filteredLinkAndMetadatas: LinkedDatasetMetadatas[], curentLink: LinkWithSubscribability) => {
                filteredLinkAndMetadatas.push(curentLink.link);
                return filteredLinkAndMetadatas;
            }, [])
        );
    }

    /**
     * Active la possibilité de souscrire aux API, si le traitement a déjà été fait, l'activation n'est pas relancée
     * @param password le mot de passe de l'utilisateur WSO2
     * @param project le projet pour savoir quel utilisateur WSO2 est concerné par les accès
     */
    private enableApiForSubscribtion(password: string, project: Project): Observable<Credentials> {

        // Récupération des crédentials pour démarrer la chaîne
        let credentials: Credentials;
        return this.getCredentialsForContext(password, project).pipe(
            switchMap((credentialsRetrieved: Credentials) => {
                // 1) Check si on a les accès aux APIs pour ce contexte
                credentials = credentialsRetrieved;
                return this.hasEnabledApi(credentials).pipe(
                    catchError((httpError: Error) => {
                        console.error(httpError);
                        throw Error('Erreur lors de la vérification des accès API pour ce projet');
                    })
                );
            }),
            switchMap((hasAccess: boolean) => {
                // 2) si on a pas les accès on les créé
                if (!hasAccess) {
                    return this.enableApi(credentials).pipe(
                        catchError((httpError: HttpErrorResponse) => {
                            console.error(httpError);
                            throw Error('Erreur lors de la création des accès API pour ce projet');
                        }),
                        mapTo(credentials)
                    );
                }

                // Si on les a go étape 3
                return of(credentials);
            })
        );
    }

    private hasEnabledApi(credentials: Credentials): Observable<boolean> {
        return this.konsultService.hasEnabledApi(credentials);
    }

    private enableApi(credentials: Credentials): Observable<void> {
        return this.konsultService.enableApi(credentials);
    }

    private getKeys(apiKeys: ApiKeysType, credentials: Credentials): Observable<ApiKeys> {
        return this.konsultService.getKeys(apiKeys, credentials);
    }
}
