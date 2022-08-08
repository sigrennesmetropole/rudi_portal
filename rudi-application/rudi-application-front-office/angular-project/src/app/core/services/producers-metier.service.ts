import {Injectable} from '@angular/core';
import {OrganizationService, ProducersService} from '../../strukture/api-strukture';
import {KindOfData} from '../../api-kmedia';
import {OrganizationMetierService} from './organization-metier.service';
import {Observable} from 'rxjs';
import {ImageLogoService} from './image-logo.service';

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
