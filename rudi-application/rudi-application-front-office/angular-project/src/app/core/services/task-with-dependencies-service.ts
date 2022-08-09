import {Action, Task} from '../../api-bpmn';
import {Observable, throwError} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {mapEach} from '../../shared/utils/ObservableUtils';
import {TaskService} from '../../projekt/projekt-api';
import {TaskMetierService, TaskSearchCriteria} from './task-metier.service';

export abstract class TaskWithDependenciesService<T, C extends TaskSearchCriteria> {

    protected constructor(
        protected readonly taskService: TaskService,
        protected readonly taskMetierService: TaskMetierService,
    ) {
    }

    getTask(taskId: string): Observable<T> {
        return this.taskService.getTask(taskId).pipe(
            map(task => this.newTaskWithDependencies(task))
        );
    }

    searchTasks(searchCriteria: C = this.defaultSearchCriteria()): Observable<T[]> {
        return this.taskMetierService.searchTasks('LinkedDataset', searchCriteria).pipe(
            mapEach(task => this.newTaskWithDependencies(task))
        );
    }

    abstract newTaskWithDependencies(task: Task): T;

    abstract defaultSearchCriteria(): C;

    doAction(action: Action, task: Task): Observable<Task> {
        return this.claimTask(task.id).pipe(
            catchError(err => this.unclaimTask(task.id).pipe(switchMap(() => throwError(err)))),
            switchMap(() => this.updateTask(task)),
            switchMap(() => this.doIt(task.id, action.name)),
            switchMap(() => this.unclaimTask(task.id)),
        );
    }

    abstract claimTask(taskId: string): Observable<Task>;

    abstract unclaimTask(taskId: string): Observable<Task>;

    abstract updateTask(task: Task): Observable<Task>;

    abstract doIt(taskId: string, actionName: string): Observable<Task>;
}
