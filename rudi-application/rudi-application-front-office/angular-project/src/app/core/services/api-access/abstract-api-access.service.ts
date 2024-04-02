import {HttpErrorResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {ErrorWithCause} from '@shared/models/error-with-cause';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {User} from 'micro_service_modules/acl/acl-model';
import {OwnerType} from 'micro_service_modules/konsent/konsent-model';
import {ApiKeys, ApiKeysType, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Project, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {OrganizationService} from 'micro_service_modules/strukture/api-strukture';
import {from, Observable, of, throwError} from 'rxjs';
import {catchError, filter, map, mapTo, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {LinkedDatasetMetadatas} from '../asset/project/project-dependencies.service';
import {UserService} from '@core/services/user.service';
import {Credentials} from './credentials';
import {LinkWithSubscribability} from './link-with-subscribability';
import {SubscriptionData} from './subscription-data';
import {SubscriptionRequestReport} from './subscription-request-report';
import {SubscriptionRequestResult} from './subscription-request-result';
import {SubscriptionRequestStatus} from './subscription-request-status.enum';

/**
 * Nombre maximal de souscriptions qu'on peut appeler en parallèle
 */
const MAX_CONCURRENT_SUBSCRIPTION = 5;

export abstract class AbstractApiAccessService {

    protected constructor(
        protected readonly userService: UserService,
        protected readonly aclService: AclService,
        protected readonly konsultService: KonsultService,
        protected readonly projektService: ProjektService,
        protected readonly organizationService: OrganizationService,
        protected readonly translateService: TranslateService
    ) {

    }

    /**
     * Récupération du consumer key et consumer secret pour le projet de l'espace personnel consulté
     * effectue les différentes opérations de vérification / création des accès WSO2
     * @param password le mot de passe USER ou ORGANISATION
     * @param ownerType utile pour savoir si celui qui fait l'action le fait en son propre compte ou au nom de son organisation
     * @param ownerUuid uuid du porteur du projet (peut correspondre à un user classique ou une organisation)
     */
    getConsumerKeys(password: string, ownerType: OwnerType, ownerUuid: string): Observable<ApiKeys> {
        // Récupération des crédentials pour démarrer la chaîne
        return this.enableApiForSubscribtion(password, ownerType, ownerUuid).pipe(
            switchMap((credentials: Credentials) => {
                // on a les accès on récupère les clés
                return this.getKeys(ApiKeysType.Production, credentials);
            })
        );
    }

    /**
     * Récupère les credentials à utiliser pour les API access en fonction du contexte
     * @param password mot de passe lié au contexte (USER ou ORGANISATION)
     * @param ownerType utile pour savoir si celui qui fait l'action le fait en son propre compte ou au nom de son organisation
     * @param ownerUuid uuid du porteur du projet (peut correspondre à un user classique ou une organisation)
     * @private
     */
    getCredentialsForContext(password: string, ownerType: OwnerType, ownerUuid: string): Observable<Credentials> {

        if (!password) {
            return throwError('Paramètres obligatoires manquants');
        }

        // On veut récupérer le login pour vérifier les accès
        let loginObs: Observable<string>;

        // Soit le login USER dans le cadre d'un projet porté par un user
        if (ownerType === OwnerType.User) {
            loginObs = this.userService.getConnectedUser().pipe(
                map((user: User) => {
                    if (!user) {
                        throw Error('Aucun utilisateur actuellement connecté');
                    }

                    if (user.uuid !== ownerUuid) {
                        throw Error('Erreur l\'utilisateur connecté n\'est pas le porteur du projet actuel');
                    }

                    return user.login;
                })
            );
        }
        // Soit le login ORGANISATION dans le cadre d'un projet porté par une organisation
        else if (ownerType === OwnerType.Organization) {
            loginObs = this.organizationService.getOrganizationUserFromOrganizationUuid(ownerUuid).pipe(
                map((user: User) => {
                    if (!user) {
                        throw Error('Impossible de récupérer un utilisateur pour l\'organisation responsable du projet');
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
     * @param subscriptionDatas les éléments pour réaliser la souscription
     * @param ownerType utile pour savoir si celui qui fait l'action le fait en son propre compte ou au nom de son organisation
     * @param ownerUuid uuid du porteur du projet (peut correspondre à un user classique ou une organisation)
     */
    public doSubscriptionProcessToDatasets(password: string, subscriptionDatas: SubscriptionData[],
                                           ownerType: OwnerType = null, ownerUuid: string = null): Observable<SubscriptionRequestReport> {

        return this.enableApiForSubscribtion(password, ownerType, ownerUuid).pipe(
            switchMap(() => {
                return from(subscriptionDatas).pipe(
                    mergeMap((subscriptionData: SubscriptionData) => {
                        return this.doSubscribeToDataset(subscriptionData).pipe(
                            catchError((subscribeError: Error) => {
                                return of(new SubscriptionRequestResult(subscriptionData.metadata, SubscriptionRequestStatus.FAILURE, subscribeError));
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
     * @param subscriptionData les données nécessaires pour réaliser la souscription
     */
    public doSubscribeToDataset(subscriptionData: SubscriptionData): Observable<SubscriptionRequestResult> {

        return this.hasSubscribedToDataset(subscriptionData).pipe(
            catchError((hasSubscribeError: HttpErrorResponse) => {
                throw new ErrorWithCause('Erreur lors de la vérification de la souscription au JDD', hasSubscribeError);
            }),
            switchMap((hasSubscribed: boolean) => {
                if (!hasSubscribed) {
                    return this.subscribeToDataset(subscriptionData).pipe(
                        // Si erreur HTTP pendant la souscription stop chaînage
                        catchError((subscribeError: HttpErrorResponse) => {
                            throw new ErrorWithCause('Erreur lors de la souscription au JDD', subscribeError);
                        }),

                        mapTo(new SubscriptionRequestResult(subscriptionData.metadata, SubscriptionRequestStatus.SUCCESS))
                    );
                }

                return of(new SubscriptionRequestResult(subscriptionData.metadata, SubscriptionRequestStatus.IGNORED));
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
                return this.projektService.hasAccessToDataset(project.owner_uuid, linkAndMetadata.dataset.global_id).pipe(
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
     * @param ownerType utile pour savoir si celui qui fait l'action le fait en son propre compte ou au nom de son organisation
     * @param ownerUuid uuid du porteur du projet (peut correspondre à un user classique ou une organisation)
     */
    private enableApiForSubscribtion(password: string, ownerType: OwnerType, ownerUuid: string): Observable<Credentials> {

        // Récupération des crédentials pour démarrer la chaîne
        let credentials: Credentials;
        return this.getCredentialsForContext(password, ownerType, ownerUuid).pipe(
            switchMap((credentialsRetrieved: Credentials) => {
                // 1) Check si on a les accès aux APIs pour ce contexte
                credentials = credentialsRetrieved;
                return this.hasEnabledApi(credentials).pipe(
                    catchError((error: HttpErrorResponse) => this.handleApiAccessError(error))
                );
            }),
            switchMap((hasAccess: boolean) => {
                // 2) si on a pas les accès on les créé
                if (!hasAccess) {
                    return this.enableApi(credentials).pipe(
                        catchError((error: HttpErrorResponse) => this.handleApiAccessError(error)),
                        mapTo(credentials)
                    );
                }
                // Si on les a go étape 3
                return of(credentials);
            })
        );
    }

    /**
     * Lorsque les appels vers API-ACCESS renvoient 403, ons ait que c'est une erreur de login/mot de passe
     * @param httpError l'erreur HTTP reçue
     * @private
     */
    private handleApiAccessError(httpError: HttpErrorResponse): Observable<never> {
        console.error(httpError);

        if (httpError.status === 403) {
            const errorMessage = this.translateService.instant('personalSpace.apiAccess.errorPassword');
            return throwError(new ErrorWithCause(errorMessage, httpError));
        }

        throw Error('Erreur lors de la vérification des accès API pour l\'utilisateur connecté');
    }

    abstract hasEnabledApi(credentials: Credentials): Observable<boolean>;

    abstract enableApi(credentials: Credentials): Observable<void>;

    abstract hasSubscribedToDataset(subscriptionData: SubscriptionData): Observable<boolean>;

    abstract subscribeToDataset(subscriptionData: SubscriptionData): Observable<unknown>;

    private getKeys(apiKeys: ApiKeysType, credentials: Credentials): Observable<ApiKeys> {
        return this.konsultService.getKeys(apiKeys, credentials);
    }
}
