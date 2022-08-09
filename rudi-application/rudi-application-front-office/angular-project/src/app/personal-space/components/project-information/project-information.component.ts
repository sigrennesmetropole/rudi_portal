import {Component, Input, OnInit} from '@angular/core';
import {Project} from '../../../projekt/projekt-model';

@Component({
    selector: 'app-project-information',
    templateUrl: './project-information.component.html',
    styleUrls: ['./project-information.component.scss']
})
export class ProjectInformationComponent implements OnInit {
    @Input() project: Project;

    constructor() {
    }

    ngOnInit(): void {
    }

}
