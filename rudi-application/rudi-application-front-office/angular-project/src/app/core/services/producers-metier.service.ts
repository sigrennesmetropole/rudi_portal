import {Injectable} from '@angular/core';
import {ProducersService} from '../../providers/api-providers';
import {KindOfData} from '../../api-kmedia';
import {OrganizationMetierService} from './organization-metier.service';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProducersMetierService extends OrganizationMetierService {

    constructor(private producersService: ProducersService) {
        super();
    }

    protected downloadProducerMediaByType(producerUuid: string, kindOfData: KindOfData): Observable<Blob> {
        return this.producersService.downloadProducerMediaByType(producerUuid, KindOfData.Logo);
    }
}
