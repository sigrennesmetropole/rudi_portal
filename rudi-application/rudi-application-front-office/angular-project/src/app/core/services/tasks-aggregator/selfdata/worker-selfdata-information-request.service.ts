import {Injectable} from '@angular/core';
import {RequestToStudy} from '../request-to-study.interface';

import {Task} from 'src/app/api-bpmn';
import {WorkerService} from '../worker.service';
import {SelfdataInformationRequest} from '../../../../selfdata/selfdata-model';
import {
    SelfdataInformationRequestDependencies,
    SelfdataInformationRequestTask, SelfdataInformationRequestTaskDependenciesService, SelfdataInformationRequestTaskDependencyFetchers
} from '../../tasks/selfdata/selfdata-information-request-task-dependencies.service';
import {SelfdataTaskSearchCriteria} from '../../tasks/selfdata/selfdata-task-search-criteria.interface';

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
        requestToStudy.url = 'selfdata-information-request-detail';
        return requestToStudy;
    }
}
