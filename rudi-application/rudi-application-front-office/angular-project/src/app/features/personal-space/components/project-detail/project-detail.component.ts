import {Component, Input, OnInit} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';

import {map, switchMap} from 'rxjs/operators';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {Base64EncodedLogo} from '@core/services/image-logo.service';
import {PageTitleService} from '@core/services/page-title.service';
import {
    LinkedDatasetTaskDependenciesService,
    LinkedDatasetTaskDependencyFetchers,
    OpenLinkedDatasetAccessRequest
} from '@core/services/tasks/projekt/linked-dataset-task-dependencies.service';
import {
    NewDatasetRequestTaskDepenciesService,
    NewDatasetRequestTaskDependencyFetchers
} from '@core/services/tasks/projekt/new-dataset-request-task-depencies.service';
import {Indicators, NewDatasetRequest, Task} from 'micro_service_modules/projekt/projekt-api';
import {Project} from 'micro_service_modules/projekt/projekt-model';
import {injectDependencies} from '@shared/utils/dependencies-utils';

/**
 * Les dépendances qu'on doit afficher dans cet onglet
 */
export interface ProjectDependencies {

    /**
     * Le projet de l'onglet
     */
    project?: Project;

    /**
     * Les "autres" demandes, c'est à dire les demandes d'accès restreintes qui concernent le même producteur
     * que celui de la demande actuelle
     */
    otherLinkedDatasets?: Task[];

    /**
     * Le X et N infos sur les autres demandes d'accès restreintes
     */
    otherIndicators?: Indicators;

    /**
     * Les demandes d'accès à des JDDs ouverts
     */
    linkedDatasetsOpened?: OpenLinkedDatasetAccessRequest[];

    /**
     * Les demandes de nouvelles données
     */
    newDatasetRequests?: NewDatasetRequest[];

    /**
     * Le logo du projet
     */
    logo?: string;
}

@Component({
    selector: 'app-project-detail',
    templateUrl: './project-detail.component.html',
    styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {

    loading: boolean;
    mediaSize: MediaSize;
    formatsMenuActive = false;
    projectLogo: Base64EncodedLogo = '/assets/images/logo_projet_par_defaut.png';

    dependencies: ProjectDependencies;

    @Input()
    isNewRequest: boolean;

    constructor(
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly route: ActivatedRoute,
        private readonly projektMetierService: ProjektMetierService,
        private readonly router: Router,
        private readonly aclService: AclService,
        private readonly translateService: TranslateService,
        private readonly pageTitleService: PageTitleService,
        private readonly linkedDatasetTaskDependenciesService: LinkedDatasetTaskDependenciesService,
        private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
        private readonly newDatasetRequestTaskDepenciesService: NewDatasetRequestTaskDepenciesService,
        private readonly newDatasetRequestTaskDependencyFetchers: NewDatasetRequestTaskDependencyFetchers
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }

    ngOnInit(): void {
        // Récupération de l'ID de la tâche dans la route
        this.loading = true;
        this.route.params.pipe(
            switchMap((params: Params) => {
                // Si ID on charge les dépendances de l'onglet
                if (params.taskId) {
                    if (this.isNewRequest) {
                        return this.loadProjectDependenciesForNewDatasetRequest(params.taskId);
                    } else {
                        return this.loadProjectDependenciesForLinkedDataset(params.taskId);
                    }
                }
                // Sinon erreur on peut pas afficher l'onglet
                throw Error('Erreur pas d\'UUID de tâche pour afficher l\'onglet');
            })
        ).subscribe({
            next: (dependencies: ProjectDependencies) => {
                this.dependencies = dependencies;
                this.loading = false;
            },
            error: (error) => {
                console.error(error);
                this.loading = false;
                this.dependencies = {};
            }
        });
    }

    /**
     * Récupère les dépendances à afficher côté front pour le projet
     * @param taskUuid l'uuid de la tâche à récupérer pour récupérer le projet et les dépendances d'un linkedDataset
     * @private
     */
    private loadProjectDependenciesForLinkedDataset(taskUuid: string): Observable<ProjectDependencies> {
        return this.linkedDatasetTaskDependenciesService.getTaskWithDependencies(taskUuid).pipe(
            injectDependencies({
                dataset: this.linkedDatasetDependencyFetchers.dataset,
                project: this.linkedDatasetDependencyFetchers.project
            }),
            injectDependencies({
                otherLinkedDatasets: this.linkedDatasetDependencyFetchers.otherLinkedDatasets,
                otherIndicators: this.linkedDatasetDependencyFetchers.otherIndicators,
                newDatasetRequests: this.linkedDatasetDependencyFetchers.newDatasetRequests,
                projectLogo: this.linkedDatasetDependencyFetchers.projectLogo,
                linkedDatasetsOpened: this.linkedDatasetDependencyFetchers.linkedDatasetsOpened,
            }),
            map(({task, asset, dependencies}) => {
                return {
                    project: dependencies.project,
                    otherLinkedDatasets: dependencies.otherLinkedDatasets,
                    otherIndicators: dependencies.otherIndicators,
                    linkedDatasetsOpened: dependencies.linkedDatasetsOpened,
                    newDatasetRequests: dependencies.newDatasetRequests,
                    logo: dependencies.projectLogo
                };
            })
        );
    }

    /**
     * Récupère les dépendances à afficher côté front pour le projet
     * @param taskUuid l'uuid de la tâche à récupérer pour récupérer le projet et les dépendances d'une newDatasetRequest
     * @private
     */
    private loadProjectDependenciesForNewDatasetRequest(taskUuid: string): Observable<ProjectDependencies> {
        return this.newDatasetRequestTaskDepenciesService.getTaskWithDependencies(taskUuid).pipe(
            injectDependencies({
                project: this.newDatasetRequestTaskDependencyFetchers.project
            }),
            map(({task, asset, dependencies}) => {
                return {
                    project: dependencies.project,
                };
            })
        );
    }
}
