import {Injectable} from '@angular/core';
import {DependencyFetcher, ObjectWithDependencies, OwnerKey} from '@shared/utils/dependencies-utils';
import {mapEach} from '@shared/utils/ObservableUtils';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {NewDatasetRequest, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {LinkedDataset, LinkedDatasetStatus, OwnerInfo, PagedProjectList, Project} from 'micro_service_modules/projekt/projekt-model';
import {from, Observable} from 'rxjs';
import {map, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {KonsultMetierService} from '../../konsult-metier.service';
import {ProjectConsultationService} from './project-consultation.service';
import {ProjektMetierService} from './projekt-metier.service';

/**
 * Objet pour charger les dépendances d'un projet
 */
export class ProjectWithDependencies extends ObjectWithDependencies<ProjectDependencies> {
    constructor(readonly project: Project, readonly dependencies: ProjectDependencies) {
        super(dependencies);
        this.project = project;
    }
}

/**
 * les dépendances attendues pour un projet
 */
interface ProjectDependencies {
    ownerInfo?: OwnerInfo;
    logo?: string;
    numberOfRequests?: number;
    linkedDatasetMetadatas?: LinkedDatasetMetadatas[];
    dataset?: Metadata;
    linkedDatasetsRestricted?: LinkedDatasetMetadatas[];
    newDatasetRequests?: NewDatasetRequest[];
    linkedDatasetsOpened?: LinkedDatasetMetadatas[];
}

/**
 * les dépendances attendues pour un projet
 */
export interface LinkedDatasetMetadatas {
    linkedDataset?: LinkedDataset;
    dataset?: Metadata;
}

@Injectable({
    providedIn: 'root'
})
export class ProjectDependenciesService {

    constructor(
        private readonly projektService: ProjektService
    ) {
    }

    getProject(projectUuid: string): Observable<ProjectWithDependencies> {
        return this.projektService.getProject(projectUuid).pipe(
            map((project: Project) => {
                return new ProjectWithDependencies(project, {});
            })
        );
    }

    searchMyProjects(observablePageProjects: Observable<PagedProjectList>): Observable<ProjectWithDependencies[]> {
        return observablePageProjects.pipe(
            map(({total, elements}) => {
                return elements;
            }),
            mapEach(project => new ProjectWithDependencies(project, {}))
        );
    }
}

@Injectable({
    providedIn: 'root'
})
export class ProjectDependenciesFetchers {

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly projektService: ProjektService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly projectConsultService: ProjectConsultationService,
    ) {
    }

    get ownerInfo(): DependencyFetcher<ProjectWithDependencies, OwnerInfo> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => input != null && input.project != null,
            getKey: projectWithDependencies => OwnerKey.serialize(projectWithDependencies.project),
            getValue: ownerKey => {
                const {owner_type, owner_uuid} = OwnerKey.deserialize(ownerKey);
                return this.projektMetierService.getOwnerInfo(owner_type, owner_uuid);
            }
        };
    }

    get logo(): DependencyFetcher<ProjectWithDependencies, string> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getProjectLogo(projectUuid)
        };
    }

    get numberOfRequests(): DependencyFetcher<ProjectWithDependencies, number> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektService.getNumberOfRequests(projectUuid)
        };
    }


    get linkedDatasetsOpened(): DependencyFetcher<ProjectWithDependencies, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projectConsultService.getOpenedLinkedDatasetsMetadata(projectUuid)
        };
    }

    get linkedDatasetsRestricted(): DependencyFetcher<ProjectWithDependencies, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projectConsultService.getRestrictedLinkedDatasetsMetadata(projectUuid)
        };
    }

    get newDatasetRequests(): DependencyFetcher<ProjectWithDependencies, NewDatasetRequest[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektService.getNewDatasetRequests(projectUuid)
        };
    }
    /**
     * Check les entrées des dépendances pour savoir si y'a bien la dépendance projet qui a été loadée
     * @param input l'entrée à checker
     * @private
     */
    private static hasProjectUuid(input: ProjectWithDependencies): boolean {
        return input != null && input.project != null && input.project.uuid != null;
    }

    linkedDatasetMetadatas(status?: LinkedDatasetStatus[]): DependencyFetcher<ProjectWithDependencies, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getLinkedDatasets(projectUuid, status).pipe(
                switchMap((linkedDatasets: LinkedDataset[]) => {
                    return from(linkedDatasets).pipe(
                        mergeMap((linkedDataset: LinkedDataset) => {
                            return this.konsultMetierService.getMetadataByUuid(linkedDataset.dataset_uuid).pipe(
                                map((dataset: Metadata) => {
                                    return {linkedDataset, dataset};
                                })
                            );
                        }),
                        reduce((accumulator: LinkedDatasetMetadatas[], current: LinkedDatasetMetadatas) => {
                            accumulator.push(current);
                            return accumulator;
                        }, [])
                    );
                })
            )
        };
    }
}
