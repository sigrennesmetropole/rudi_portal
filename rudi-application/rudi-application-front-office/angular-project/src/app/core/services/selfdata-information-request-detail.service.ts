import {Injectable} from '@angular/core';
import {Form} from 'micro_service_modules/api-bpmn';
import {TaskService as SelfdataTaskService} from 'micro_service_modules/selfdata/selfdata-api';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class SelfdataInformationRequestDetailService {

    constructor(
        private readonly taskService: SelfdataTaskService
    ) {
    }

    public lookupFilledMatchingDataForm(taskId: string): Observable<Form> {
        return this.taskService.lookupFilledMatchingDataForm(taskId);
    }
}
