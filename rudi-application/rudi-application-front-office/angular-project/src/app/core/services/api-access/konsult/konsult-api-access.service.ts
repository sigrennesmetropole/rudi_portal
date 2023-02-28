import {Injectable} from '@angular/core';
import {UserService} from '../../user.service';
import {AclService} from '../../../../acl/acl-api';
import {Credentials as KonsultCredentials, KonsultService} from '../../../../api-konsult';
import {ProjektService} from '../../../../projekt/projekt-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {TranslateService} from '@ngx-translate/core';
import {AbstractApiAccessService} from '../abstract-api-access.service';
import {Credentials} from '../credentials';
import {Observable} from 'rxjs';

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
}
