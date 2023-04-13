import {Observable} from 'rxjs';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {AppInfo, MiscellaneousService} from '../../acl/acl-api';

@Injectable({
    providedIn: 'root'
})
export class AclConfigurationResolver implements Resolve<Observable<AppInfo>> {

    constructor(
        private readonly miscellaneousService: MiscellaneousService,
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AppInfo> {
        return this.miscellaneousService.getApplicationInformation();
    }
}
