import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {PropertiesAdapter} from './properties-adapter';
import {FrontOfficeProperties, KonsultService} from '../../konsult/konsult-api';

@Injectable({
    providedIn: 'root'
})
export class PropertiesMetierService extends PropertiesAdapter<FrontOfficeProperties> {

    constructor(
        private readonly konsultService: KonsultService,
    ) {
        super();
    }

    protected fetchBackendProperties(): Observable<FrontOfficeProperties> {
        return this.konsultService.getFrontOfficeProperties();
    }
}
