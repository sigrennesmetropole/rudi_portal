import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CloseEvent, DialogClosedData} from '@features/data-set/models/dialog-closed-data';
import {getSectionWithFields} from '@shared/utils/workflow-form-utils';
import {WorkflowFormDialogInputData, WorkflowFormDialogOutputData} from '@shared/workflow-form-dialog/types';
import {WorkflowFormComponent} from '@shared/workflow-form/workflow-form.component';
import {Field, Section} from 'micro_service_modules/projekt/projekt-api';

@Component({
    selector: 'app-workflow-form-dialog',
    templateUrl: './workflow-form-dialog.component.html',
    styleUrls: ['./workflow-form-dialog.component.scss']
})
export class WorkflowFormDialogComponent {
    @ViewChild('workflowForm', {static: true})
    workflowFormComponent: WorkflowFormComponent;

    constructor(
        @Inject(MAT_DIALOG_DATA) public dialogData: WorkflowFormDialogInputData,
        public dialogRef: MatDialogRef<WorkflowFormDialogComponent, DialogClosedData<WorkflowFormDialogOutputData>>,
    ) {
    }

    get hasRequiredFields(): boolean {
        if (!this.dialogData.form || this.dialogData.form.sections == null || this.dialogData.form.sections.length === 0) {
            return false;
        }

        return getSectionWithFields(this.dialogData.form).some(
            (section: Section) => section.fields.some((field: Field) => field.definition.required)
        );
    }

    onClickClose(): void {
        this.closeDialog(CloseEvent.CANCEL);
    }

    onClickConfirm(): void {
        this.workflowFormComponent.submit();
    }

    handleFormSubmit(): void {
        this.closeDialog(CloseEvent.VALIDATION);
    }

    private closeDialog(closeEvent: CloseEvent): void {
        this.dialogRef.close({
            data: {
                form: this.dialogData.form
            },
            closeEvent,
        });
    }
}


