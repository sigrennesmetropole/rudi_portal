import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as SelfdataTaskService} from 'micro_service_modules/selfdata/selfdata-api';
import {SelfdataInformationRequest} from 'micro_service_modules/selfdata/selfdata-model';
import {Observable} from 'rxjs';
import {ObjectType} from '../object-type.enum';
import {MicroserviceSelfdataTaskMetierService} from './microservice-selfdata-task-metier.service';

@Injectable({
    providedIn: 'root'
})
export class SelfdataInformationRequestTaskMetierService extends MicroserviceSelfdataTaskMetierService<SelfdataInformationRequest> {

    protected objectType: ObjectType = ObjectType.SELFDATA_INFORMATION_REQUEST;

    constructor(
        selfdataTaskService: SelfdataTaskService
    ) {
        super(selfdataTaskService);
    }

    claimTask(taskId: string): Observable<Task> {
        return this.selfdataTaskService.claimSelfdataInformationRequestTask(taskId);
    }

    doIt(taskId: string, actionName: string): Observable<Task> {
        return this.selfdataTaskService.doItSelfdataInformationRequest(taskId, actionName);
    }

    unclaimTask(taskId: string): Observable<Task> {
        return this.selfdataTaskService.unclaimSelfdataInformationRequestTask(taskId);
    }

    updateTask(task: Task): Observable<Task> {
        return this.selfdataTaskService.updateSelfdataInformationRequestTask(task);
    }

    createDraft(asset: SelfdataInformationRequest): Observable<Task> {
        return this.selfdataTaskService.createSelfdataInformationRequestDraft(asset);
    }

    deleteAsset(asset: SelfdataInformationRequest): Observable<void> {
        return this.selfdataTaskService.deleteSelfdataInformationRequest(asset.uuid);
    }

    startTask(task: Task): Observable<Task> {
        return this.selfdataTaskService.startSelfdataInformationRequestTask(task);
    }
}
