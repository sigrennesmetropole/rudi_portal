import {Component, OnInit} from '@angular/core';
import {Project} from '../../../projekt/projekt-model';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {ProjektMetierService} from '../../../core/services/projekt-metier.service';
import {AclService} from '../../../acl/acl-api';
import {TranslateService} from '@ngx-translate/core';
import {PageTitleService} from '../../../core/services/page-title.service';
import {Base64EncodedLogo} from '../../../core/services/image-logo.service';
import {Observable} from 'rxjs';
import {
    Indicators,
    NewDatasetRequest,
    Task
} from '../../../projekt/projekt-api';
import {TaskMetierService} from '../../../core/services/task-metier.service';
import {injectDependencies} from '../../../shared/utils/task-utils';
import {
    LinkedDatasetTaskDependencyFetchers,
    LinkedDatasetTaskService,
    OpenLinkedDatasetAccessRequest
} from '../../../core/services/linked-dataset-task.service';
import {map, switchMap, tap} from 'rxjs/operators';
import * as moment from 'moment';

const ICON_INFO = '../assets/icons/icon_info_default_color.svg';

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

    constructor(
        private readonly matIconRegistry: MatIconRegistry,
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly route: ActivatedRoute,
        private readonly projektMetierService: ProjektMetierService,
        private readonly router: Router,
        private readonly aclService: AclService,
        private readonly translateService: TranslateService,
        private readonly pageTitleService: PageTitleService,
        private readonly taskMetierService: TaskMetierService,
        private readonly linkedDatasetTaskService: LinkedDatasetTaskService,
        private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.matIconRegistry.addSvgIcon(
            'icon-info',
            this.domSanitizer.bypassSecurityTrustResourceUrl(ICON_INFO)
        );
    }

    ngOnInit(): void {
        // Récupération de l'ID de la tâche dans la route
        this.loading = true;
        this.route.params.pipe(
            switchMap((params: Params) => {
                // Si ID on charge les dépendances de l'onglet
                if (params.taskId) {
                    return this.loadProjectDependencies(params.taskId);
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
     * @param taskUuid l'uuid de la tâche à récupérer pour récupérer le projet et ses dépendances
     * @private
     */
    private loadProjectDependencies(taskUuid: string): Observable<ProjectDependencies> {
        // TODO RUDI-2273 bug chargement infini à corriger autrement qu'en décalant les injectDependencies
        return this.linkedDatasetTaskService.getTask(taskUuid).pipe(
            injectDependencies({
                dataset: this.linkedDatasetDependencyFetchers.dataset,
                project: this.linkedDatasetDependencyFetchers.project,
            }),
            injectDependencies({
                otherLinkedDatasets: this.linkedDatasetDependencyFetchers.otherLinkedDatasets,
                otherIndicators: this.linkedDatasetDependencyFetchers.otherIndicators
            }),
            injectDependencies({
                newDatasetRequests: this.linkedDatasetDependencyFetchers.newDatasetRequests,
                projectLogo: this.linkedDatasetDependencyFetchers.projectLogo
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
     * permet de récupérer la une date d'un champ de l'objet projet au format MM/YYYY
     * @param fieldName le nom du champ date de l'objet project
     * @private
     */
    private getProjectDate(fieldName: string): string {
        if (this.dependencies && this.dependencies.project && this.dependencies.project[fieldName] != null) {
            return moment(this.dependencies.project[fieldName]).format('MM/YYYY');
        }

        return '-';
    }

    /**
     * Récupère la date de début du projet au format MM/YYYY
     */
    public getProjectDateDebut(): string {
        return this.getProjectDate('expected_completion_start_date');
    }

    /**
     * Récupère la date de fin du projet au format MM/YYYY
     */
    public getProjectDateFin(): string {
        return this.getProjectDate('expected_completion_end_date');
    }

    /**
     * Récupération d'une chaîne de caractère représentant les niveaux d'accompagnements souhaités
     */
    public getDesiredSupports(): string {
        if (this.dependencies && this.dependencies.project && this.dependencies.project.desired_supports
            && this.dependencies.project.desired_supports.length > 0) {
            const labels = this.dependencies.project.desired_supports.map(support => support.label);
            return labels.join(', ');
        }

        return '-';
    }
}
