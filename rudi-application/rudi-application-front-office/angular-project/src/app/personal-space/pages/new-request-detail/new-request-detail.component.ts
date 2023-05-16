import {Component, OnInit} from '@angular/core';
import {TaskDetailComponent} from '../../../shared/task-detail/task-detail.component';
import {LinkedDataset} from '../../../projekt/projekt-model';
import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependenciesService
} from '../../../core/services/tasks/projekt/linked-dataset-task-dependencies.service';
import {ProjektTaskSearchCriteria} from '../../../core/services/tasks/projekt/projekt-task-search-criteria.interface';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {map, tap} from 'rxjs/operators';
import {ALL_TYPES, PROCESS_DEFINITION_KEY_TYPES} from '../../../shared/models/title-icon-type';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {ProcessDefinitionsKeyIconRegistryService} from '../../../core/services/process-definitions-key-icon-registry.service';
import {NewDatasetRequestTaskMetierService} from '../../../core/services/tasks/projekt/new-dataset-request-task-metier.service';
import {RequestDetailDependencies} from '../request-detail-dependencies';

@Component({
    selector: 'app-new-request-detail',
    templateUrl: './new-request-detail.component.html',
    styleUrls: ['./new-request-detail.component.scss']
})
export class NewRequestDetailComponent extends TaskDetailComponent<LinkedDataset, LinkedDatasetDependencies, LinkedDatasetTask, ProjektTaskSearchCriteria>
    implements OnInit {
    headingLoading: boolean;
    dependencies:  RequestDetailDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                private readonly IconRegistryService: IconRegistryService,
                private readonly processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService,
                readonly dialog: MatDialog,
                readonly translateService: TranslateService,
                readonly snackBarService: SnackBarService,
                readonly taskWithDependenciesService: LinkedDatasetTaskDependenciesService,
                readonly newDatasetRequestTaskMetierService: NewDatasetRequestTaskMetierService,
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService, newDatasetRequestTaskMetierService);
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
                    next: (dependencies:RequestDetailDependencies) => {
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
