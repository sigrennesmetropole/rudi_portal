import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AbstractControl, FormGroup} from '@angular/forms';
import {Field} from 'micro_service_modules/api-bpmn';
import {WorkflowProperties} from '@shared/workflow-form/workflow-properties';

/**
 * Champ généré dynamiquement à partir d'un {@link Field} du WorkFlow.
 * On utilise ce composant par défaut, lorsqu'aucun composant spécifique n'a été trouvé pour le type du champ.
 */
@Component({
    selector: 'app-workflow-field',
    templateUrl: './workflow-field.component.html',
    styleUrls: ['./workflow-field.component.scss']
})
export class WorkflowFieldComponent {
    /**
     * Props
     */
    @Input()
    formGroup: FormGroup;

    @Input()
    formControlNamePrefix: string;

    @Input()
    field: Field;

    @Input()
    properties: WorkflowProperties;

    @Output()
    submit: EventEmitter<void> = new EventEmitter<void>();

    /**
     * Getters
     */
    get formControlName(): string {
        return `${this.formControlNamePrefix}_${this.field.definition.name}`;
    }

    get label(): string {
        return this.field.definition.label;
    }

    get help(): string {
        return this.field.definition.help;
    }

    get required(): boolean {
        return this.field.definition.required;
    }

    get readonly(): boolean {
        return this.field.definition.readOnly;
    }

    get formControl(): AbstractControl {
        return this.formGroup.get(this.formControlName);
    }

    /**
     * Methods
     */
    addOtherControls(): void {
        // Par défaut on ne rajoute, les components fils en rajoutent si besoin
    }
}
