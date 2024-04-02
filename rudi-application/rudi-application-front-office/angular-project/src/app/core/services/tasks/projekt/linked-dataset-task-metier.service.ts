import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as ProjektTaskService} from 'micro_service_modules/projekt/projekt-api';
import {LinkedDataset} from 'micro_service_modules/projekt/projekt-model';
import {Observable} from 'rxjs';
import {ObjectType} from '../object-type.enum';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';

@Injectable({
    providedIn: 'root'
})
export class LinkedDatasetTaskMetierService extends MicroserviceProjektTaskMetierService<LinkedDataset> {

    protected objectType: ObjectType = ObjectType.LINKED_DATASET;

    constructor(projektTaskService: ProjektTaskService) {
        super(projektTaskService);
    }

    claimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.claimLinkedDatasetTask(taskId);
    }

    doIt(taskId: string, actionName: string): Observable<Task> {
        return this.projektTaskService.doItLinkedDataset(taskId, actionName);
    }

    unclaimTask(taskId: string): Observable<Task> {
        return this.projektTaskService.unclaimLinkedDatasetTask(taskId);
    }

    updateTask(task: Task): Observable<Task> {
        return this.projektTaskService.updateLinkedDatasetTask(task);
    }

    createDraft(asset: LinkedDataset): Observable<Task> {
        return this.projektTaskService.createLinkedDatasetDraft(asset);
    }

    startTask(task: Task): Observable<Task> {
        return this.projektTaskService.startLinkedDatasetTask(task);
    }
}
