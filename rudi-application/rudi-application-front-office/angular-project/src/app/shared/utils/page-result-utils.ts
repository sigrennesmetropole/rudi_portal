import {concat, EMPTY, from, Observable, ObservedValueOf, of, pipe, throwError, UnaryFunction} from 'rxjs';
import {concatMap, switchMap, toArray} from 'rxjs/operators';

export interface PageResult<T> {
    total?: number;
    elements?: T[];
    items?: T[];
}

export class PageResultUtils {

    static fetchAllElementsUsing<P extends PageResult<T>, T>(fetchOffset: (offset: number) => Observable<P>): Observable<T[]> {
        // source : https://stackoverflow.com/a/35494766/1655155
        const fetchAllElementsFromOffset: (offset) => Observable<T> = offset => fetchOffset(offset).pipe(
            concatMap(pageResult => {
                const total = pageResult.total;
                const elementsOrItems = pageResult.elements || pageResult.items || [];
                const elementsOrItem$ = from(elementsOrItems);
                const nextOffset = offset + elementsOrItems.length;
                const nextElementsOrItem$: Observable<T> = nextOffset < total ? fetchAllElementsFromOffset(nextOffset) : EMPTY;
                return concat(elementsOrItem$, nextElementsOrItem$);
            })
        );

        return fetchAllElementsFromOffset(0).pipe(
            toArray()
        );
    }

    /**
     * Pipe RxJS qui récupère le premier élément d'une liste paginée ou lance une erreur s'il n'y a aucun élément
     */
    static firstElementOrThrow<P extends PageResult<T>, T>(e: Error): UnaryFunction<Observable<P>, Observable<ObservedValueOf<Observable<T>>>> {
        return pipe(switchMap(elements => {
            if (elements?.total) {
                return of(elements.elements[0]);
            } else {
                return throwError(e);
            }
        }));
    }

}
