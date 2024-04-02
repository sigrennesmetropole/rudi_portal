import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as ProjektTaskService} from 'micro_service_modules/projekt/projekt-api';
import {Project} from 'micro_service_modules/projekt/projekt-model';
import {Observable} from 'rxjs';
import {ObjectType} from '../object-type.enum';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';

@Injectable({
    providedIn: 'root'
})
export class ProjectTaskMetierService extends MicroserviceProjektTaskMetierService<Project> {

    protected objectType: ObjectType = ObjectType.PROJECT;

    constructor(projektTaskService: ProjektTaskService) {
        super(projektTaskService);
    }

    claimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.claimProjectTask(taskId);
    }

    doIt(taskId: string, actionName: string): Observable<Task> {
        return this.projektTaskService.doItProject(taskId, actionName);
    }

    unclaimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.unclaimProjectTask(taskId);
    }

    updateTask(task: Task): Observable<Task> {
        return this.projektTaskService.updateProjectTask(task);
    }

    createDraft(asset: Project): Observable<Task> {
        return this.projektTaskService.createProjectDraft(asset);
    }

    startTask(task: Task): Observable<Task> {
        return this.projektTaskService.startProjectTask(task);
    }
}
