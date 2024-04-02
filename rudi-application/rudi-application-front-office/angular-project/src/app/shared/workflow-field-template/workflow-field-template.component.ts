import {Component, ComponentFactoryResolver, EventEmitter, Input, OnInit, Output, Type, ViewContainerRef} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {WorkflowFieldDateComponent} from '@shared/workflow-field-date/workflow-field-date.component';
import {Field, Section} from 'micro_service_modules/api-bpmn';
import {Observable, of} from 'rxjs';
import {WorkflowFieldAddressComponent} from '../workflow-field-address/workflow-field-address.component';
import {WorkflowFieldAttachmentComponent} from '../workflow-field-attachment/workflow-field-attachment.component';
import {WorkflowFieldBooleanComponent} from '../workflow-field-boolean/workflow-field-boolean.component';
import {WorkflowFieldTextComponent} from '../workflow-field-text/workflow-field-text.component';
import {WorkflowFieldComponent} from '../workflow-field/workflow-field.component';
import {WorkflowProperties} from '../workflow-form/workflow-properties';

@Component({
    selector: 'app-workflow-field-template',
    template: ''
})
export class WorkflowFieldTemplateComponent implements OnInit {

    /**
     * On est obligé d'avoir un élément parent de <mat-form-field> avec [formGroup]="formGroup" dans le template de chaque component
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

    @Output()
    submit: EventEmitter<void> = new EventEmitter<void>();

    constructor(
        public viewContainerRef: ViewContainerRef,
        private componentFactoryResolver: ComponentFactoryResolver
    ) {
    }

    ngOnInit(): void {
        this.loadWorkflowFieldComponent();
    }

    loadWorkflowFieldComponent(): void {
        const viewContainerRef = this.viewContainerRef;
        viewContainerRef.clear();

        this.getWorkflowFieldComponentType().subscribe(componentType => {
            const componentFactory = this.componentFactoryResolver.resolveComponentFactory(componentType);
            const componentRef = viewContainerRef.createComponent<WorkflowFieldComponent>(componentFactory);
            Object.assign(componentRef.instance, {
                formGroup: this.formGroup,
                formControlNamePrefix: this.section.name,
                field: this.field,
                properties: this.properties,
                submit: this.submit,
            });
            componentRef.instance.addOtherControls();
        });
    }

    /**
     * Méthode pour retrouver le composant Angular associé à un type de champ du workflow.
     *
     * <p>
     * Attention on ne peut pas faire d'import dynamique à partir d'un string.
     * C'est pourquoi on doit importer tous les modules préalablement.
     * </p>
     *
     * <p>
     * Par exemple, ce code fonctionne :
     * </p>
     *
     * <pre>
     *     import('../workflow-field/workflow-field.component')
     * </pre>
     *
     * <p>
     * Mais pas celui-ci :
     * </p>
     *
     * <pre>
     *     const file = '../workflow-field/workflow-field.component';
     *     import(file)
     * </pre>
     *
     * <p>L'erreur suivante est lancée:</p>
     *
     * <pre>
     * Critical dependency: the request of a dependency is an expression
     * </pre>
     */
    private getWorkflowFieldComponentType(): Observable<Type<WorkflowFieldComponent>> {
        return of(this.getTypeFromAlreadyImportedModules());
    }

    private getTypeFromAlreadyImportedModules(): Type<WorkflowFieldComponent> {
        const type = this.field.definition.type;
        switch (type) {
            case 'STRING':
                return WorkflowFieldComponent;
            case 'TEXT':
                return WorkflowFieldTextComponent;
            case 'BOOLEAN':
                return WorkflowFieldBooleanComponent;
            case 'ADDRESS':
                return WorkflowFieldAddressComponent;
            case 'ATTACHMENT':
                return WorkflowFieldAttachmentComponent;
            case 'DATE':
                return WorkflowFieldDateComponent;
            default:
                console.warn(`WorkFlow FieldType "${type}" not handled. Using default component : WorkflowFieldComponent`);
                return WorkflowFieldComponent;
        }
    }
}
