import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {map, tap} from 'rxjs/operators';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

import * as moment from 'moment';
import {injectDependencies} from '../../../shared/utils/dependencies-utils';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TaskDetailComponent} from '../../../shared/task-detail/task-detail.component';
import {LinkedDataset} from '../../../projekt/projekt-model';

import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependenciesService,
    LinkedDatasetTaskDependencyFetchers
} from '../../../core/services/tasks/projekt/linked-dataset-task-dependencies.service';
import {LinkedDatasetTaskMetierService} from '../../../core/services/tasks/projekt/linked-dataset-task-metier.service';
import {ProjektTaskSearchCriteria} from '../../../core/services/tasks/projekt/projekt-task-search-criteria.interface';
import {RequestDetailDependencies} from '../request-detail-dependencies';


@Component({
    selector: 'app-dataset-task-detail',
    templateUrl: './dataset-task-detail.component.html',
    styleUrls: ['./dataset-task-detail.component.scss']
})
export class DatasetTaskDetailComponent
    extends TaskDetailComponent<LinkedDataset, LinkedDatasetDependencies, LinkedDatasetTask, ProjektTaskSearchCriteria>
    implements OnInit {

    headingLoading: boolean;
    dependencies: RequestDetailDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
                private readonly iconRegistry: MatIconRegistry,
                private readonly sanitizer: DomSanitizer,
                readonly dialog: MatDialog,
                readonly translateService: TranslateService,
                readonly snackBarService: SnackBarService,
                readonly taskWithDependenciesService: LinkedDatasetTaskDependenciesService,
                readonly linkedDatasetTaskMetierService: LinkedDatasetTaskMetierService
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService, linkedDatasetTaskMetierService);
        iconRegistry.addSvgIcon(
            'request',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/key_icon_circle.svg'));
        iconRegistry.addSvgIcon(
            'project',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/projet.svg'));
        iconRegistry.addSvgIcon(
            'historical',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/historique.svg'));
        iconRegistry.addSvgIcon(
            'restricted-dataset',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/jdd_restreint.svg'));
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
                injectDependencies({
                    dataset: this.linkedDatasetDependencyFetchers.dataset,
                    project: this.linkedDatasetDependencyFetchers.project,
                }),
                injectDependencies({
                    ownerInfo: this.linkedDatasetDependencyFetchers.ownerInfo,
                }),
                map(({task, asset, dependencies}) => {
                    return {
                        ownerName: dependencies.ownerInfo.name,
                        ownerEmail: dependencies.project.contact_email,
                        receivedDate: moment(task.updatedDate),
                        datasetTitle: dependencies.dataset.resource_title
                    };
                })
            ).subscribe({
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
