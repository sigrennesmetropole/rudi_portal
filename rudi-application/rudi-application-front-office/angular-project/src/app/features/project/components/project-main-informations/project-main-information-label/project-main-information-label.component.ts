import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-project-main-information-label',
    templateUrl: './project-main-information-label.component.html',
    styleUrls: ['./project-main-information-label.component.scss']
})
export class ProjectMainInformationLabelComponent {
    @Input() label: string;
    @Input() value: string;
}
