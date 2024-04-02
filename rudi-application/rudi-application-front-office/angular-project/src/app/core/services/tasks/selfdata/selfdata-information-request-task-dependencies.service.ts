import {Injectable} from '@angular/core';
import {DependencyFetcher} from '@shared/utils/dependencies-utils';
import {TaskWithDependencies} from '@shared/utils/task-utils';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {Task} from 'micro_service_modules/api-bpmn';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {OwnerInfo} from 'micro_service_modules/projekt/projekt-model';
import {SelfdataInformationRequest} from 'micro_service_modules/selfdata/selfdata-model';
import {OrganizationService} from 'micro_service_modules/strukture/api-strukture';
import {KonsultMetierService} from '../../konsult-metier.service';
import {TaskDependencies} from '../task-dependencies.interface';
import {TaskDependencyFetchers, TaskWithDependenciesService} from '../task-with-dependencies-service';
import {SelfdataInformationRequestTaskMetierService} from './selfdata-information-request-task-metier.service';
import {SelfdataTaskSearchCriteria} from './selfdata-task-search-criteria.interface';

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
