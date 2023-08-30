import {Injectable} from '@angular/core';
import {RvaService} from '../rva.service';
import {Observable} from 'rxjs';
import {Address} from '../../../../api-rva';
import {SelfdataService} from '../../../../selfdata/selfdata-api';

@Injectable({
    providedIn: 'root'
})
export class SelfdataRvaService extends RvaService {

    constructor(
        private readonly selfdataService: SelfdataService
    ) {
        super();
    }

    getAddressById(query: number): Observable<Address> {
        return this.selfdataService.getAddressById(query);
    }

    getFullAddresses(query: string): Observable<Address[]> {
        return this.selfdataService.getFullAddresses(query);
    }
}
