import {Inject, Injectable} from '@angular/core';
import {WORKERS_AGGREGATOR_TASKS} from '../../core.module';
import {AbstractAggregator} from './abstract-aggregator';
import {Worker} from './worker.interface';

@Injectable({
    providedIn: 'root'
})
export class TasksAggregatorService extends AbstractAggregator {

    /**
     * On injecte les workers à l'aide de la clé d'injection
     * @param recuperes workers récuperés
     */
    constructor(@Inject(WORKERS_AGGREGATOR_TASKS) recuperes: Worker[]) {
        super();
        this.workers = recuperes;
    }
}
