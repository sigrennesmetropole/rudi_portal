import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {map, tap} from 'rxjs/operators';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {
    LinkedDatasetDependencies,
    LinkedDatasetTask,
    LinkedDatasetTaskDependencyFetchers,
    LinkedDatasetTaskService,
    ProjektTaskSearchCriteria
} from '../../../core/services/linked-dataset-task.service';
import * as moment from 'moment';
import {Moment} from 'moment';
import {injectDependencies} from '../../../shared/utils/dependencies-utils';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TaskDetailComponent} from '../../../shared/task-detail/task-detail.component';
import {LinkedDataset} from '../../../projekt/projekt-model';

/**
 * les dépendances de la page globale : détail de la demande
 */
export interface RequestDetailDependencies {
    ownerName: string;
    ownerEmail: string;
    receivedDate: Moment;
    datasetTitle: string;
}

@Component({
    selector: 'app-request-detail',
    templateUrl: './request-detail.component.html',
    styleUrls: ['./request-detail.component.scss']
})
export class RequestDetailComponent
    extends TaskDetailComponent<LinkedDataset, LinkedDatasetDependencies, LinkedDatasetTask, ProjektTaskSearchCriteria>
    implements OnInit {

    headingLoading: boolean;
    dependencies: RequestDetailDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                taskWithDependenciesService: LinkedDatasetTaskService,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
                iconRegistry: MatIconRegistry,
                sanitizer: DomSanitizer,
                dialog: MatDialog,
                translateService: TranslateService,
                snackBarService: SnackBarService,
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService);
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
            this.taskWithDependenciesService.getTask(idTask).pipe(
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
        return this.router.navigate(['/personal-space/received-access-requests']);
    }

}
