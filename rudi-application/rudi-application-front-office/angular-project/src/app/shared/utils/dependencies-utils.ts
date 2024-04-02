import {forkJoin, Observable, ObservedValueOf, of, pipe, throwError, UnaryFunction} from 'rxjs';
import {map, mapTo, switchMap} from 'rxjs/operators';
import {OwnerType} from 'micro_service_modules/projekt/projekt-model';

/**
 * Récupérateur de dépendance à injecter dans un champ donné d'un objet
 */
export interface DependencyFetcher<I, V> {

    /**
     * Est-ce que la dépendance a ce qu'il faut en input pour se charger ?
     * @param input les données en entrées nécessaires pour charger la dépendance
     */
    hasPrerequisites(input: I): boolean;

    /**
     * @return la clé utilisée pour injecter la dépendance, on ne fait qu'un seul appel à #getValue par clé
     */
    getKey(input: I): string;

    /**
     * @return la dépendance à injecter
     */
    getValue(key: string): Observable<V>;
}

/**
 * Définiton d'un objet ayant des dépendances à charger
 */
export abstract class ObjectWithDependencies<D> {
    protected constructor(readonly dependencies: D) {
        this.dependencies = dependencies;
    }
}

// tslint:disable-next-line:no-any
export type DependencyFetchersByFieldName<T extends ObjectWithDependencies<D>, D> = { [key: string]: DependencyFetcher<T, any> };

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans chaque object actuellement présent dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length
export function injectDependenciesEach<T extends ObjectWithDependencies<D>, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, D>): UnaryFunction<Observable<T[]>, Observable<ObservedValueOf<Observable<T[]>>>> {
    return injectDependenciesImpl(fetchersByFieldName);
}

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans l'objet e actuellement présent dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length
export function injectDependencies<T extends ObjectWithDependencies<D>, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, D>): UnaryFunction<Observable<T>, Observable<ObservedValueOf<Observable<T>>>> {
    return injectDependenciesImpl(fetchersByFieldName);
}

/**
 * Opérateur RxJS permettant d'injecter des dépendances dans la tâche actuellement présente dans le pipe.
 * Si certaines dépendances nécessitent d'autres dépendances, il faut alors séparer les appels à cet opérateur.
 */
// tslint:disable-next-line:max-line-length no-any
function injectDependenciesImpl<T extends ObjectWithDependencies<D>, D, R>(fetchersByFieldName: DependencyFetchersByFieldName<T, D>): UnaryFunction<Observable<any>, Observable<ObservedValueOf<Observable<any>>>> {
    return pipe(switchMap(tasksWithDependencies => {
        return injectDependenciesInto(fetchersByFieldName, tasksWithDependencies);
    }));
}

/**
 * Injection des dépendences de objectOrObjects à l'aide des fetchersByFieldName
 * Chaque dépendance est injectée en parallèle (forkjoin)
 * si objectOrObjects est un tableau chaque champ de dépendance est lui aussi chargé en parallèle (forkjoin)
 * @param fetchersByFieldName objet contenant les méthodes de chargement de chaque dépendance par nom de champ et clé/valeur
 * @param objectOrObjects liste des éléments ayant des dépendances, objet unique OU tableau
 */
// tslint:disable-next-line:max-line-length
function injectDependenciesInto<T extends ObjectWithDependencies<D>, D>(fetchersByFieldName: DependencyFetchersByFieldName<T, D>, objectOrObjects: T | T[]): Observable<T | T[]> {
    const objectsWithDependencies: T[] = objectOrObjects instanceof Array ? objectOrObjects : [objectOrObjects];
    if (!objectsWithDependencies.length) {
        return of([]);
    }

    // Construction d'un objet { clé: observable } pour charger les dépendances
    const dependencie$ = {};
    for (const fieldName in fetchersByFieldName) {
        const fetcher = fetchersByFieldName[fieldName];
        dependencie$[fieldName] = injectDependencyIntoObjects(fieldName, fetcher, objectsWithDependencies);
    }

    // On lance la récupération en parallèle des dépendances pour chaque clé
    // alimentation de objectsWithDependencies à la complétion du forkjoin (on se fiche du résultat de retour)
    return forkJoin(dependencie$).pipe(
        mapTo(objectOrObjects instanceof Array ? objectsWithDependencies : objectsWithDependencies[0])
    );
}

