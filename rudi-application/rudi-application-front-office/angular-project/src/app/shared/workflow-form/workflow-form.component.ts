import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {Action, Field, Form, Task} from '../../api-bpmn';
import {computeFormControlName} from '../workflow-field/workflow-field.component';
import {Level} from '../notification-template/notification-template.component';
import {SnackBarService} from '../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-workflow-form',
    templateUrl: './workflow-form.component.html',
    styleUrls: ['./workflow-form.component.scss']
})
export class WorkflowFormComponent implements OnInit {

    @Input()
    action: Action;

    @Input()
    task: Task;

    formGroup: FormGroup;

    @Output()
    submitted: EventEmitter<void> = new EventEmitter<void>();

    constructor(
        private readonly formBuilder: FormBuilder,
        private snackBarService: SnackBarService,
        private translateService: TranslateService,
    ) {
        this.formGroup = this.createEmptyFormGroup();
    }

    get workflowForm(): Form | undefined {
        return getWorkflowForm(this.action, this.task);
    }

    get invalid(): boolean {
        return this.formGroup.invalid;
    }

    ngOnInit(): void {
        this.formGroup = this.createFormGroup();

        const taskContainsAction = this.task.actions.some(action => action === this.action);
        if (!taskContainsAction) {
            throw new Error(`Task ${this.task.id} must contain action ${this.action.name}`);
        }
    }

    /**
     * Applique les valeurs saisies dans les champs respectifs de la tâche d'origine.
     * La tâche d'origine est donc modifiée.
     * @return true si le formulaire a été soumis
     */
    submit(): void {
        if (!this.workflowForm) {
            return;
        }
        if (this.invalid) {
            this.translateService.get('common.errorMissingFieldAccount').subscribe(message => {
                this.snackBarService.openSnackBar({
                    level: Level.ERROR,
                    message,
                }, 2000);
            });
            return;
        }
        this.workflowForm.sections.forEach(section => {
            section.fields.forEach(field => {
                const fieldKey = computeFormControlName(section, field);
                const control = this.formGroup.controls[fieldKey];
                if (!control) {
                    throw new Error(`Cannot find control for WorkFlow Field ${fieldKey} with name "${field.definition.name}" in section "${section.name}"`);
                }
                field.values = [control.value]; // Pour le moment on ne gère que les champs non multiples
            });
        });
        this.submitted.emit(void 0);
    }

    private createEmptyFormGroup(): FormGroup {
        return this.formBuilder.group({});
    }

    private createFormGroup(): FormGroup {
        // tslint:disable-next-line:no-any : Angular typing
        const controlsConfig: { [key: string]: any } = {};

        if (this.workflowForm) {
            this.workflowForm.sections.forEach(section => {
                section.fields.forEach(field => {
                    const value = field.values ? field.values[0] : ''; // Pour le moment on ne gère pas les champs multiples
                    const validators = getValidatorsFor(field);
                    controlsConfig[computeFormControlName(section, field)] = [value, ...validators];
                });
            });
        }

        return this.formBuilder.group(controlsConfig);
    }

}

export function getWorkflowForm(action: Action, task: Task): Form | undefined {
    return action.form || task.asset.form;
}

function getValidatorsFor(field: Field): Validator[] {
    const validators: Validator[] = [];

    if (field.definition.required) {
        validators.push(Validators.required);
    }

    return validators;
}

type Validator = (control: AbstractControl) => ValidationErrors | null;
