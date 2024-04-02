import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {RequestDetailDependencies} from '@features/personal-space/pages/request-detail-dependencies';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {LogService} from '@core/services/log.service';
import {ProcessDefinitionsKeyIconRegistryService} from '@core/services/process-definitions-key-icon-registry.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependenciesService
} from '@core/services/tasks/projekt/linked-dataset-task-dependencies.service';
import {NewDatasetRequestTaskMetierService} from '@core/services/tasks/projekt/new-dataset-request-task-metier.service';
import {ProjektTaskSearchCriteria} from '@core/services/tasks/projekt/projekt-task-search-criteria.interface';
import {TranslateService} from '@ngx-translate/core';
import {ALL_TYPES, PROCESS_DEFINITION_KEY_TYPES} from '@shared/models/title-icon-type';
import {TaskDetailComponent} from '@shared/task-detail/task-detail.component';
import {LinkedDataset} from 'micro_service_modules/projekt/projekt-model';
import {map, tap} from 'rxjs/operators';

@Component({
    selector: 'app-new-request-task-detail',
    templateUrl: './new-request-task-detail.component.html',
    styleUrls: ['./new-request-task-detail.component.scss']
})
export class NewRequestTaskDetailComponent
    extends TaskDetailComponent<LinkedDataset, LinkedDatasetDependencies, LinkedDatasetTask, ProjektTaskSearchCriteria>
    implements OnInit {
    headingLoading: boolean;
    dependencies: RequestDetailDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                protected logger: LogService,
                private readonly IconRegistryService: IconRegistryService,
                private readonly processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService,
                readonly dialog: MatDialog,
                readonly translateService: TranslateService,
                readonly snackBarService: SnackBarService,
                readonly taskWithDependenciesService: LinkedDatasetTaskDependenciesService,
                readonly newDatasetRequestTaskMetierService: NewDatasetRequestTaskMetierService,
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService, newDatasetRequestTaskMetierService, logger);
        IconRegistryService.addAllSvgIcons(ALL_TYPES);
        processDefinitionsKeyIconRegistryService.addAllSvgIcons(PROCESS_DEFINITION_KEY_TYPES);
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            this.headingLoading = true;
            this.taskWithDependenciesService.getTaskWithDependencies(idTask).pipe(
                tap(taskWithDependencies => this.taskWithDependencies = taskWithDependencies),
                map(({task, asset, dependencies}) => {
                    return {
                        taskStatus: asset.functional_status,
                        datasetTitle: asset.title
                    };
                })
            )
                .subscribe({
                    next: (dependencies: RequestDetailDependencies) => {
                        this.headingLoading = false;
                        this.dependencies = dependencies;
                    },
                    error: (error) => {
                        this.headingLoading = false;
                        console.error(error);
                    }
                });
        }
    }
    protected goBackToList(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-notifications']);
    }
}
