import {Injectable} from '@angular/core';
import {DependencyFetcher, ObjectWithDependencies, OwnerKey} from '../../shared/utils/dependencies-utils';
import {LinkedDataset, OwnerInfo, PagedProjectList, Project} from '../../projekt/projekt-model';
import {from, Observable} from 'rxjs';
import {map, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {ProjektService} from '../../projekt/projekt-api';
import {ProjektMetierService} from './projekt-metier.service';
import {mapEach} from '../../shared/utils/ObservableUtils';
import {Metadata} from '../../api-kaccess';
import {KonsultMetierService} from './konsult-metier.service';

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
}

/**
 * les dépendances attendues pour un projet
 */
export interface LinkedDatasetMetadatas {
    validatedLink?: LinkedDataset;
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
        private readonly projektMetierService: ProjektMetierService
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

    get linkedDatasetMetadatas(): DependencyFetcher<ProjectWithDependencies, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getLinkedDatasets(projectUuid).pipe(
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

    get validatedLinkedDatasetsMetadatas(): DependencyFetcher<ProjectWithDependencies, LinkedDatasetMetadatas[]> {
        return {
            hasPrerequisites: (input: ProjectWithDependencies) => ProjectDependenciesFetchers.hasProjectUuid(input),
            getKey: projectWithDependencies => projectWithDependencies.project.uuid,
            getValue: projectUuid => this.projektMetierService.getValidatedLinkedDatasets(projectUuid).pipe(
                switchMap((validatedLinkedDatasets: LinkedDataset[]) => {
                    return from(validatedLinkedDatasets).pipe(
                        mergeMap((validatedLinkedDataset: LinkedDataset) => {
                            return this.konsultMetierService.getMetadataByUuid(validatedLinkedDataset.dataset_uuid).pipe(
                                map((dataset: Metadata) => {
                                    return {validatedLinkedDataset, dataset};
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

    /**
     * Check les entrées des dépendances pour savoir si y'a bien la dépendance projet qui a été loadée
     * @param input l'entrée à checker
     * @private
     */
    private static hasProjectUuid(input: ProjectWithDependencies): boolean {
        return input != null && input.project != null && input.project.uuid != null;
    }
}
