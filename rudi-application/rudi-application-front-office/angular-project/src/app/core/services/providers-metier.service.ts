import {Injectable} from '@angular/core';
import {OrganizationService, ProvidersService} from '../../strukture/api-strukture';
import {KindOfData} from '../../api-kmedia';
import {OrganizationMetierService} from './organization/organization-metier.service';
import {Observable} from 'rxjs';
import {ImageLogoService} from './image-logo.service';

@Injectable({
    providedIn: 'root'
})
export class ProvidersMetierService extends OrganizationMetierService {

    constructor(protected imageLogoService: ImageLogoService,
                private providersService: ProvidersService,
                protected organizationService: OrganizationService) {
        super(imageLogoService, organizationService);
    }

    protected downloadProducerMediaByType(providerUuid: string, kindOfData: KindOfData): Observable<Blob> {
        return this.providersService.downloadProviderMediaByType(providerUuid, KindOfData.Logo);
    }

}
