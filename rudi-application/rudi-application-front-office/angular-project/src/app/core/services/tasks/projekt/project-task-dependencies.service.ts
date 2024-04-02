import {Injectable} from '@angular/core';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {Task} from 'micro_service_modules/api-bpmn';
import {OrganizationService} from 'micro_service_modules/strukture/api-strukture';
import {DependencyFetcher, OwnerKey} from '@shared/utils/dependencies-utils';
import {TaskWithDependencies} from '@shared/utils/task-utils';
import {NewDatasetRequest, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {OwnerInfo, Project} from 'micro_service_modules/projekt/projekt-model';
import {ProjectConsultationService} from '../../asset/project/project-consultation.service';
import {LinkedDatasetMetadatas} from '../../asset/project/project-dependencies.service';
import {ProjektMetierService} from '../../asset/project/projekt-metier.service';
import {KonsultMetierService} from '../../konsult-metier.service';
import {TaskDependencies} from '../task-dependencies.interface';
import {TaskDependencyFetchers, TaskWithDependenciesService} from '../task-with-dependencies-service';
import {ProjectTaskMetierService} from './project-task-metier.service';
import {ProjektTaskSearchCriteria} from './projekt-task-search-criteria.interface';

export interface ProjectDependencies extends TaskDependencies {
    project?: Project;
    logo?: string;
    ownerInfo?: OwnerInfo;
    openLinkedDatasets?: LinkedDatasetMetadatas[];
    restrictedLinkedDatasets?: LinkedDatasetMetadatas[];
    newDatasetsRequest?: NewDatasetRequest[];
}

export class ProjectTask extends TaskWithDependencies<Project, ProjectDependencies> {
    constructor(task: Task) {
        super(task, {});
    }
}

@Injectable({
    providedIn: 'root'
})
export class ProjectTaskDependenciesService extends TaskWithDependenciesService<ProjectTask, ProjektTaskSearchCriteria, Project> {
    constructor(readonly projectTaskMetierService: ProjectTaskMetierService) {
        super(projectTaskMetierService);
    }

    newTaskWithDependencies(task: Task): ProjectTask {
        return new ProjectTask(task);
    }

    defaultSearchCriteria(): ProjektTaskSearchCriteria {
        return {};
    }
}

@Injectable({
    providedIn: 'root'
})
export class ProjectTaskDependencyFetcher extends TaskDependencyFetchers<ProjectTask, Project, ProjectDependencies> {

    constructor(organizationService: OrganizationService,
                aclService: AclService,
                private readonly konsultMetierService: KonsultMetierService,
                private readonly projektService: ProjektService,
                private readonly projektMetierService: ProjektMetierService,
                private readonly projectConsultService: ProjectConsultationService, ) {
        super(organizationService, aclService);
    }

    get project(): DependencyFetcher<ProjectTask, Project> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProjectUuid(input),
            getKey: taskWithDependencies => taskWithDependencies.asset.uuid,
            getValue: uuid => this.projektMetierService.getProject(uuid)
        };
    }

    get logo(): DependencyFetcher<ProjectTask, string> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.task.functionalId,
            getValue: projectUuid => this.projektMetierService.getProjectLogo(projectUuid)
        };
    }

    get ownerInfo(): DependencyFetcher<ProjectTask, OwnerInfo> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProject(input),
            getKey: projectWithDependencies => OwnerKey.serialize(projectWithDependencies.dependencies.project),
            getValue: ownerKey => {
                const {owner_type, owner_uuid} = OwnerKey.deserialize(ownerKey);
                return this.projektMetierService.getOwnerInfo(owner_type, owner_uuid);
            }
        };
    }

    get openLinkedDatasets(): DependencyFetcher<ProjectTask, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProject(input),
            getKey: projectTaskWithDependencies => projectTaskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projectConsultService.getOpenedLinkedDatasetsMetadata(projectUuid)
        };
    }
    get restrictedLinkedDatasets(): DependencyFetcher<ProjectTask, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProject(input),
            getKey: projectTaskWithDependencies => projectTaskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projectConsultService.getRestrictedLinkedDatasetsMetadata(projectUuid)
        };
    }

    get newDatasetsRequest(): DependencyFetcher<ProjectTask, NewDatasetRequest[]> {
        return {
            hasPrerequisites: (input: ProjectTask) => ProjectTaskDependencyFetcher.hasProject(input),
            getKey: projectTaskWithDependencies => projectTaskWithDependencies.dependencies.project.uuid,
            getValue: projectUuid => this.projectConsultService.getNewDatasetsRequest(projectUuid)
        };
    }

    /**
     * Check les entrées des dépendances pour savoir s'il y a bien la dépendance projet qui a été loadée
     * @param input l'entrée à checker
     * @private
     */
    private static hasProjectUuid(input: ProjectTask): boolean {
        return input != null && input.task != null && input.task.functionalId != null;
    }

    /**
     *
     */
    private static hasProject(input: ProjectTask): boolean {
        return input != null && input.dependencies != null && input.dependencies.project != null;
    }

}
