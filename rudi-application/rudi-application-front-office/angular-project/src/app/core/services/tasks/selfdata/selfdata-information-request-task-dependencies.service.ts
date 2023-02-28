import {Injectable} from '@angular/core';
import {TaskWithDependencies} from '../../../../shared/utils/task-utils';
import {Task} from '../../../../api-bpmn';
import {SelfdataInformationRequest} from '../../../../selfdata/selfdata-model';
import {TaskDependencyFetchers, TaskWithDependenciesService} from '../task-with-dependencies-service';
import {SelfdataTaskSearchCriteria} from './selfdata-task-search-criteria.interface';
import {TaskDependencies} from '../task-dependencies.interface';
import {AclService} from '../../../../acl/acl-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {SelfdataInformationRequestTaskMetierService} from './selfdata-information-request-task-metier.service';
import {DependencyFetcher} from '../../../../shared/utils/dependencies-utils';
import {Metadata} from '../../../../api-kaccess';
import {KonsultMetierService} from '../../konsult-metier.service';
import {OwnerInfo} from '../../../../projekt/projekt-model';

/**
 * les dépendances attendues pour une tâche
 */
export interface SelfdataInformationRequestDependencies extends TaskDependencies {
    dataset?: Metadata;
    ownerInfo?: OwnerInfo;
}

/**
 * Objet permettant de charger les dépendances d'une tâche
 */
export class SelfdataInformationRequestTask extends TaskWithDependencies
    <SelfdataInformationRequest, SelfdataInformationRequestDependencies> {
    constructor(task: Task) {
        super(task, {});
    }
}

@Injectable({
    providedIn: 'root'
})
export class SelfdataInformationRequestTaskDependenciesService extends TaskWithDependenciesService
    <SelfdataInformationRequestTask, SelfdataTaskSearchCriteria, SelfdataInformationRequest> {

    constructor(readonly selfdataInformationRequestTaskMetierService: SelfdataInformationRequestTaskMetierService) {
        super(selfdataInformationRequestTaskMetierService);
    }

    defaultSearchCriteria(): SelfdataTaskSearchCriteria {
        return {};
    }

    newTaskWithDependencies(task: Task): SelfdataInformationRequestTask {
        return new SelfdataInformationRequestTask(task);
    }
}

@Injectable({
    providedIn: 'root'
})
export class SelfdataInformationRequestTaskDependencyFetchers
    extends TaskDependencyFetchers<SelfdataInformationRequestTask, SelfdataInformationRequest, SelfdataInformationRequestDependencies> {

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        organizationService: OrganizationService,
        aclService: AclService,
    ) {
        super(organizationService, aclService);
    }

    get dataset(): DependencyFetcher<SelfdataInformationRequestTask, Metadata> {
        return {
            hasPrerequisites: (input: SelfdataInformationRequestTask) => input != null && input.asset != null && input.asset.dataset_uuid != null,
            getKey: taskWithDependencies => taskWithDependencies.asset.dataset_uuid,
            getValue: datasetUuid => this.konsultMetierService.getMetadataByUuid(datasetUuid)
        };
    }
}
