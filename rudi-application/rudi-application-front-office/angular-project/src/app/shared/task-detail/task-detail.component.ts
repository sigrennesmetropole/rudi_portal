import {Action, Task} from '../../projekt/projekt-api';
import {DefaultMatDialogConfig} from '../../core/services/default-mat-dialog-config';
import {WorkflowPopinComponent, WorkflowPopinInputData, WorkflowPopinOutputData} from '../workflow-popin/workflow-popin.component';
import {CloseEvent, DialogClosedData} from '../../data-set/models/dialog-closed-data';
import {Level} from '../notification-template/notification-template.component';
import {TaskWithDependencies} from '../utils/task-utils';
import {AssetDescription} from '../../api-bpmn';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '../../core/services/snack-bar.service';
import {TaskWithDependenciesService} from '../../core/services/task-with-dependencies-service';

export abstract class TaskDetailComponent<A extends AssetDescription, D, T extends TaskWithDependencies<A, D>, C> {
    taskWithDependencies: T;

    protected constructor(
        protected readonly dialog: MatDialog,
        protected readonly translateService: TranslateService,
        protected readonly snackBarService: SnackBarService,
        protected readonly taskWithDependenciesService: TaskWithDependenciesService<T, C>,
    ) {
    }

    get task(): Task {
        return this.taskWithDependencies?.task;
    }

    openPopinForAction(action: Action): void {
        const dialogConfig = new DefaultMatDialogConfig<WorkflowPopinInputData>();
        dialogConfig.data = {
            action,
            task: this.taskWithDependencies.task
        };
        // tslint:disable-next-line:max-line-length
        const dialogRef = this.dialog.open<WorkflowPopinComponent, WorkflowPopinInputData, DialogClosedData<WorkflowPopinOutputData>>(WorkflowPopinComponent, dialogConfig);
        dialogRef.afterClosed().subscribe(data => {
            if (data.closeEvent === CloseEvent.VALIDATION) {
                this.doAction(action, data.data);
            }
        });
    }

    protected abstract goBackToList(): Promise<boolean>;

    private doAction(action: Action, data: WorkflowPopinOutputData): void {
        this.taskWithDependenciesService.doAction(action, data.task).subscribe({
            complete: () => {
                this.translateService.get('task.success').subscribe(message => {
                    this.snackBarService.openSnackBar({
                        level: Level.INFO,
                        message,
                        keepBeforeSecondRouteChange: true,
                    });
                });
                this.goBackToList();
            },
            error: (err) => {
                console.error(`Erreur lors du doAction ${action.name} sur la tâche`, data.task, err);
                this.translateService.get('task.error').subscribe(message => {
                    this.snackBarService.openSnackBar({
                        level: Level.ERROR,
                        message,
                    });
                });
            }
        });
    }

}
