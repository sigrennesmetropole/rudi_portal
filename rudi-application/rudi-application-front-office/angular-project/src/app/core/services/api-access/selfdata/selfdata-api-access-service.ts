import {Injectable} from '@angular/core';
import {UserService} from '../../user.service';
import {AclService} from '../../../../acl/acl-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {TranslateService} from '@ngx-translate/core';
import {AbstractApiAccessService} from '../abstract-api-access.service';
import {Credentials} from '../credentials';
import {Observable, throwError} from 'rxjs';
import {SelfdataService} from '../../../../selfdata/selfdata-api';
import { Credentials as SelfdataCredentials} from '../../../../api-apimaccess';
import {SubscriptionData} from '../subscription-data';
import {KonsultService} from '../../../../konsult/konsult-api';
import {ProjektService} from '../../../../projekt/projekt-api';

@Injectable({
    providedIn: 'root'
})
export class SelfdataApiAccessService extends AbstractApiAccessService {

    private static toSelfdataCredentials(credentials: Credentials): SelfdataCredentials {
        return {
            login: credentials.login,
            password: credentials.password
        } as SelfdataCredentials;
    }

    constructor(
        userService: UserService,
        aclService: AclService,
        konsultService: KonsultService,
        projektService: ProjektService,
        organizationService: OrganizationService,
        translateService: TranslateService,
        private readonly selfdataService: SelfdataService) {
        super(userService, aclService, konsultService, projektService, organizationService, translateService);
    }

    enableApi(credentials: Credentials): Observable<void> {
        return this.selfdataService.enableApi(SelfdataApiAccessService.toSelfdataCredentials(credentials));
    }

    hasEnabledApi(credentials: Credentials): Observable<boolean> {
        return this.selfdataService.hasEnabledApi(SelfdataApiAccessService.toSelfdataCredentials(credentials));
    }

    hasSubscribedToDataset(subscriptionData: SubscriptionData): Observable<boolean> {
        if (subscriptionData.metadata == null) {
            return throwError(Error('Impossible de vérifier la souscription si on ne propose pas de JDD'));
        }
        return this.konsultService.hasSubscribeToSelfdataDataset(subscriptionData.metadata.global_id);
    }

    subscribeToDataset(subscriptionData: SubscriptionData): Observable<unknown> {
        if (subscriptionData.metadata == null) {
            return throwError(Error('Impossible de souscrire si on ne propose pas de JDD'));
        }
        return this.konsultService.subscribeToSelfdataDataset(subscriptionData.metadata.global_id);
    }
}
