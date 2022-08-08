import {Injectable} from '@angular/core';
import {ProjectStatus, Status, Task, TaskService} from '../../projekt/projekt-api';
import {Observable} from 'rxjs';
import {LinkedDataset, Project} from '../../projekt/projekt-model';
import {map} from 'rxjs/operators';

export interface TaskSearchCriteria {
    title?: string;
    description?: string;
    processDefinitionKeys?: string[];
    status?: Status[];
    fonctionalStatus?: string[];
    projectStatus?: ProjectStatus;
    asAdmin?: boolean;
    datasetProducerUuid?: string;
    projectUuid?: string;
}

type ObjectType = 'LinkedDataset';

@Injectable({
    providedIn: 'root'
})
export class TaskMetierService {

    constructor(
        private readonly taskService: TaskService
    ) {
    }

    /**
     * Créé la Task d'un projet à l'état DRAFT si elle existe déjà ne la créé pas, à appeler avant de démarrer le workflow
     * @param project le projet lié à la tâche
     */
    public createProjectDraft(project: Project): Observable<Task> {
        return this.taskService.createProjectDraft(project);
    }

    /**
     * Crée la task d'une demande d'accès à un JDD
     * @param link la demande d'accès dont on doit créer la tâche
     */
    public createLinkedDatasetDraft(link: LinkedDataset): Observable<Task> {
        return this.taskService.createLinkedDatasetDraft(link);
    }

    /**
     * Démarre le workflow d'une tâche fournie pour un projet
     * @param task la tâche à démarrer
     */
    public startProjectTask(task: Task): Observable<Task> {
        return this.taskService.startProjectTask(task);
    }

    /**
     * Démarre le workflow d'une tâche fournie pour une demande d'accès à un JDD
     * @param task la tâche à démarrer
     */
    public startLinkedDatasetTask(task: Task): Observable<Task> {
        return this.taskService.startLinkedDatasetTask(task);
    }

    searchTasks(objectType: ObjectType, searchCriteria: TaskSearchCriteria): Observable<Task[]> {
        return this.taskService.searchTasks(
            searchCriteria.title,
            searchCriteria.description,
            searchCriteria.processDefinitionKeys,
            searchCriteria.status,
            searchCriteria.fonctionalStatus,
            searchCriteria.projectStatus,
            searchCriteria.asAdmin,
            searchCriteria.datasetProducerUuid,
            searchCriteria.projectUuid
        ).pipe(
            map(tasks => tasks.filter(task => task.asset.object_type))
        );
    }
}
