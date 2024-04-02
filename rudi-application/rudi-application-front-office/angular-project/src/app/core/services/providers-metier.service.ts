import {Injectable} from '@angular/core';
import {KindOfData} from 'micro_service_modules/api-kmedia';
import {OrganizationService, ProvidersService} from 'micro_service_modules/strukture/api-strukture';
import {Observable} from 'rxjs';
import {ImageLogoService} from './image-logo.service';
import {OrganizationMetierService} from './organization/organization-metier.service';

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
