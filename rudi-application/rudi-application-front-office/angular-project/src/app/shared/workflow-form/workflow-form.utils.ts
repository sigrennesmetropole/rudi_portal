import {Injectable} from '@angular/core';
import {Validators} from '@angular/forms';
import {Field, Section, Validator as WorkflowValidator} from 'micro_service_modules/api-bpmn';
import {LogService} from '@core/services/log.service';
import {ObjectUtils} from '@shared/utils/object-utils';
import {Validator} from '@shared/workflow-form/workflow-form.types';
import {Form} from 'micro_service_modules/projekt/projekt-api';

const REQUIRED_WORKFLOW_VALIDATOR: WorkflowValidator = {
    type: 'REQUIRED'
};

@Injectable({
    providedIn: 'root'
})
export class WorkflowFormUtils {
    constructor(
        private logger: LogService
    ) {
    }

    computeFormControlName(section: Section, field: Field): string {
        return section.name + '_' + field.definition.name;
    }

    isFormReadonly(form: Form): boolean {
        // form is readonly only if all section are readonly
        for (const section of form.sections) {
            if (!section.readOnly) {
                return false;
            }
        }

        return true;
    }

    getValidatorsFor(field: Field): Validator[] {
        const validators = new Set<Validator>();

        if (field.definition.required) {
            validators.add(this.buildValidatorFromWorkflow(REQUIRED_WORKFLOW_VALIDATOR, field));
        }

        if (field.definition.validators) {
            field.definition.validators
                .map(workflowValidator => this.buildValidatorFromWorkflow(workflowValidator, field))
                .filter(ObjectUtils.nonNull)
                .forEach(validator => validators.add(validator));
        }

        return Array.from(validators.values());
    }

    private buildValidatorFromWorkflow(workflowValidator: WorkflowValidator, field: Field): Validator {
        if (workflowValidator.type === 'REQUIRED') {
            if (field.definition.type === 'BOOLEAN') {
                return Validators.requiredTrue;
            }
            return Validators.required;
        }
        this.logger.warning(
            `Le validateur \"${workflowValidator.type}\" du workflow n'a pas d'équivalent côté Angular.`,
            workflowValidator
        );
        return null;
    }
}

