import {Injectable} from '@angular/core';
import {RequestToStudy} from '../request-to-study.interface';
import {Task} from '../../../../api-bpmn';
import {WorkerService} from '../worker.service';
import {NewDatasetRequest} from '../../../../projekt/projekt-model';
import {
    NewDatasetRequestDependencies,
    NewDatasetRequestTask,
    NewDatasetRequestTaskDepenciesService,
    NewDatasetRequestTaskDependencyFetchers
} from '../../tasks/projekt/new-dataset-request-task-depencies.service';
import {SelfdataInformationRequestDependencies} from '../../tasks/selfdata/selfdata-information-request-task-dependencies.service';
import {ProjektTaskSearchCriteria} from '../../tasks/projekt/projekt-task-search-criteria.interface';


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
