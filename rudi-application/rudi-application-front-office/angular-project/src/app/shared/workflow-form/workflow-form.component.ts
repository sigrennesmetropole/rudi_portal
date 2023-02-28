import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {Action, Field, Form, Section, Task, Validator as WorkflowValidator} from '../../api-bpmn';
import {computeFormControlName} from '../workflow-field/workflow-field.component';
import {Level} from '../notification-template/notification-template.component';
import {SnackBarService} from '../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ObjectUtils} from '../utils/object-utils';
import {WorkflowProperties} from './workflow-properties';
import {getSectionWithFields} from '../utils/workflow-form-utils';
import {LogService} from '../../core/services/log.service';

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

    @Input()
    draftForm: Form;

    @Input()
    properties: WorkflowProperties;

    formGroup: FormGroup;

    @Input()
    worflowFormReadOnly: boolean;

    @Output()
    submitted: EventEmitter<void> = new EventEmitter<void>();

    constructor(
        private readonly formBuilder: FormBuilder,
        private snackBarService: SnackBarService,
        private translateService: TranslateService,
        private logService: LogService,
    ) {
        this.formGroup = this.createEmptyFormGroup();
    }

    get workflowForm(): Form | undefined {
        return getWorkflowForm(this.action, this.task, this.draftForm);
    }

    get invalid(): boolean {
        return this.formGroup.invalid;
    }

    get validSections(): Section[] {
        return getSectionWithFields(this.workflowForm);
    }

    ngOnInit(): void {
        this.formGroup = this.createFormGroup();

        if ((this.task || this.action) && this.draftForm) {
            throw new Error(`The form has a task or an action, so it cannnot be a draft form`);
        }

        if (this.task) {
            const taskContainsAction = this.task.actions.some(action => action === this.action);
            if (!taskContainsAction) {
                throw new Error(`Task ${this.task.id} must contain action ${this.action.name}`);
            }
        }
    }

    /**
     * Est-ce que la section du formulaire doit être matérialisée ?
     * @param section la section testée
     */
    isSectionDisplayed(section: Section): boolean {
        return !(section == null || section.label == null || section.label === '');
    }

    /**
     * Applique les valeurs saisies dans les champs respectifs de la tâche d'origine.
     * La tâche d'origine est donc modifiée.
     * @return true si le formulaire a été soumis
     */
    submit(): boolean {
        if (!this.workflowForm) {
            this.logService.error('Aucun formulaire à soumettre.');
            return false;
        }
        this.markAllAsTouched();
        if (this.invalid) {
            this.logService.debug('Formulaire invalide', this.formGroup);
            this.translateService.get('common.errorMissingFieldAccount').subscribe(message => {
                this.snackBarService.openSnackBar({
                    level: Level.ERROR,
                    message,
                }, 2000);
            });
            return false;
        }

        this.validSections.forEach(section => {
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
        this.logService.debug('Formulaire de workflow soumis', this.workflowForm);
        return true;
    }

    public isSectionOnlyHelp(section: Section): boolean {
        return section != null && section.help != null && section.help !== '' && (section.label == null || section.label === '');
    }

    private createEmptyFormGroup(): FormGroup {
        return this.formBuilder.group({});
    }

    private createFormGroup(): FormGroup {
        // tslint:disable-next-line:no-any : Angular typing
        const controlsConfig: { [key: string]: any } = {};

        if (this.workflowForm) {
            this.validSections.forEach(section => {
                section.fields.forEach(field => {
                    const value = field.values ? field.values[0] : ''; // Pour le moment on ne gère pas les champs multiples
                    const validators = getValidatorsFor(field);
                    controlsConfig[computeFormControlName(section, field)] = [value, ...validators];
                });
            });
        }

        return this.formBuilder.group(controlsConfig);
    }

    private markAllAsTouched(): void {
        this.formGroup.markAllAsTouched();
    }
}

export function getWorkflowForm(action: Action, task: Task, draftForm: Form): Form | undefined {
    return draftForm || action.form || task.asset.form;
}

const REQUIRED_WORKFLOW_VALIDATOR: WorkflowValidator = {
    type: 'REQUIRED'
};

function getValidatorsFor(field: Field): Validator[] {
    const validators = new Set<Validator>();

    if (field.definition.required) {
        validators.add(buildValidatorFromWorkflow(REQUIRED_WORKFLOW_VALIDATOR, field));
    }

    if (field.definition.validators) {
        field.definition.validators
            .map(workflowValidator => buildValidatorFromWorkflow(workflowValidator, field))
            .filter(ObjectUtils.nonNull)
            .forEach(validator => validators.add(validator));
    }

    return Array.from(validators.values());
}

function buildValidatorFromWorkflow(workflowValidator: WorkflowValidator, field: Field): Validator {
    if (workflowValidator.type === 'REQUIRED') {
        if (field.definition.type === 'BOOLEAN') {
            return Validators.requiredTrue;
        }
        return Validators.required;
    }
    console.warn(`Le validateur \"${workflowValidator.type}\" du workflow n'a pas d'équivalent côté Angular.`, workflowValidator);
    return null;
}

type Validator = (control: AbstractControl) => ValidationErrors | null;
