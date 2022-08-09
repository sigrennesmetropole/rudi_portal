import {AssetDescription, Task} from '../../api-bpmn';
import {ObjectWithDependencies} from './dependencies-utils';

/**
 * Une tâche du WorkFlow enrichie avec :
 *
 * <ul>
 *     <li><b>task</b> : la tâche originale du workflow</li>
 *     <li><b>asset</b> : un accesseur vers <code>task.asset</code> du type attendu (type générique <code>A</code>)</li>
 *     <li><b>dependencies</b> : toutes les dépendances injectées par l'opérateur RxJS {@link injectDependencies} (type générique D)</li>
 * </ul>
 */
export abstract class TaskWithDependencies<A extends AssetDescription, D> extends ObjectWithDependencies<D> {
    protected constructor(readonly task: Task, dependencies: D) {
        super(dependencies);
        this.task = task;
    }

    get asset(): A {
        return this.task.asset as A;
    }
}
