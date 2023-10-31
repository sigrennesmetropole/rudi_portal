import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-project-main-information-date',
    templateUrl: './project-main-information-date.component.html',
    styleUrls: ['./project-main-information-date.component.scss']
})
export class ProjectMainInformationDateComponent{
    @Input() startDate: string = null;
    @Input() endDate: string = null;
}
