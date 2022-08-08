import {Injectable} from '@angular/core';
import {KonsultMetierService} from './konsult-metier.service';
import {ProjektMetierService} from './projekt-metier.service';
import {DatasetConfidentiality, Indicators, NewDatasetRequest, ProjektService, TaskService} from '../../projekt/projekt-api';
import {DependencyFetcher, TaskWithDependencies} from '../../shared/utils/task-utils';
import {Metadata} from '../../api-kaccess';
import {LinkedDataset, OwnerInfo, OwnerType, Project, ProjectStatus} from '../../projekt/projekt-model';
import {mapEach} from '../../shared/utils/ObservableUtils';
import {TaskMetierService, TaskSearchCriteria} from './task-metier.service';
import {forkJoin, Observable} from 'rxjs';
import {Task} from '../../api-bpmn';
import {map, mapTo, switchMap} from 'rxjs/operators';

export interface OpenLinkedDatasetAccessRequest {
    linkedDataset: LinkedDataset;
    dataset: Metadata;
}

@Injectable({
    providedIn: 'root'
})
export class LinkedDatasetTaskService {

    constructor(
        private readonly taskService: TaskService,
        private readonly taskMetierService: TaskMetierService,
    ) {
    }

    getTask(taskId: string): Observable<LinkedDatasetTask> {
        return this.taskService.getTask(taskId).pipe(
            map(task => new LinkedDatasetTask(task))
        );
    }

    searchTasks(searchCriteria: ProjektTaskSearchCriteria = {}): Observable<LinkedDatasetTask[]> {
        return this.taskMetierService.searchTasks('LinkedDataset', searchCriteria).pipe(
            mapEach(task => new LinkedDatasetTask(task))
        );
    }
}

export interface ProjektTaskSearchCriteria extends TaskSearchCriteria {
    title?: string;
    projectStatus?: ProjectStatus;
    datasetProducerUuid?: string;
    projectUuid?: string;
}

class LinkedDatasetTask extends TaskWithDependencies<LinkedDataset, LinkedDatasetDependencies> {
    constructor(task: Task) {
        super(task, {});
    }
}

interface LinkedDatasetDependencies {
    dataset?: Metadata;
    project?: Project;
    ownerInfo?: OwnerInfo;
    otherLinkedDatasets?: Task[];
    otherIndicators?: Indicators;
    linkedDatasetsOpened?: OpenLinkedDatasetAccessRequest[];
    newDatasetRequests?: NewDatasetRequest[];
    projectLogo?: string;
}

@Injectable({
    providedIn: 'root'
})
export class LinkedDatasetTaskDependencyFetchers {

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly projektService: ProjektService,
        private readonly taskMetierService: TaskMetierService
    ) {
    }

    get dataset(): DependencyFetcher<LinkedDatasetTask, Metadata> {
        return {
            getKey: taskWithDependencies => taskWithDependencies.asset.dataset_uuid,
            getValue: datasetUuid => this.konsultMetierService.getMetadataByUuid(datasetUuid)
        };
    }

    get project(): DependencyFetcher<LinkedDatasetTask, Project> {
        return {
            getKey: taskWithDependencies => taskWithDependencies.asset.uuid,
            getValue: linkedDatasetUuid => this.projektMetierService.getLinkedProject(linkedDatasetUuid)
        };
    }

    get ownerInfo(): DependencyFetcher<LinkedDatasetTask, OwnerInfo> {
        return {
            getKey: taskWithDependencies => OwnerKey.serialize(taskWithDependencies.dependencies.project),
            getValue: ownerKey => {
                const {owner_type, owner_uuid} = OwnerKey.deserialize(ownerKey);
                return this.projektService.getOwnerInfo(owner_type, owner_uuid);
            }
        };
    }

    get otherLinkedDatasets(): DependencyFetcher<LinkedDatasetTask, Task[]> {
        return {
            getKey: taskWithDependencies => OtherLinksKey.serialize(taskWithDependencies.dependencies.project,
                taskWithDependencies.dependencies.dataset.producer, taskWithDependencies.task),
            getValue: otherLinksKey => {
                const {project_uuid, dataset_producer_uuid, task_id} = OtherLinksKey.deserialize(otherLinksKey);
                // On recherche les tâches de type : demande d'accès pour ce projet et ce producteur
                return this.taskMetierService.searchTasks('LinkedDataset', {
                    projectUuid: project_uuid,
                    datasetProducerUuid: dataset_producer_uuid
                }).pipe(
                    // On retire la demande actuelle car on veut les "autres"
                    map((tasks: Task[]) => tasks.filter((task: Task) => task.id !== task_id))
                );
            }
        };
    }

    get otherIndicators(): DependencyFetcher<LinkedDatasetTask, Indicators> {
        return {
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
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getLinkedDatasets(projectUuid).pipe(
                map((links: LinkedDataset[]) => {
                    return links.filter((link: LinkedDataset) =>
                        link.dataset_confidentiality === DatasetConfidentiality.Opened);
                }),
                switchMap((openLinks: LinkedDataset[]) => {
                    const openLinkedDatasetAccessRequests: OpenLinkedDatasetAccessRequest[] = [];
                    const observables = [];
                    openLinks.forEach((openLink: LinkedDataset) => {
                        const observable = this.konsultMetierService.getMetadataByUuid(openLink.dataset_uuid).pipe(
                            map((dataset: Metadata) => {
                                openLinkedDatasetAccessRequests.push({linkedDataset: openLink, dataset});
                            }));
                        observables.push(observable);
                    });
                    return forkJoin(observables).pipe(mapTo(openLinkedDatasetAccessRequests));
                })
            )
        };
    }

    get newDatasetRequests(): DependencyFetcher<LinkedDatasetTask, NewDatasetRequest[]> {
        return {
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektService.getNewDatasetRequests(projectUuid)
        };
    }

    get projectLogo(): DependencyFetcher<LinkedDatasetTask, string> {
        return {
            getKey: taskWithDependencies => taskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getProjectLogo(projectUuid)
        };
    }
}

interface HasOwnerTypeAndUuid {
    owner_type: OwnerType;
    owner_uuid: string;
}

interface HasUuid {
    uuid?: string;
}

interface HasOrganizationId {
    organization_id: string;
}

interface HasId {
    id?: string;
}

interface OtherLinkedDatasetParams {
    project_uuid: string;
    dataset_producer_uuid: string;
    task_id: string;
}

class OwnerKey {

    static serialize(value: HasOwnerTypeAndUuid): string {
        return `${value.owner_type}:${value.owner_uuid}`;
    }

    static deserialize(serial: string): HasOwnerTypeAndUuid {
        const [ownerType, ownerUuid] = serial.split(':');
        return {
            owner_type: ownerType as OwnerType,
            owner_uuid: ownerUuid
        };
    }
}

class OtherLinksKey {

    static serialize(value1: HasUuid, value2: HasOrganizationId, value3: HasId): string {
        return `${value1.uuid}:${value2.organization_id}:${value3.id}`;
    }

    static deserialize(serial: string): OtherLinkedDatasetParams {
        const [projectUuid, datasetProducerUuid, taskId] = serial.split(':');
        return {
            project_uuid: projectUuid,
            dataset_producer_uuid: datasetProducerUuid,
            task_id: taskId
        };
    }
}
