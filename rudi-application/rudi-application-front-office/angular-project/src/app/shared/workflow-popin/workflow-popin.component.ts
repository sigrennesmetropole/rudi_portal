import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {getWorkflowForm, WorkflowFormComponent} from '../workflow-form/workflow-form.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CloseEvent, DialogClosedData} from '../../data-set/models/dialog-closed-data';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Action, Task} from '../../projekt/projekt-api';

@Component({
    selector: 'app-workflow-popin',
    templateUrl: './workflow-popin.component.html',
    styleUrls: ['./workflow-popin.component.scss']
})
export class WorkflowPopinComponent implements AfterViewInit {

    @ViewChild(WorkflowFormComponent)
    workflowFormComponent: WorkflowFormComponent;

    constructor(
        public dialogRef: MatDialogRef<WorkflowPopinComponent, DialogClosedData<WorkflowPopinOutputData>>,
        @Inject(MAT_DIALOG_DATA) public dialogData: WorkflowPopinInputData,
        matIconRegistry: MatIconRegistry,
        domSanitizer: DomSanitizer,
    ) {
        matIconRegistry.addSvgIcon(
            'icon-close',
            domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg')
        );
    }

    get action(): Action {
        return this.dialogData.action;
    }

    get task(): Task {
        return this.dialogData.task;
    }

    get hasRequiredFields(): boolean {
        const form = getWorkflowForm(this.action, this.task);
        if (!form) {
            return false;
        }
        return form.sections.some(section => section.fields.some(field => field.definition.required));
    }

    ngAfterViewInit(): void {
        this.workflowFormComponent.submitted.subscribe(() => this.close(CloseEvent.VALIDATION));
    }

    handleClose(): void {
        this.close();
    }

    close(closeEvent = CloseEvent.CANCEL): void {
        this.dialogRef.close({
            data: {
                task: this.task
            },
            closeEvent,
        });
    }

    handleCancel(): void {
        this.close();
    }

    handleSubmit(): void {
        // Pour éviter une ExpressionChangedAfterItHasBeenCheckedError on laisse le bouton submit toujours cliquable
        this.workflowFormComponent.submit();
    }

}

export interface WorkflowPopinInputData {
    action: Action;
    task: Task;
}

export interface WorkflowPopinOutputData {
    task: Task;
}
