import {Injectable} from '@angular/core';

import {Task} from 'micro_service_modules//api-bpmn';
import {SelfdataInformationRequest} from 'micro_service_modules/selfdata/selfdata-model';
import {
    SelfdataInformationRequestDependencies,
    SelfdataInformationRequestTask,
    SelfdataInformationRequestTaskDependenciesService,
    SelfdataInformationRequestTaskDependencyFetchers
} from '../../tasks/selfdata/selfdata-information-request-task-dependencies.service';
import {SelfdataTaskSearchCriteria} from '../../tasks/selfdata/selfdata-task-search-criteria.interface';
import {RequestToStudy} from '../request-to-study.interface';
import {WorkerService} from '../worker.service';

@Injectable({
    providedIn: 'root'
})
export class WorkerSelfdataInformationRequestService
    extends WorkerService<SelfdataInformationRequestTask, SelfdataInformationRequest,
        SelfdataInformationRequestDependencies, SelfdataTaskSearchCriteria> {

    constructor(
        selfdataInformationRequestTaskService: SelfdataInformationRequestTaskDependenciesService,
        selfdataInformationRequestTaskDependencyFetchers: SelfdataInformationRequestTaskDependencyFetchers) {
        super(selfdataInformationRequestTaskService, selfdataInformationRequestTaskDependencyFetchers);
    }

    mapToRequestToStudy(task: Task, assetDescription: SelfdataInformationRequest, dependencies: SelfdataInformationRequestDependencies)
        : RequestToStudy {
        const requestToStudy = super.mapToRequestToStudy(task, assetDescription, dependencies);
        requestToStudy.url = 'selfdata-information-request-task-detail';
        return requestToStudy;
    }
}
