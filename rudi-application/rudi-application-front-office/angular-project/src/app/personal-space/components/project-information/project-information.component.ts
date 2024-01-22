import {Component, Input} from '@angular/core';
import {Project} from '../../../projekt/projekt-model';

@Component({
    selector: 'app-project-information',
    templateUrl: './project-information.component.html'
})
export class ProjectInformationComponent {
    @Input() project: Project;
}
