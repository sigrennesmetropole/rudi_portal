import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {Address} from '../../../selfdata/selfdata-api/model/address';

@Injectable({
    providedIn: 'root'
})
export abstract class RvaService {
    private static MIN_QUERY_LENGTH = 3;

    public static isValidQuery(query: string): boolean {
        return query.length > RvaService.MIN_QUERY_LENGTH;
    }

    constructor() {
    }

    abstract getFullAddresses(query: string): Observable<Address[]>;


    abstract getAddressById(query: number): Observable<Address>;

}
