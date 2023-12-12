import {Component, OnInit} from '@angular/core';
import {DateTimeUtils} from '@shared/utils/date-time-utils';
import {WorkflowFieldComponent} from '@shared/workflow-field/workflow-field.component';

@Component({
    selector: 'app-workflow-field-date',
    templateUrl: './workflow-field-date.component.html',
    styleUrls: ['./workflow-field-date.component.scss']
})
export class WorkflowFieldDateComponent extends WorkflowFieldComponent implements OnInit {
    ngOnInit(): void {
        this.formControl.patchValue(DateTimeUtils.formatStringDate(this.formControl.value));
    }
}
