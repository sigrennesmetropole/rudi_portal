import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules/api-bpmn';
import {NewDatasetRequest} from 'micro_service_modules/projekt/projekt-model';
import {
    NewDatasetRequestDependencies,
    NewDatasetRequestTask,
    NewDatasetRequestTaskDepenciesService,
    NewDatasetRequestTaskDependencyFetchers
} from '../../tasks/projekt/new-dataset-request-task-depencies.service';
import {ProjektTaskSearchCriteria} from '../../tasks/projekt/projekt-task-search-criteria.interface';
import {SelfdataInformationRequestDependencies} from '../../tasks/selfdata/selfdata-information-request-task-dependencies.service';
import {RequestToStudy} from '../request-to-study.interface';
import {WorkerService} from '../worker.service';


@Injectable({
    providedIn: 'root'
})
export class WorkerNewDatasetRequestService
    extends WorkerService<NewDatasetRequestTask, NewDatasetRequest, NewDatasetRequestDependencies, ProjektTaskSearchCriteria> {

    constructor(
        newDatasetRequestTaskService: NewDatasetRequestTaskDepenciesService,
        newDatasetRequestTaskDependencyFetchers: NewDatasetRequestTaskDependencyFetchers,
    ) {
        super(newDatasetRequestTaskService, newDatasetRequestTaskDependencyFetchers);
    }

    mapToRequestToStudy(task: Task, assetDescription: NewDatasetRequest, dependencies: SelfdataInformationRequestDependencies)
        : RequestToStudy {
        const requestToStudy = super.mapToRequestToStudy(task, assetDescription, dependencies);
        requestToStudy.url = 'new-request-task-detail';
        return requestToStudy;
    }
}
