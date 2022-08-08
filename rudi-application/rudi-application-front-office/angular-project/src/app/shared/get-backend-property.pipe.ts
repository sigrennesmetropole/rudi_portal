import {Pipe, PipeTransform} from '@angular/core';
import {PropertiesMetierService} from '../core/services/properties-metier.service';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Pipe({
    name: 'getBackendProperty'
})
export class GetBackendPropertyPipe implements PipeTransform {

    constructor(
        private readonly propertiesService: PropertiesMetierService,
    ) {
    }

    transform(key: string): Observable<string> {
        return this.transformWithoutAsync(key);
    }

    // Actuellement on est obligé de faire un `| async` après ce pipe car il renvoie un Observable
    // Le pipe async n'a pas été inclus dans ce pipe pour les raisons décrites ici : https://stackoverflow.com/a/51495671/1655155
    transformWithoutAsync(key: string): Observable<string> {
        return this.propertiesService.get(key).pipe(
            map(backendValue => backendValue ?? key)
        );
    }

}
