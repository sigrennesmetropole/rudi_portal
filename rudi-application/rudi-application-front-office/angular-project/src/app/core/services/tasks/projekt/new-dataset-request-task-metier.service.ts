import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as ProjektTaskService} from 'micro_service_modules/projekt/projekt-api';
import {NewDatasetRequest} from 'micro_service_modules/projekt/projekt-model';
import {Observable} from 'rxjs';
import {ObjectType} from '../object-type.enum';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';

@Injectable({
    providedIn: 'root'
})
export class NewDatasetRequestTaskMetierService extends MicroserviceProjektTaskMetierService<NewDatasetRequest> {

    protected objectType: ObjectType = ObjectType.NEW_DATASET_REQUEST;

    constructor(projektTaskService: ProjektTaskService) {
        super(projektTaskService);
    }

    claimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.claimNewDatasetRequestTask(taskId);
    }

    doIt(taskId: string, actionName: string): Observable<Task> {
        return this.projektTaskService.doItNewDatasetRequest(taskId, actionName);
    }

    unclaimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.unclaimNewDatasetRequestTask(taskId);
    }

    updateTask(task: Task): Observable<Task> {
        return this.projektTaskService.updateNewDatasetRequestTask(task);
    }

    createDraft(asset: NewDatasetRequest): Observable<Task> {
        return this.projektTaskService.createNewDatasetRequestDraft(asset);
    }

    startTask(task: Task): Observable<Task> {
        return this.projektTaskService.startNewDatasetRequestTask(task);
    }
}
