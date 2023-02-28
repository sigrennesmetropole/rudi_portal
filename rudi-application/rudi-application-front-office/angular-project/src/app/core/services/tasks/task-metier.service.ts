import {Action, AssetDescription, Task} from '../../../api-bpmn';
import {Observable,throwError} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {ObjectType} from './object-type.enum';
import {TaskSearchCriteria} from './task-search-criteria.interface';

/**
 * Service générique de gestion de tâches RUDI
 */
export abstract class TaskMetierService<T extends AssetDescription> {

    protected abstract objectType: ObjectType;

    /**
     * Réalisation d'une action sur une tâche RUDI pour un user
     * Réalise en séquence le verrouillage le perform et le délock de la tâche
     * Si une step échoue on unclaim la task pour ne pas la laisser vérrouillée en cas d'erreur technique
     * @param action l'action de workflow à réaliser
     * @param task la tâche RUDI
     */
    doAction(action: Action, task: Task): Observable<Task> {
        return this.claimTask(task.id).pipe(
            catchError(err => this.unclaimTask(task.id).pipe(switchMap(() => throwError(err)))),
            switchMap(() => this.updateTask(task)),
            catchError(err => this.unclaimTask(task.id).pipe(switchMap(() => throwError(err)))),
            switchMap(() => this.doIt(task.id, action.name)),
            catchError(err => this.unclaimTask(task.id).pipe(switchMap(() => throwError(err)))),
            switchMap(() => this.unclaimTask(task.id)),
        );
    }

    /**
     * Recherche de tâches RUDI du workflow
     * @param searchCriteria le critère de filtrage sur les tâches
     */
    searchTasks(searchCriteria: TaskSearchCriteria): Observable<Task[]> {
        return this.searchMicroserviceTasks(searchCriteria).pipe(
            map(tasks => tasks.filter(task => task.asset.object_type === this.objectType))
        );
    }

    /**
     * Création de la Task Draft à partir de l'asset
     * @param asset l'obje tasset de la task
     */
    abstract createDraft(asset: T): Observable<Task>;

    /**
     * Recherche des tâches dans un microservice donné
     * @param searchCriteria le critère de filtrage sur les tâches
     */
    abstract searchMicroserviceTasks(searchCriteria: TaskSearchCriteria): Observable<Task[]>;

    /**
     * Démarre le workflow d'une tâche fournie
     * @param task la tâche à démarrer
     */
    abstract startTask(task: Task): Observable<Task>;

    /**
     * Récupération d'une tâche par son ID pour le détail
     * @param taskId l'id de la tâche
     */
    abstract getTask(taskId): Observable<Task>;

    /**
     * Appropriation et verrouillage de la tâche via l'id fourni
     * @param taskId id de la tâche à approprier
     */
    abstract claimTask(taskId: string): Observable<Task>;

    /**
     * Déverrouillage de la tâche via l'id fourni
     * @param taskId id de la tâche à déverrouiller
     */
    abstract unclaimTask(taskId: string): Observable<Task>;

    /**
     * Mise à jour des informations de la tâche RUDI
     * @param task la tâche à modifier
     */
    abstract updateTask(task: Task): Observable<Task>;

    /**
     * Réalisation d'une action de workflow sur la tâche
     * @param taskId id de la tâche ciblée
     * @param actionName nom de l'action de workflow
     */
    abstract doIt(taskId: string, actionName: string): Observable<Task>;
}
