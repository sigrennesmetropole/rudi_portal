import {Injectable} from '@angular/core';
import {TaskService as ProjektTaskService} from '../../../../projekt/projekt-api';
import {Observable} from 'rxjs';
import {Task} from '../../../../api-bpmn';
import {LinkedDataset} from '../../../../projekt/projekt-model';
import {MicroserviceProjektTaskMetierService} from './microservice-projekt-task-metier.service';
import {ObjectType} from '../object-type.enum';

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
