import {Injectable} from '@angular/core';
import {TaskService as ProjektTaskService} from '../../../../projekt/projekt-api';
import {Observable} from 'rxjs';
import {Task} from '../../../../api-bpmn';
import {Project} from '../../../../projekt/projekt-model';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';
import {ObjectType} from '../object-type.enum';

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
