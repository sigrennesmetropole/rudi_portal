import {Injectable} from '@angular/core';
import {TaskService as SelfdataTaskService} from '../../selfdata/selfdata-api';
import {Observable} from 'rxjs';
import {Form} from '../../api-bpmn';

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
