import {forkJoin, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

export class ObservableUtils {
    static filter<T>(items: T[]): ObservableArrayFilter<T> {
        return new ObservableArrayFilter(items);
    }
}

export class ObservableArrayFilter<T> {
    constructor(private readonly items: T[]) {
    }

    using(observablePredicate: (item: T) => Observable<boolean>): Observable<T[]> {
        const filteredItemsObservables = this.items.map(item => {
            const predicate$ = observablePredicate.call(this, item) as Observable<boolean>;
            return predicate$.pipe(
                map(predicate => predicate ? item : null)
            );
        });
        return forkJoin(filteredItemsObservables).pipe(
            map(filteredItems => filteredItems.filter(filteredItem => filteredItem)) // remove null items
        );
    }

}
