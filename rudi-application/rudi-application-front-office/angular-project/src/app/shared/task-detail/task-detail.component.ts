import {MatDialog} from '@angular/material/dialog';
import {AssetDescription} from 'micro_service_modules/api-bpmn';
import {CloseEvent} from '@features/data-set/models/dialog-closed-data';
import {DefaultMatDialogConfig} from '@core/services/default-mat-dialog-config';
import {LogService} from '@core/services/log.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TaskMetierService} from '@core/services/tasks/task-metier.service';
import {TaskWithDependenciesService} from '@core/services/tasks/task-with-dependencies-service';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {TaskWithDependencies} from '@shared/utils/task-utils';
import {WorkflowFormDialogInputData} from '@shared/workflow-form-dialog/types';
import {WorkflowFormDialogComponent} from '@shared/workflow-form-dialog/workflow-form-dialog.component';
import {Action, Task} from 'micro_service_modules/projekt/projekt-api';

export abstract class TaskDetailComponent<A extends AssetDescription, D, T extends TaskWithDependencies<A, D>, C> {
    taskWithDependencies: T;

    protected constructor(
        protected readonly dialog: MatDialog,
        protected readonly translateService: TranslateService,
        protected readonly snackBarService: SnackBarService,
        protected readonly taskWithDependenciesService: TaskWithDependenciesService<T, C, A>,
        protected readonly taskMetierService: TaskMetierService<A>,
        protected readonly logger: LogService,
    ) {
    }

    get task(): Task {
        return this.taskWithDependencies?.task;
    }

    get actions(): Action[] {
        return this.sortAction().filter(action => !!action.label);
    }

    private sortAction(): Action[] {
        const actions: Action[] = this.task?.actions ?? [];

        return actions.sort(function compare(a: Action, b: Action): number {
            // pour afficher le bouton valider avant le bouton rejeter
            if (a.label < b.label) {
                return -1;
            }
            if (a.label > b.label) {
                return 1;
            }
            return 0;
        });
    }

    openPopinForAction(action: Action, disable = false, updatedTask?: Task): void {
        if (disable) {
            return;
        }
        const dialogConfig: DefaultMatDialogConfig<WorkflowFormDialogInputData> = new DefaultMatDialogConfig();

        if (this.task && !this.task.actions.some((current: Action): boolean => current === action)) {
            throw new Error(`Task ${this.task.id} must contain action ${action.name}`);
        }

        dialogConfig.data = {
            title: action.label,
            form: action.form || this.task.asset.form,
        };

        this.dialog
            .open(WorkflowFormDialogComponent, dialogConfig)
            .afterClosed()
            .subscribe(data => {
                if (data.closeEvent === CloseEvent.VALIDATION) {
                    this.doAction(action, updatedTask);
                }
            });
    }

    protected abstract goBackToList(): Promise<boolean>;

    private doAction(action: Action, updatedTask?: Task): void {
        this.taskMetierService.doAction(action, updatedTask ?? this.taskWithDependencies.task).subscribe({
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
                this.logger.error(`Erreur lors du doAction ${action.name} sur la tâche`, this.taskWithDependencies.task, err);
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
