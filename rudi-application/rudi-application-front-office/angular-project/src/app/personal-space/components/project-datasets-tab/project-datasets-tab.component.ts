import {Component, Input, OnInit} from '@angular/core';
import {injectDependencies} from '../../../shared/utils/dependencies-utils';
import {
    LinkedDatasetMetadatas,
    ProjectDependenciesFetchers,
    ProjectDependenciesService, ProjectWithDependencies
} from '../../../core/services/project-dependencies.service';
import {of} from 'rxjs';
import {Project} from '../../../projekt/projekt-model';
import {DialogSubscribeDatasetsService} from '../../../core/services/dialog-subscribe-datasets.service';
import {switchMap, tap} from 'rxjs/operators';
import {KonsultApiAccessService} from '../../../core/services/api-access/konsult/konsult-api-access.service';

@Component({
    selector: 'app-project-datasets-tab',
    templateUrl: './project-datasets-tab.component.html',
    styleUrls: ['./project-datasets-tab.component.scss']
})
export class ProjectDatasetsTabComponent implements OnInit {

    /**
     * Toutes les demandes d'un projet avec le JDD
     */
    linkedDatasetMetadatas: LinkedDatasetMetadatas[] = [];

    /**
     * Demandes enrichies auxquelles on peut souscrire
     */
    subscribableLinkedDatasetMetadatas: LinkedDatasetMetadatas[] = [];

    /**
     * Loader de l'onglet
     */
    loading = false;

    /**
     * S'il y'a eu une erreur pendant la récupération des deps de l'onglet
     */
    initializationError = false;

    /**
     * Projet de l'onglet
     */
    _project: Project;

    @Input()
    set project(value: Project) {
        this._project = value;
        if (this._project) {
            this.loading = true;
            of(new ProjectWithDependencies(this._project, {})).pipe(
                injectDependencies({
                    linkedDatasetMetadatas: this.projectDependenciesFetchers.linkedDatasetMetadatas
                }),
                tap((projectWithDependencies: ProjectWithDependencies) => {
                    this.linkedDatasetMetadatas = projectWithDependencies.dependencies.linkedDatasetMetadatas;
                }),
                switchMap((projectWithDependencies: ProjectWithDependencies) => this.apiAccessService.filterSubscribableMetadatas(
                        projectWithDependencies.dependencies.linkedDatasetMetadatas,
                        this._project
                    )
                ),
                tap((subscribables: LinkedDatasetMetadatas[]) => {
                    this.subscribableLinkedDatasetMetadatas = subscribables;
                })
            ).subscribe({
                complete: () => {
                    this.loading = false;
                    this.initializationError = false;
                },
                error: (error) => {
                    console.error(error);
                    this.initializationError = true;
                    this.loading = false;
                }
            });
        }
    }

    constructor(private readonly personalSpaceProjectService: DialogSubscribeDatasetsService,
                private readonly apiAccessService: KonsultApiAccessService,
                private readonly projectDependenciesService: ProjectDependenciesService,
                private readonly projectDependenciesFetchers: ProjectDependenciesFetchers) {
    }

    ngOnInit(): void {
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Souscrire", ouverture de popin
     */
    public handleClickSubscribe(): void {
        this.personalSpaceProjectService.openDialogSelectDatasetsToSubscribe(this.subscribableLinkedDatasetMetadatas, this._project)
            .subscribe({
                error: (error) => {
                    console.error(error);
                }
            });
    }
}
