import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AppInfo, MiscellaneousService} from 'micro_service_modules/acl/acl-api';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AclConfigurationResolver {

    constructor(
        private readonly miscellaneousService: MiscellaneousService,
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AppInfo> {
        return this.miscellaneousService.getApplicationInformation();
    }
}
