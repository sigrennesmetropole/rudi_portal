import {Injectable} from '@angular/core';
import {RvaService} from '../rva.service';
import {Observable} from 'rxjs';
import {Address} from '../../../../api-rva';
import {KonsultService} from '../../../../konsult/konsult-api';

@Injectable({
    providedIn: 'root'
})
export class KonsultRvaService extends RvaService {

    constructor(
        private readonly konsultService: KonsultService
    ) {
        super();
    }

    getAddressById(query: number): Observable<Address> {
        throw new Error('Method not existing');
    }

    getFullAddresses(query: string): Observable<Address[]> {
        return this.konsultService.searchAddresses(query);
    }
}
