import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {FrontOfficeProperties, KonsultService} from '../../api-konsult';

function get(key: string, object: object): string | undefined {
    const childKey = key.split('.')[0];
    const child = object[childKey];
    if (child) {
        if (key.length > childKey.length) {
            const childChildKey = key.substr(childKey.length + 1);
            return get(childChildKey, child);
        } else {
            return child;
        }
    } else {
        return undefined;
    }
}

@Injectable({
    providedIn: 'root'
})
export class PropertiesMetierService {
    private propertie$: Observable<FrontOfficeProperties>;

    constructor(
        private readonly konsultService: KonsultService,
    ) {
    }

    get(key: string): Observable<string> {
        return this.getProperties().pipe(
            map(properties => get(key, properties))
        );
    }

    private getProperties(): Observable<FrontOfficeProperties> {
        if (!this.propertie$) {
            this.propertie$ = this.konsultService.getFrontOfficeProperties().pipe(
                shareReplay(1)
            );
        }
        return this.propertie$;
    }
}
