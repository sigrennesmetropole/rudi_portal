import {Observable, ObservedValueOf, pipe, UnaryFunction} from 'rxjs';
import {map} from 'rxjs/operators';

type FilterFunction<T> = (element: T) => boolean;

/**
 * Opérateur RxJS permettant de filtrer chaque élément d'un tableau.
 */
// tslint:disable-next-line:max-line-length no-any
export function filterEach<T>(elementFilter: FilterFunction<T>): UnaryFunction<Observable<T[]>, Observable<ObservedValueOf<Observable<T[]>>>> {
    return pipe(
        map(elements => elements.filter(elementFilter))
    );
}
