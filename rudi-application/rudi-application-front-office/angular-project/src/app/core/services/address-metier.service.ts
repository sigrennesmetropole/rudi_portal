import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {SelfdataService} from '../../selfdata/selfdata-api/api/selfdata.service';
import {Address} from '../../selfdata/selfdata-api/model/address';

@Injectable({
    providedIn: 'root'
})
export class AddressMetierService {
    private static MIN_QUERY_LENGTH = 3;

    public static isValidQuery(query: string): boolean {
        return query.length > AddressMetierService.MIN_QUERY_LENGTH;
    }

    constructor(
        private readonly selfdataService: SelfdataService
    ) {
    }

    getFullAddresses(query: string): Observable<Address[]> {
        return this.selfdataService.getFullAddresses(query);
    }

    getAddressById(query: number): Observable<Address> {
        return this.selfdataService.getAddressById(query);
    }
}
