import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute} from '@angular/router';
import {FormProjectDependencies, ProjectSubmissionService} from '@core/services/asset/project/project-submission.service';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {DialogSubscribeDatasetsService} from '@core/services/dialog-subscribe-datasets.service';
import {FiltersService} from '@core/services/filters.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {ProjectTaskMetierService} from '@core/services/tasks/projekt/project-task-metier.service';
import {CloseEvent} from '@features/data-set/models/dialog-closed-data';
import {ReuseProjectCommonComponent} from '@features/project/components/reuse-project-common/reuse-project-common.component';
import {UpdateAction} from '@features/project/model/upate-action';
import {TranslateService} from '@ngx-translate/core';
import {RadioListItem} from '@shared/radio-list/radio-list-item';
import {User} from 'micro_service_modules/acl/acl-api';
import {Project, ProjektService, ReutilisationStatus} from 'micro_service_modules/projekt/projekt-api';
import {Task} from 'micro_service_modules/projekt/projekt-api/model/task';
import {Confidentiality, ProjectStatus, Support, TargetAudience, TerritorialScale} from 'micro_service_modules/projekt/projekt-model';
import * as moment from 'moment';
import {forkJoin, of} from 'rxjs';
import {switchMap, tap} from 'rxjs/operators';

@Component({
    selector: 'app-project-main-informations',
    templateUrl: './project-main-informations.component.html',
    styleUrls: ['./project-main-informations.component.scss']
})
export class ProjectMainInformationsComponent extends ReuseProjectCommonComponent implements OnInit {
    @Input() project: Project;
    @Output() updateInProgressEvent = new EventEmitter<boolean>();
    @Output() updateCurrentTaskEvent = new EventEmitter<Task>();

    isUpdating = false;

    public suggestions: RadioListItem[];
    public publicCible: TargetAudience[];
    public reuseStatus: ReutilisationStatus[];
    public territorialScales: TerritorialScale[];
    private confidentialities: Confidentiality[];
    public supports: Support[];
    public isRefusedProject: boolean;
    public user: User;
    public taskId = '';
    public currentTask: Task;
    @Input() isProjectUpdatable = false;

    /**
     * L'action de mise à jour d'image à apppliquer pour un projet
     * @private
     */
    private updateImageAction: UpdateAction;

    /**
     * L'image du projet qui a été sauvegardée, pour savoir si une mise à jour a eu lieu
     * @private
     */
    private projectImageSaved: Blob;

    constructor(
        readonly projektMetierService: ProjektMetierService,
        readonly filtersService: FiltersService,
        readonly projectSubmissionService: ProjectSubmissionService,
        public dialog: MatDialog,
        readonly projektService: ProjektService,
        readonly projektTaskMetierService: ProjectTaskMetierService,
        private readonly personalSpaceProjectService: DialogSubscribeDatasetsService,
        private route: ActivatedRoute,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService
    ) {
        super(projektMetierService, filtersService, projectSubmissionService);
    }

    ngOnInit(): void {
        this.isRefusedProject = this.project?.project_status === ProjectStatus.Rejected;
        // Récupération de l'uuid du task
        this.taskId = this.route.snapshot.paramMap.get('taskId') || '0';
    }

