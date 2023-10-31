import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {LinkedDatasetFromProject} from '@app/data-set/models/linked-dataset-from-project';
import {NewDatasetRequest, ProjektService} from '@app/projekt/projekt-api';
import {Project} from '@app/projekt/projekt-model';
import {ProjectConsultationService} from '@core/services/asset/project/project-consultation.service';
import {LinkedDatasetMetadatas} from '@core/services/asset/project/project-dependencies.service';
import {ProjectSubmissionService} from '@core/services/asset/project/project-submission.service';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {DataSetActionsAuthorizationService} from '@core/services/data-set/data-set-actions-authorization.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {
    ProjectDependencies,
    ProjectTask,
    ProjectTaskDependenciesService,
    ProjectTaskDependencyFetcher
} from '@core/services/tasks/projekt/project-task-dependencies.service';
import {ProjectTaskMetierService} from '@core/services/tasks/projekt/project-task-metier.service';
import {ProjektTaskSearchCriteria} from '@core/services/tasks/projekt/projekt-task-search-criteria.interface';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {TaskDetailComponent} from '@shared/task-detail/task-detail.component';
import {injectDependencies} from '@shared/utils/dependencies-utils';
import {map, switchMap, tap} from 'rxjs/operators';

@Component({
    selector: 'app-project-task-detail',
    templateUrl: './project-task-detail.component.html',
    styleUrls: ['./project-task-detail.component.scss']
})
export class ProjectTaskDetailComponent
    extends TaskDetailComponent<Project, ProjectDependencies, ProjectTask, ProjektTaskSearchCriteria>
    implements OnInit {

    isLoading: boolean;
    public dependencies: ProjectDependencies;
    addingInProgress: boolean = false;

    addActionAuthorized: boolean = false;
    deleteActionAuthorized: boolean = false;


    constructor(
        private readonly route: ActivatedRoute,
        private readonly router: Router,
        private readonly projectTaskDependencyFetcher: ProjectTaskDependencyFetcher,
        private readonly iconRegistry: MatIconRegistry,
        private readonly sanitizer: DomSanitizer,
        private readonly projektService: ProjektService,
        private readonly dataSetActionsAuthorizationService: DataSetActionsAuthorizationService,
        readonly dialog: MatDialog,
        readonly translateService: TranslateService,
        readonly snackBarService: SnackBarService,
        readonly taskWithDependenciesService: ProjectTaskDependenciesService,
        readonly projectTaskMetierService: ProjectTaskMetierService,
        readonly projektMetierService: ProjektMetierService,
        readonly projectSubmissionService: ProjectSubmissionService,
        readonly projectConsultService: ProjectConsultationService,
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService, projectTaskMetierService);
        iconRegistry.addSvgIcon('project-svg-icon',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/process-definitions-key/project_definition_key.svg'));
    }


    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            this.isLoading = true;
            this.taskWithDependenciesService.getTaskWithDependencies(idTask).pipe(
                tap(taskWithDependencies => this.taskWithDependencies = taskWithDependencies),
                injectDependencies({
                    project: this.projectTaskDependencyFetcher.project,
                    logo: this.projectTaskDependencyFetcher.logo,
                }),
                injectDependencies({
                    ownerInfo: this.projectTaskDependencyFetcher.ownerInfo,
                    openLinkedDatasets: this.projectTaskDependencyFetcher.openLinkedDatasets,
                    restrictedLinkedDatasets: this.projectTaskDependencyFetcher.restrictedLinkedDatasets,
                    newDatasetsRequest: this.projectTaskDependencyFetcher.newDatasetsRequest,
                }),
                map(({task, asset, dependencies}) => {
                    return {
                        logo: dependencies.logo,
                        project: dependencies.project,
                        ownerInfo: dependencies.ownerInfo,
                        openLinkedDatasets: dependencies.openLinkedDatasets,
                        restrictedLinkedDatasets: dependencies.restrictedLinkedDatasets,
                        newDatasetsRequest: dependencies.newDatasetsRequest
                    };
                })
            ).subscribe({
                next: (dependencies: ProjectDependencies) => {
                    this.dependencies = dependencies;
                    this.isLoading = false;
                    this.projektService.isAuthenticatedUserProjectOwner(dependencies.project.uuid).subscribe(isOwner => {
                        this.addActionAuthorized = isOwner && this.dataSetActionsAuthorizationService.canAddDatasetFromProject(dependencies.project);
                        this.deleteActionAuthorized = isOwner && this.dataSetActionsAuthorizationService.canDeleteDatasetFromProject(dependencies.project);
                    });
                },
                error: (error) => {
                    this.isLoading = false;
                    console.error(error);
                }
            });
        }
    }

    protected goBackToList(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-notifications']);
    }

    updateAddButtonStatus(buttonStatus: boolean): void {
        this.addingInProgress = buttonStatus;
    }

    addLinkedDatasetAndReloadDependencies(linkToCreate: LinkedDatasetFromProject, isRestricted: boolean): void {
        this.updateAddButtonStatus(true);
        linkToCreate.project = this.dependencies.project;
        this.isLoading = true;
        this.projectSubmissionService.createLinkedDatasetFromProject(linkToCreate).pipe(
            // Reload dependencies
            switchMap(() => {
                if (isRestricted) {
                    return this.projectConsultService.getRestrictedLinkedDatasetsMetadata(this.dependencies.project.uuid).pipe(
                        tap((links: LinkedDatasetMetadatas[]) => {
                            this.dependencies.restrictedLinkedDatasets = links;
                            this.isLoading = false;
                            this.addingInProgress = false;
                        })
                    );
                }

                return this.projectConsultService.getOpenedLinkedDatasetsMetadata(this.dependencies.project.uuid).pipe(
                    tap((links: LinkedDatasetMetadatas[]) => {
                        this.dependencies.openLinkedDatasets = links;
                        this.isLoading = false;
                        this.addingInProgress = false;
                    })
                );
            }),
        ).subscribe({
            error: err => {
                console.error(err);
                this.isLoading = false;
                this.addingInProgress = false;
            }
        });
    }

    handleOpenDatasetRequestUuidChanged(openDatasetRequestUuid: string): void {
        this.isLoading = true;
        this.projektMetierService.deleteLinkedDatasetRequest(this.dependencies.project.uuid, openDatasetRequestUuid).pipe(
            // Reload dependencies
            switchMap(() => this.projectConsultService.getOpenedLinkedDatasetsMetadata(this.dependencies.project.uuid)),
            tap((links: LinkedDatasetMetadatas[]) => {
                this.dependencies.openLinkedDatasets = links;
                this.isLoading = false;
            })
        ).subscribe({
            error: err => {
                console.error(err);
                this.isLoading = false;
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('personalSpace.projectDatasets.delete.error'),
                    level: Level.ERROR
                });
            }
        });
    }

    handleRestrictedDatasetRequestUuidChanged(restrictedDatasetRequestUuid: string): void {
        this.isLoading = true;
        this.projektMetierService.deleteLinkedDatasetRequest(this.dependencies.project.uuid, restrictedDatasetRequestUuid).pipe(
            // Reload dependencies
            switchMap(() => this.projectConsultService.getRestrictedLinkedDatasetsMetadata(this.dependencies.project.uuid)),
            tap((links: LinkedDatasetMetadatas[]) => {
                this.dependencies.restrictedLinkedDatasets = links;
                this.isLoading = false;
            })
        ).subscribe({
            error: err => {
                console.error(err);
                this.isLoading = false;
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('personalSpace.projectDatasets.delete.error'),
                    level: Level.ERROR
                });
            }
        });
    }

    addNewDatasetRequest(linkToCreate: NewDatasetRequest): void {
        this.updateAddButtonStatus(true);
        const projectUuid = this.dependencies.project.uuid;
        this.isLoading = true;
        this.projectSubmissionService.addNewDatasetRequest(projectUuid, linkToCreate, this.dependencies.project).pipe(
            // Reload dependencies
            switchMap(() => this.projectConsultService.getNewDatasetsRequest(projectUuid)),
            tap((values: NewDatasetRequest[]) => {
                this.dependencies.newDatasetsRequest = values;
                this.isLoading = false;
                this.updateAddButtonStatus(false);
            })
        ).subscribe({
            error: err => {
                console.error(err);
                this.isLoading = false;
                this.updateAddButtonStatus(false);
            }
        });
    }

    handleNewDatasetRequestUuidChanged(newDatasetRequestUuid: string): void {
        this.isLoading = true;
        this.projektMetierService.deleteNewDatasetRequest(this.dependencies.project.uuid, newDatasetRequestUuid).pipe(
            // Reload dependencies
            switchMap(() => this.projectConsultService.getNewDatasetsRequest(this.dependencies.project.uuid)),
            tap((values: NewDatasetRequest[]) => {
                this.dependencies.newDatasetsRequest = values;
                this.isLoading = false;
            })
        ).subscribe({
            error: err => {
                console.error(err);
                this.isLoading = false;
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('personalSpace.projectDatasets.delete.error'),
                    level: Level.ERROR
                });
            }
        });
    }
}
