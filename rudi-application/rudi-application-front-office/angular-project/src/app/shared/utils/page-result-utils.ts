import {concat, EMPTY, from, Observable} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';

export interface PageResult<T> {
    total?: number;
    elements?: T[];
}

export class PageResultUtils {

    static fetchAllElementsUsing<P extends PageResult<T>, T>(fetchOffset: (offset) => Observable<P>): Observable<T[]> {
        // source : https://stackoverflow.com/a/35494766/1655155
        const fetchAllElementsFromOffset: (offset) => Observable<T> = offset => fetchOffset(offset).pipe(
            concatMap(({total, elements}) => {
                const element$ = from(elements);
                const nextOffset = offset + elements.length;
                const nextElement$: Observable<T> = nextOffset < total ? fetchAllElementsFromOffset(nextOffset) : EMPTY;
                return concat(element$, nextElement$);
            })
        );

        return fetchAllElementsFromOffset(0).pipe(
            toArray()
        );
    }

}