    // Chargement des infos de la réutilisation
    loadProjectInformations(): void {
        this.projektTaskMetierService.getTask(this.taskId).subscribe(task => {
            this.currentTask = task;
        });
        this.suggestions = [];
        this.step1FormGroup = this.projectSubmissionService.initStep1ProjectFormGroup();
        this.isLoading = true;
        this.projectSubmissionService.loadDependenciesProject().subscribe(
            (dependencies: FormProjectDependencies) => {
                this.isLoading = false;
                this.suggestions = this.projectSubmissionService.getConfidentialitiesRadio(dependencies.confidentialities);
                this.confidentialities = dependencies.confidentialities;
                this.publicCible = dependencies.projectPublicCible;
                this.territorialScales = dependencies.territorialScales;
                this.projectType = dependencies.projectTypes;
                this.supports = dependencies.supports;
                this.user = dependencies.user;
                this.reuseStatus = dependencies.reuseStatus;
            }
        );
        this.step1FormGroup.patchValue({
            title: this.project.title,
            description: this.project.description,
            reuse_status: this.project.reutilisation_status,
            begin_date: this.project.expected_completion_start_date ? moment(this.project.expected_completion_start_date) : null,
            end_date: this.project.expected_completion_end_date ? moment(this.project.expected_completion_end_date) : null,
            publicCible: this.project.target_audiences,
            echelle: this.project.territorial_scale,
            territoire: this.project.detailed_territorial_scale,
            accompagnement: this.project.desired_supports,
            type: this.project.type,
            url: this.project.access_url,
            confidentiality: this.project.confidentiality,
        });
    }

    // Activation du mode modification
    updateProjectTaskInfo(): void {
        this.loadProjectInformations();
        this.isUpdating = !this.isUpdating;
        this.updateInProgressEvent.emit(this.isUpdating);
    }

    /**
     * On gère le fait que l'image saisie a l'étape 1 change
     * @param image l'image qui a été saisie
     */
    handleImageChanged(image: Blob): void {
        // On détermine l'action de mise à jour à faire si on update le projet
        if (this.projectImageSaved == null && image) {
            this.updateImageAction = UpdateAction.AJOUT;
        } // Si l'utillisateur saisit une image et qu'il y'en avait déjà une avant ET si leurs tailles sont différentes
        // Comparaison de blob nécessite fileReader qui bloque le Thread
        else if (this.projectImageSaved && image && this.projectImageSaved.size !== image.size) {
            this.updateImageAction = UpdateAction.MISE_A_JOUR;
        } else if (this.projectImageSaved && image == null) {
            this.updateImageAction = UpdateAction.SUPPRESSION;
        }
    }

    /**
     * Ouverture d'une dialog permettant de confirmer la modification de la réutilisation
     */
    public updateConfirmation(): void {
        this.personalSpaceProjectService.openDialogUpdateConfirmation()
            .subscribe(item => {
                if (item && item.closeEvent === CloseEvent.VALIDATION) {
                    this.updateProjectTask();
                }
            });
    }

    public updateProjectTask(): void {
        this.updateTaskFromForm();
        this.isLoading = true;
        this.projektTaskMetierService.claimTask(this.taskId).pipe(
            switchMap(item => {
                return forkJoin([
                    this.projektTaskMetierService.updateTask(this.currentTask),
                    of(item) // Utilisation de of pour conserver la valeur du premier switchMap
                ]);
            }),
            tap(([updatedTask, item]) => {
                this.project = updatedTask.asset as Project;
                this.projektMetierService.uploadLogo(this.project.uuid, this.step1FormGroup.get('image').value.file).subscribe();
            }),
            switchMap(() => {
                return this.projektTaskMetierService.unclaimTask(this.taskId);
            })
        ).subscribe({
            next: () => {
                this.isUpdating = false;
                this.updateInProgressEvent.emit(this.isUpdating);
                this.updateCurrentTaskEvent.emit(this.currentTask);
                this.isLoading = false;
                this.snackBarService.showSuccess(this.translateService.instant('personalSpace.project.tabs.update.success'));
            },
            error: (e) => {
                console.error(e);
                this.snackBarService.add(this.translateService.instant('personalSpace.project.tabs.update.error'));
                this.isLoading = false;
            }
        });
    }

    /**
     * MAJ de l'objet Project à partir de l'état du formulaire de saisie
     * @private
     */
    private updateTaskFromForm(): void {
        this.projectSubmissionService.updateProjectTaskField(this.currentTask, this.step1FormGroup, this.confidentialities);
    }
}
