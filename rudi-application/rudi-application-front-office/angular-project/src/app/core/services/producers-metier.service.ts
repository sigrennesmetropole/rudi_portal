import {Injectable} from '@angular/core';
import {KindOfData} from 'micro_service_modules/api-kmedia';
import {OrganizationService, ProducersService} from 'micro_service_modules/strukture/api-strukture';
import {Observable} from 'rxjs';
import {ImageLogoService} from './image-logo.service';
import {OrganizationMetierService} from './organization/organization-metier.service';

@Injectable({
    providedIn: 'root'
})
export class ProducersMetierService extends OrganizationMetierService {

    constructor(
        protected imageLogoService: ImageLogoService,
        private producersService: ProducersService,
        protected readonly organizationService: OrganizationService
    ) {
        super(imageLogoService, organizationService);
    }

    protected downloadProducerMediaByType(producerUuid: string, kindOfData: KindOfData): Observable<Blob> {
        return this.producersService.downloadProducerMediaByType(producerUuid, KindOfData.Logo);
    }
}
