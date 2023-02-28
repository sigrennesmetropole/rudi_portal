import {Component} from '@angular/core';
import {WorkflowFieldComponent} from '../workflow-field/workflow-field.component';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
    selector: 'app-workflow-field-boolean',
    templateUrl: './workflow-field-boolean.component.html',
    styleUrls: ['./workflow-field-boolean.component.scss']
})
export class WorkflowFieldBooleanComponent extends WorkflowFieldComponent {

    constructor(private readonly sanitizer: DomSanitizer) {
        super();
    }

    /**
     * Permet d'interpreter les styles css décrits dans le label depuis le back
     */
    get safeLabel(): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(this.field.definition.label);
    }
}
