import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup} from '@angular/forms';
import {Form, Section} from 'micro_service_modules/api-bpmn';
import {LogService} from '@core/services/log.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {getSectionWithFields} from '@shared/utils/workflow-form-utils';
import {WorkflowFormUtils} from '@shared/workflow-form/workflow-form.utils';
import {WorkflowProperties} from '@shared/workflow-form/workflow-properties';

@Component({
    selector: 'app-workflow-form',
    templateUrl: './workflow-form.component.html',
    styleUrls: ['./workflow-form.component.scss']
})
export class WorkflowFormComponent implements OnInit {
    formGroup: FormGroup;

    @Input()
    workflowForm?: Form;
    @Input()
    properties: WorkflowProperties;

    @Output()
    submitEvent: EventEmitter<void>;

    constructor(
        private readonly formBuilder: FormBuilder,
        private snackBarService: SnackBarService,
        private translateService: TranslateService,
        private logService: LogService,
        private workflowFormUtils: WorkflowFormUtils
    ) {
        this.formGroup = formBuilder.group({});
        this.submitEvent = new EventEmitter();
    }

    /**
     * Getters
     */
    get invalid(): boolean {
        return this.formGroup.invalid;
    }

    get validSections(): Section[] {
        return getSectionWithFields(this.workflowForm);
    }

    get isReadonly(): boolean {
        return this.workflowFormUtils.isFormReadonly(this.workflowForm);
    }

    /**
     * Lifecycles
     */
    ngOnInit(): void {
        this.formGroup = this.createFormGroup();
    }

    /**
     * Methods
     */

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
                const fieldKey: string = this.workflowFormUtils.computeFormControlName(section, field);
                const control: AbstractControl | undefined = this.formGroup.controls[fieldKey];
                if (!control) {
                    throw new Error(`Cannot find control for WorkFlow Field ${fieldKey} with name "${field.definition.name}" in section "${section.name}"`);
                }
                field.values = [control.value]; // Pour le moment on ne gère que les champs non multiples
            });
        });
        this.submitEvent.emit();
        this.logService.debug('Formulaire de workflow soumis', this.workflowForm);
        return true;
    }

    /**
     * Internal methods
     */

    private markAllAsTouched(): void {
        this.formGroup.markAllAsTouched();
    }

    private createFormGroup(): FormGroup {
        // tslint:disable-next-line:no-any : Angular typing
        const controlsConfig: { [key: string]: any } = {};

        if (this.workflowForm) {
            this.validSections.forEach(section => {
                section.fields.forEach(field => {
                    const value = field.values ? field.values[0] : ''; // Pour le moment on ne gère pas les champs multiples
                    const validators = this.workflowFormUtils.getValidatorsFor(field);
                    controlsConfig[this.workflowFormUtils.computeFormControlName(section, field)] = [value, ...validators];
                });
            });
        }

        return this.formBuilder.group(controlsConfig);
    }
}
