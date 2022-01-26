import {Injectable} from '@angular/core';
import {ProvidersService} from '../../providers/api-providers';
import {KindOfData} from '../../api-kmedia';
import {OrganizationMetierService} from './organization-metier.service';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProvidersMetierService extends OrganizationMetierService {

    constructor(private providersService: ProvidersService) {
        super();
    }

    protected downloadProducerMediaByType(providerUuid: string, kindOfData: KindOfData): Observable<Blob> {
        return this.providersService.downloadProviderMediaByType(providerUuid, KindOfData.Logo);
    }

}
