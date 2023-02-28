import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Field, Section} from '../../api-bpmn';
import {AbstractControl, FormGroup} from '@angular/forms';
import {WorkflowProperties} from '../workflow-form/workflow-properties';

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
     * On est obligé d'avoir un élément parent de <mat-form-field> avec [formGroup]="formGroup" dans le template
     */
    @Input()
    formGroup: FormGroup;

    /**
     * Section contenant le champ <i>field</i>
     */
    @Input()
    section: Section;

    @Input()
    field: Field;

    @Input()
    properties: WorkflowProperties;

    @Input()
    worflowFormReadOnly: boolean;

    @Output()
    submit: EventEmitter<void> = new EventEmitter<void>();

    get formControlName(): string {
        return computeFormControlName(this.section, this.field);
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
        return this.field.definition.readOnly || this.worflowFormReadOnly;
    }

    get formControl(): AbstractControl {
        return this.formGroup.get(this.formControlName);
    }


    addOtherControls(): void {
        // Par défaut on ne rajoute, les components fils en rajoutent si besoin
    }
}

export function computeFormControlName(section: Section, field: Field): string {
    return section.name + '_' + field.definition.name;
}
