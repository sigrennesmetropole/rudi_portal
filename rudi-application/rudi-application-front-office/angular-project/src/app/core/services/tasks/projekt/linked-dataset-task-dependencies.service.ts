import {Injectable} from '@angular/core';
import {KonsultMetierService} from '../../konsult-metier.service';
import {ProjektMetierService} from '../../asset/project/projekt-metier.service';
import {DatasetConfidentiality, Indicators, NewDatasetRequest, ProjektService} from '../../../../projekt/projekt-api';
import {Metadata} from '../../../../api-kaccess';
import {LinkedDataset, OwnerInfo, Project} from '../../../../projekt/projekt-model';
import {from} from 'rxjs';
import {Task} from '../../../../api-bpmn';
import {map, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {DependencyFetcher, OtherLinksKey, OwnerKey} from '../../../../shared/utils/dependencies-utils';
import {TaskDependencyFetchers, TaskWithDependenciesService} from '../task-with-dependencies-service';
import {TaskWithDependencies} from '../../../../shared/utils/task-utils';
import {TaskDependencies} from '../task-dependencies.interface';
import {AclService} from '../../../../acl/acl-api';
import {OrganizationService} from '../../../../strukture/api-strukture';
import {LinkedDatasetTaskMetierService} from './linked-dataset-task-metier.service';
import {ProjektTaskSearchCriteria} from './projekt-task-search-criteria.interface';

export interface OpenLinkedDatasetAccessRequest {
    linkedDataset: LinkedDataset;
    dataset: Metadata;
}

/**
 * les dépendances attendues pour une tâche
 */
export interface LinkedDatasetDependencies extends TaskDependencies {
    dataset?: Metadata;
    project?: Project;
    ownerInfo?: OwnerInfo;
    otherLinkedDatasets?: Task[];
    otherIndicators?: Indicators;
    linkedDatasetsOpened?: OpenLinkedDatasetAccessRequest[];
    newDatasetRequests?: NewDatasetRequest[];
    projectLogo?: string;
}

/**
 * Objet permettant de charger les dépendances d'une tâche
 */
export class LinkedDatasetTask extends TaskWithDependencies<LinkedDataset, LinkedDatasetDependencies> {
    constructor(task: Task) {
        super(task, {});
    }
}

@Injectable({
    providedIn: 'root'
})
export class LinkedDatasetTaskDependenciesService
    extends TaskWithDependenciesService<LinkedDatasetTask, ProjektTaskSearchCriteria, LinkedDataset> {

    constructor(readonly linkedDatasetTaskMetierService: LinkedDatasetTaskMetierService) {
        super(linkedDatasetTaskMetierService);
    }

    newTaskWithDependencies(task: Task): LinkedDatasetTask {
        return new LinkedDatasetTask(task);
    }

    defaultSearchCriteria(): ProjektTaskSearchCriteria {
        return {};
    }
}

@Injectable({
    providedIn: 'root'
})
export class LinkedDatasetTaskDependencyFetchers
    extends TaskDependencyFetchers<LinkedDatasetTask, LinkedDataset, LinkedDatasetDependencies> {

    constructor(
        organizationService: OrganizationService,
        aclService: AclService,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly projektService: ProjektService,
        private readonly linkedDatasetTaskMetierService: LinkedDatasetTaskMetierService,
    ) {
        super(organizationService, aclService);
    }

    get dataset(): DependencyFetcher<LinkedDatasetTask, Metadata> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => input != null && input.asset != null && input.asset.dataset_uuid != null,
            getKey: taskWithDependencies => taskWithDependencies.asset.dataset_uuid,
            getValue: datasetUuid => this.konsultMetierService.getMetadataByUuid(datasetUuid)
        };
    }

    get project(): DependencyFetcher<LinkedDatasetTask, Project> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => input != null && input.asset != null && input.asset.uuid != null,
            getKey: taskWithDependencies => taskWithDependencies.asset.uuid,
            getValue: linkedDatasetUuid => this.projektMetierService.getLinkedProject(linkedDatasetUuid)
        };
    }

    get ownerInfo(): DependencyFetcher<LinkedDatasetTask, OwnerInfo> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => input != null && input.dependencies != null
                && input.dependencies.project != null,
            getKey: taskWithDependencies => OwnerKey.serialize(taskWithDependencies.dependencies.project),
            getValue: ownerKey => {
                const {owner_type, owner_uuid} = OwnerKey.deserialize(ownerKey);
                return this.projektMetierService.getOwnerInfo(owner_type, owner_uuid);
            }
        };
    }

    get otherLinkedDatasets(): DependencyFetcher<LinkedDatasetTask, Task[]> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasTaskProjectAndDatasetProducer(input),
            getKey: taskWithDependencies => OtherLinksKey.serialize(taskWithDependencies.dependencies.project,
                taskWithDependencies.dependencies.dataset.producer, taskWithDependencies.task),
            getValue: otherLinksKey => {
                const {project_uuid, dataset_producer_uuid, task_id} = OtherLinksKey.deserialize(otherLinksKey);
                // On recherche les tâches de type : demande d'accès pour ce projet et ce producteur
                const projectTaskSearchCriteria: ProjektTaskSearchCriteria = {
                    projectUuid: project_uuid,
                    datasetProducerUuid: dataset_producer_uuid
                };
                return this.linkedDatasetTaskMetierService.searchTasks(projectTaskSearchCriteria).pipe(
                    // On retire la demande actuelle car on veut les "autres"
                    map((tasks: Task[]) => tasks.filter((task: Task) => task.id !== task_id))
                );
            }
        };
    }

    get otherIndicators(): DependencyFetcher<LinkedDatasetTask, Indicators> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasTaskProjectAndDatasetProducer(input),
            getKey: taskWithDependencies => OtherLinksKey.serialize(taskWithDependencies.dependencies.project,
                taskWithDependencies.dependencies.dataset.producer, taskWithDependencies.task),
            getValue: otherLinksKey => {
                const {project_uuid, dataset_producer_uuid} = OtherLinksKey.deserialize(otherLinksKey);
                return this.projektService.computeIndicators(project_uuid, dataset_producer_uuid);
            }
        };
    }

    get linkedDatasetsOpened(): DependencyFetcher<LinkedDatasetTask, OpenLinkedDatasetAccessRequest[]> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasProjectUuid(input),
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getLinkedDatasets(projectUuid).pipe(
                map((links: LinkedDataset[]) => {
                    return links.filter((link: LinkedDataset) =>
                        link.dataset_confidentiality === DatasetConfidentiality.Opened);
                }),
                switchMap((openLinks: LinkedDataset[]) => {
                    return from(openLinks).pipe(
                        mergeMap((openLink: LinkedDataset) => {
                            return this.konsultMetierService.getMetadataByUuid(openLink.dataset_uuid).pipe(
                                map((dataset: Metadata) => {
                                    return {linkedDataset: openLink, dataset};
                                })
                            );
                        }),
                        reduce((accumulator: OpenLinkedDatasetAccessRequest[], current: OpenLinkedDatasetAccessRequest) => {
                            accumulator.push(current);
                            return accumulator;
                        }, [])
                    );
                })
            )
        };
    }

    get newDatasetRequests(): DependencyFetcher<LinkedDatasetTask, NewDatasetRequest[]> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasProjectUuid(input),
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektService.getNewDatasetRequests(projectUuid)
        };
    }

    get projectLogo(): DependencyFetcher<LinkedDatasetTask, string> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasProjectUuid(input),
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getProjectLogo(projectUuid)
        };
    }

    get validatedLinkedDatasets(): DependencyFetcher<LinkedDatasetTask, LinkedDataset[]> {
        return {
            hasPrerequisites: (input: LinkedDatasetTask) => LinkedDatasetTaskDependencyFetchers.hasProjectUuid(input),
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektService.getLinkedDatasets(projectUuid, 'VALIDATED')
        };
    }

    /**
     * Check les entrées des dépendances pour savoir si y'a bien la dépendance projet qui a été loadée
     * @param input l'entrée à checker
     * @private
     */
    private static hasProjectUuid(input: LinkedDatasetTask): boolean {
        return input != null && input.dependencies != null && input.dependencies.project != null && input.dependencies.project.uuid != null;
    }

    /**
     * Check les entrées des dépendances pour savoir s'il y a bien : task, project, dataset et dataset.producer
     * @param input entrée à checker
     * @private
     */
    private static hasTaskProjectAndDatasetProducer(input: LinkedDatasetTask): boolean {
        return input != null && input.dependencies != null && input.dependencies.project != null && input.dependencies.dataset != null
            && input.dependencies.dataset.producer != null && input.task != null;
    }
}
