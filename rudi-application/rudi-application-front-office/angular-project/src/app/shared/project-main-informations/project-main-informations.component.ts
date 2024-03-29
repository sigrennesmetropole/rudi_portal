import {Component, Input} from '@angular/core';
import {Project} from '@app/projekt/projekt-api';

@Component({
    selector: 'app-project-main-informations',
    templateUrl: './project-main-informations.component.html',
    styleUrls: ['./project-main-informations.component.scss']
})
export class ProjectMainInformationsComponent{
    @Input() project: Project;
}
