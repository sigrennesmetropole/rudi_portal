import {AssetDescription, Task} from '../../api-bpmn';
import {forkJoin, Observable, ObservedValueOf, of, pipe, UnaryFunction} from 'rxjs';
import {map, mapTo, switchMap} from 'rxjs/operators';

/**
 * Récupérateur de dépendance à injecter dans un champ donné d'une tâche
 */
export interface DependencyFetcher<I, V> {
    /**
     * @return la clé utilisée pour injecter la dépendance, on ne fait qu'un seul appel à #getValue par clé
     */
    getKey(input: I): string;

    /**
     * @return la dépendance à injecter
     */
    getValue(key: string): Observable<V>;
}

// tslint:disable-next-line:no-any
export type DependencyFetchersByFieldName<T extends TaskWithDependencies<A, D>, A, D> = { [key: string]: DependencyFetcher<T, any> };

/**
 * Une tâche du WorkFlow enrichie avec :
 *
 * <ul>
 *     <li><b>task</b> : la tâche originale du workflow</li>
 *     <li><b>asset</b> : un accesseur vers <code>task.asset</code> du type attendu (type générique <code>A</code>)</li>
 *     <li><b>dependencies</b> : toutes les dépendances injectées par l'opérateur RxJS {@link injectDependencies} (type générique D)</li>
 * </ul>
 */
// tslint:disable-next-line:no-any
export abstract class TaskWithDependencies<A extends AssetDescription, D> {
    protected constructor(readonly task: Task, readonly dependencies: D) {
        this.task = task;
        this.dependencies = dependencies;
    }

    get asset(): A {
        return this.task.asset as A;
    }
}

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans chaque tâche actuellement présente dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length
export function injectDependenciesEach<T extends TaskWithDependencies<A, D>, A extends AssetDescription, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, A, D>): UnaryFunction<Observable<T[]>, Observable<ObservedValueOf<Observable<T[]>>>> {
    return injectDependenciesImpl(fetchersByFieldName);
}

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans la tâche actuellement présente dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length
export function injectDependencies<T extends TaskWithDependencies<A, D>, A extends AssetDescription, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, A, D>): UnaryFunction<Observable<T>, Observable<ObservedValueOf<Observable<T>>>> {
    return injectDependenciesImpl(fetchersByFieldName);
}

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans la tâche actuellement présente dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length no-any
function injectDependenciesImpl<T extends TaskWithDependencies<A, D>, A extends AssetDescription, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, A, D>): UnaryFunction<Observable<any>, Observable<ObservedValueOf<Observable<any>>>> {
    return pipe(switchMap(tasksWithDependencies => {
        return injectDependenciesInto(fetchersByFieldName, tasksWithDependencies);
    }));
}

/**
 * Implémentation
 */
// tslint:disable-next-line:max-line-length
function injectDependenciesInto<T extends TaskWithDependencies<A, D>, A extends AssetDescription, D>(fetchersByFieldName: DependencyFetchersByFieldName<T, A, D>, taskOrTasks: T | T[]): Observable<T | T[]> {
    const tasksWithDependencies: T[] = taskOrTasks instanceof Array ? taskOrTasks : [taskOrTasks];
    if (!tasksWithDependencies.length) {
        return of([]);
    }
    const dependencie$ = {};
    for (const fieldName in fetchersByFieldName) {
        const fetcher = fetchersByFieldName[fieldName];
        dependencie$[fieldName] = injectDependencyIntoTasks(fieldName, fetcher, tasksWithDependencies);
    }
    return forkJoin(dependencie$).pipe(
        mapTo(taskOrTasks instanceof Array ? tasksWithDependencies : tasksWithDependencies[0])
    );
}

function injectDependencyIntoTasks<T extends TaskWithDependencies<A, D>, A extends AssetDescription, D, R>(
    fieldName: string,
    fetcher: DependencyFetcher<T, R>,
    tasksWithDependencies: T[]): Observable<T[]> {

    const value$ByKey: Map<string, Observable<R>> = new Map<string, Observable<R>>();
    for (const taskWithDependencies of tasksWithDependencies) {
        const key = fetcher.getKey(taskWithDependencies);
        if (!(key in value$ByKey)) {
            const value$ = fetcher.getValue(key);
            value$ByKey.set(key, value$);
        }
    }

    return forkJoin(Object.fromEntries(value$ByKey)).pipe(
        map(valuesByKey => tasksWithDependencies.map(taskWithDependencies => {
            const key = fetcher.getKey(taskWithDependencies);
            taskWithDependencies.dependencies[fieldName] = valuesByKey[key];
            return taskWithDependencies;
        }))
    );
}
