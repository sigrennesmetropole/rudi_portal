import {Injectable} from '@angular/core';
import {UserService} from '../../user.service';
import {AclService} from '../../../../acl/acl-api';
import {KonsultService} from '../../../../api-konsult';
import {ProjektService} from '../../../../projekt/projekt-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {TranslateService} from '@ngx-translate/core';
import {AbstractApiAccessService} from '../abstract-api-access.service';
import {Credentials} from '../credentials';
import {Observable} from 'rxjs';
import {SelfdataService, Credentials as SelfdataCredentials} from '../../../../selfdata/selfdata-api';

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
}
