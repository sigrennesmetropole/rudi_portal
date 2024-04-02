import {from, Observable} from 'rxjs';
import {mergeMap, reduce} from 'rxjs/operators';
import {RequestToStudy} from './request-to-study.interface';
import {Worker} from './worker.interface';

/**
 * class abstraite d'agregrateur (qui aggrège les workers)
 */
export abstract class AbstractAggregator {

    /**
     * Liste des workers permettant de récupérer des tâches
     * @protected
     */
    protected workers: Worker[];

    /**
     * Permet de charger et récupérer des "Tâches RUDI" à l'aide des workers
     */
    loadTasks(): Observable<RequestToStudy[]> {
        return from(this.workers).pipe(
            mergeMap((worker: Worker) => {
                return worker.loadTasks();
            }),
            reduce((accumulator: RequestToStudy[], currents: RequestToStudy[]) => {
                accumulator = accumulator.concat(currents);
                return accumulator;
            }, [])
        );
    }
}
