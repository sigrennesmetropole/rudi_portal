import {Injectable} from '@angular/core';
import {TaskService as ProjektTaskService} from '../../../../projekt/projekt-api';
import {Observable} from 'rxjs';
import {Task} from '../../../../api-bpmn';
import {NewDatasetRequest} from '../../../../projekt/projekt-model';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';
import {ObjectType} from '../object-type.enum';

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
