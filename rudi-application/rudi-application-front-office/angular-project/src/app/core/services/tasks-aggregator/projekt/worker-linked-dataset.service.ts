import {Injectable} from '@angular/core';
import {RequestToStudy} from '../request-to-study.interface';

import {WorkerService} from '../worker.service';
import {Task} from 'src/app/api-bpmn/model/task';
import {LinkedDataset} from '../../../../projekt/projekt-model';
import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependenciesService, LinkedDatasetTaskDependencyFetchers
} from '../../tasks/projekt/linked-dataset-task-dependencies.service';
import {ProjektTaskSearchCriteria} from '../../tasks/projekt/projekt-task-search-criteria.interface';

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
        requestToStudy.url = 'request-detail';
        return requestToStudy;
    }
}
