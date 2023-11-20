import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-success-step3-template',
    templateUrl: './success-step3-template.component.html',
    styleUrls: ['./success-step3-template.component.scss']
})
export class SuccessStep3TemplateComponent {

    @Input()
    stepTitle: string;
    @Input()
    stepSubtitle: string;
    @Input()
    stepDescription: string;
    @Input()
    stepDescription2: string;
    @Input()
    buttonTitle: string;
    @Input()
    routerLink: string;
}