/**
 * Injection d'un champ de dépendance dans un objet ou une série d'objets avec traitement en paralléle (forkjoin)
 * @param fieldName le nom du champ/ de la depéndance à charger
 * @param fetcher objet définissant les méthodes de chargement de la dépendance
 * @param objectsWithDependencies le ou les objets où on injecte la dépendance
 */
function injectDependencyIntoObjects<T extends ObjectWithDependencies<D>, D, R>(
    fieldName: string,
    fetcher: DependencyFetcher<T, R>,
    objectsWithDependencies: T[]): Observable<T[]> {

    // Construction d'une map < [arguments observable], [observable] >
    // pour chaque objet dont on veut récupérer les dépendances
    // Ex : si array de 3 objets avec les mêmes dépendances
    // => Map a trois entrées où on récupère les deps de la même manière avec 3 arguments différents
    const value$ByKey: Map<string, Observable<R>> = new Map<string, Observable<R>>();
    for (const objectWithDependencies of objectsWithDependencies) {

        // Check si on peut charger la dépendance
        if (!fetcher.hasPrerequisites(objectWithDependencies)) {
            return throwError(new Error('Erreur la dépendance : ' + fieldName + ' ne possède pas ses prérequis.' +
                ' Changez le chaînage RXJS.'));
        }

        // Si on peut charger la dépendance on construit la map
        const key = fetcher.getKey(objectWithDependencies);
        if (!(key in value$ByKey)) {
            const value$ = fetcher.getValue(key);
            value$ByKey.set(key, value$);
        }
    }

    // Récupération en parallèle des dépendances d'un même champs pour tous les objets ayant besoin de celle-ci
    // Ex : 3 objets ayant besoin de charger leur logo, on lance la recup des 3 logos en même temps
    // alimentation de taskWithDependencies à chaque complétion du forkjoin
    return forkJoin(Object.fromEntries(value$ByKey)).pipe(
        map(valuesByKey => objectsWithDependencies.map(taskWithDependencies => {
            const key = fetcher.getKey(taskWithDependencies);
            taskWithDependencies.dependencies[fieldName] = valuesByKey[key];
            return taskWithDependencies;
        }))
    );
}

export interface HasOwnerTypeAndUuid {
    owner_type: OwnerType;
    owner_uuid: string;
}

export interface HasUuid {
    uuid?: string;
}

export interface HasOrganizationId {
    organization_id: string;
}

export interface HasId {
    id?: string;
}

export interface OtherLinkedDatasetParams {
    project_uuid: string;
    dataset_producer_uuid: string;
    task_id: string;
}

export class OwnerKey {

    static serialize(value: HasOwnerTypeAndUuid): string {
        return `${value.owner_type}:${value.owner_uuid}`;
    }

    static deserialize(serial: string): HasOwnerTypeAndUuid {
        const [ownerType, ownerUuid] = serial.split(':');
        return {
            owner_type: ownerType as OwnerType,
            owner_uuid: ownerUuid
        };
    }
}


export class OtherLinksKey {

    static serialize(value1: HasUuid, value2: HasOrganizationId, value3: HasId): string {
        return `${value1.uuid}:${value2.organization_id}:${value3.id}`;
    }

    static deserialize(serial: string): OtherLinkedDatasetParams {
        const [projectUuid, datasetProducerUuid, taskId] = serial.split(':');
        return {
            project_uuid: projectUuid,
            dataset_producer_uuid: datasetProducerUuid,
            task_id: taskId
        };
    }
}
