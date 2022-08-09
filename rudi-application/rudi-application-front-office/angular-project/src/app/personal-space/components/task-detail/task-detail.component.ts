import {Component, OnInit} from '@angular/core';
import {map} from 'rxjs/operators';
import {RequestToPrint} from '../linked-dataset-tasks/linked-dataset-tasks.component';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {DatePipe} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {BreakpointObserverService} from '../../../core/services/breakpoint-observer.service';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {LinkedDatasetTaskDependencyFetchers, LinkedDatasetTaskService} from '../../../core/services/linked-dataset-task.service';
import {injectDependencies} from '../../../shared/utils/dependencies-utils';

@Component({
    selector: 'app-task-detail',
    templateUrl: './task-detail.component.html',
    styleUrls: ['./task-detail.component.scss']
})
export class TaskDetailComponent implements OnInit {
    loading: boolean;
    requestToPrint: RequestToPrint;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                private readonly breakpointObserverService: BreakpointObserverService,
                private readonly iconRegistry: MatIconRegistry,
                private readonly sanitizer: DomSanitizer,
                private snackBarService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly linkedDatasetTaskService: LinkedDatasetTaskService,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers) {
    }

    // Recup de l'id de la task depuis la route
    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    // Au changement de la taskId, on récupère la task correspondante et ses dépendances
    set taskId(idTask: string) {
        if (idTask) {
            this.loadTask(idTask);
        }
    }

    private loadTask(idTask: string): void {
        this.loading = true;
        this.linkedDatasetTaskService.getTask(idTask).pipe(
            injectDependencies({
                dataset: this.linkedDatasetDependencyFetchers.dataset,
                project: this.linkedDatasetDependencyFetchers.project,
            }),
            injectDependencies({
                ownerInfo: this.linkedDatasetDependencyFetchers.ownerInfo,
            }),
            map(({task, asset, dependencies}) => {
                return ({
                    taskId: task.id,
                    receivedDate: new Date(task.updatedDate),
                    datasetTitle: dependencies.dataset.resource_title,
                    ownerName: dependencies.ownerInfo.name,
                    status: task.functionalStatus,
                    endDate: asset.end_date ? new Date(asset.end_date) : undefined,
                    comment: asset.comment,
                    ownerEmail: dependencies.project.contact_email
                } as RequestToPrint);
            }),
        ).subscribe({
            next: (requestToPrint) => {
                this.requestToPrint = requestToPrint;
                this.loading = false;
            },
            error: (e) => {
                console.error(`Not authorized to access this task : ${idTask}`, e);
                this.printErrorMessage('error.technicalError');
                this.loading = false;
            }
        });
    }

    // snackbar
    private printErrorMessage(message: string): void {
        this.snackBarService.openSnackBar({
            message: this.translateService.instant(message),
            level: Level.ERROR
        });
    }

    // format date into MM/YYYy
    public formatEndDate(dateToFormat: Date): string {
        const datePipe = new DatePipe('en-US');
        return datePipe.transform(dateToFormat, 'MM/YYYY');
    }
}
