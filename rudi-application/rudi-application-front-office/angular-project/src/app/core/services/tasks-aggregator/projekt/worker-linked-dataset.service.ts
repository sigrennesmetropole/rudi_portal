import {Injectable} from '@angular/core';
import {Task} from 'micro_service_modules//api-bpmn/model/task';
import {LinkedDataset} from 'micro_service_modules/projekt/projekt-model';
import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependenciesService,
    LinkedDatasetTaskDependencyFetchers
} from '../../tasks/projekt/linked-dataset-task-dependencies.service';
import {ProjektTaskSearchCriteria} from '../../tasks/projekt/projekt-task-search-criteria.interface';
import {RequestToStudy} from '../request-to-study.interface';

import {WorkerService} from '../worker.service';

@Injectable({
    providedIn: 'root'
})
export class WorkerLinkedDatasetService
    extends WorkerService<LinkedDatasetTask, LinkedDataset, LinkedDatasetDependencies, ProjektTaskSearchCriteria> {

    constructor(linkedDatasetTaskDependenciesService: LinkedDatasetTaskDependenciesService,
                linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers) {
        super(linkedDatasetTaskDependenciesService, linkedDatasetDependencyFetchers);
    }

    mapToRequestToStudy(task: Task, assetDescription: LinkedDataset, dependencies: LinkedDatasetDependencies): RequestToStudy {
        const requestToStudy = super.mapToRequestToStudy(task, assetDescription, dependencies);
        requestToStudy.url = 'request-task-detail';
        return requestToStudy;
    }
}
