import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {FrontOfficeProperties, KonsultService} from '../../api-konsult';
import {PropertiesAdapter} from './properties-adapter';

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
