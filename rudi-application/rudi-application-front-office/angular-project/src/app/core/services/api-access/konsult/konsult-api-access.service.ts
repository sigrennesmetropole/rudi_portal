import {Injectable} from '@angular/core';
import {UserService} from '../../user.service';
import {AclService} from '../../../../acl/acl-api';
import {Credentials as KonsultCredentials, KonsultService} from '../../../../api-konsult';
import {ProjektService} from '../../../../projekt/projekt-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {TranslateService} from '@ngx-translate/core';
import {AbstractApiAccessService} from '../abstract-api-access.service';
import {Credentials} from '../credentials';
import {Observable, throwError} from 'rxjs';
import {SubscriptionData} from '../subscription-data';

@Injectable({
    providedIn: 'root'
})
export class KonsultApiAccessService extends AbstractApiAccessService {

    private static toKonsultCredentials(credentials: Credentials): KonsultCredentials {
        return {
            login: credentials.login,
            password: credentials.password
        } as KonsultCredentials;
    }

    constructor(
        userService: UserService,
        aclService: AclService,
        konsultService: KonsultService,
        projektService: ProjektService,
        organizationService: OrganizationService,
        translateService: TranslateService) {
        super(userService, aclService, konsultService, projektService, organizationService, translateService);
    }

    enableApi(credentials: Credentials): Observable<void> {
        return this.konsultService.enableApi(KonsultApiAccessService.toKonsultCredentials(credentials));
    }

    hasEnabledApi(credentials: Credentials): Observable<boolean> {
        return this.konsultService.hasEnabledApi(KonsultApiAccessService.toKonsultCredentials(credentials));
    }

    hasSubscribedToDataset(subscriptionData: SubscriptionData): Observable<boolean> {

        if (subscriptionData.metadata == null) {
            return throwError(Error('Impossible de vérifier la souscription si on ne propose pas de JDD'));
        }

        if (subscriptionData.linkedDataset == null) {
            return throwError(Error('Impossible de vérifier la souscription à un JDD sans fournir une demande'));
        }

        return this.konsultService.hasSubscribeToLinkedDataset(subscriptionData.metadata.global_id, subscriptionData.linkedDataset.uuid);
    }

    subscribeToDataset(subscriptionData: SubscriptionData): Observable<unknown> {

        if (subscriptionData.metadata == null) {
            return throwError(Error('Impossible de souscrire si on ne propose pas de JDD'));
        }

        if (subscriptionData.linkedDataset == null) {
            return throwError(Error('Impossible de souscrire à un JDD sans fournir une demande'));
        }

        return this.konsultService.subscribeToLinkedDataset(subscriptionData.metadata.global_id, subscriptionData.linkedDataset.uuid);
    }
}
