import {Injectable} from '@angular/core';
import {UserService} from '@core/services/user.service';
import {TranslateService} from '@ngx-translate/core';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {Credentials as SelfdataCredentials} from 'micro_service_modules/api-apimaccess';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {SelfdataService} from 'micro_service_modules/selfdata/selfdata-api';
import {OrganizationService} from 'micro_service_modules/strukture/api-strukture';
import {Observable, throwError} from 'rxjs';
import {AbstractApiAccessService} from '../abstract-api-access.service';
import {Credentials} from '../credentials';
import {SubscriptionData} from '../subscription-data';

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
            return throwError(() => Error('Impossible de vérifier la souscription si on ne propose pas de JDD'));
        }
        return this.konsultService.hasSubscribeToSelfdataDataset(subscriptionData.metadata.global_id);
    }

    subscribeToDataset(subscriptionData: SubscriptionData): Observable<unknown> {
        if (subscriptionData.metadata == null) {
            return throwError(() => Error('Impossible de souscrire si on ne propose pas de JDD'));
        }
        return this.konsultService.subscribeToSelfdataDataset(subscriptionData.metadata.global_id);
    }
}
