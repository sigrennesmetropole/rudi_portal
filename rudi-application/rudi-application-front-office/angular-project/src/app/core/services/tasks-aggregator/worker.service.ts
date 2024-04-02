import {injectDependenciesEach} from '@shared/utils/dependencies-utils';
import {mapEach} from '@shared/utils/ObservableUtils';
import {TaskWithDependencies} from '@shared/utils/task-utils';
import {Task} from 'micro_service_modules//api-bpmn/model/task';
import {AssetDescription} from 'micro_service_modules/api-bpmn';
import {Observable} from 'rxjs';
import {TaskDependencies} from '../tasks/task-dependencies.interface';
import {TaskSearchCriteria} from '../tasks/task-search-criteria.interface';
import {TaskDependencyFetchers, TaskWithDependenciesService} from '../tasks/task-with-dependencies-service';
import {RequestToStudy} from './request-to-study.interface';
import {Worker} from './worker.interface';

/**
 * Service capble de récupérer des données et de les renvoyer au format : Tâche de travail RUDI
 */
export abstract class WorkerService<T extends TaskWithDependencies<A, D>,
    A extends AssetDescription, D extends TaskDependencies, C extends TaskSearchCriteria> implements Worker {

    protected constructor(
        protected readonly taskWithDependenciesService: TaskWithDependenciesService<T, C, A>,
        protected readonly dependencyFetchers: TaskDependencyFetchers<T, A, D>
    ) {}

    loadTasks(): Observable<RequestToStudy[]> {
        return this.mapTaskWithDependenciesToRequestToStudy(this.searchTasksAndInjectGeneric());
    }

    mapToRequestToStudy(task: Task, assetDescription: A, dependencies: D): RequestToStudy {
        return {
            taskId: task.id,
            receivedDate: new Date(task.updatedDate),
            description: assetDescription.description,
            initiator: dependencies.initiatorInfo,
            status: task.functionalStatus,
            processDefinitionKey: assetDescription.process_definition_key
        } as RequestToStudy;
    }

    searchTasks(): Observable<T[]> {
        return this.taskWithDependenciesService.searchTasksWithDependencies();
    }

    private searchTasksAndInjectGeneric(): Observable<T[]> {
        return this.searchTasks().pipe(
            injectDependenciesEach({
                initiatorInfo: this.dependencyFetchers.initiatorInfo
            })
        );
    }

    private mapTaskWithDependenciesToRequestToStudy(tasksWithDependencies: Observable<T[]>)
        : Observable<RequestToStudy[]> {
        return tasksWithDependencies.pipe(
            mapEach((taskWithDependencies: T) => {
                return this.mapToRequestToStudy(taskWithDependencies.task, taskWithDependencies.asset, taskWithDependencies.dependencies);
            })
        );
    }
}
