import {Component, Input} from '@angular/core';
import {Organization} from '../../../strukture/strukture-model';

@Component({
    selector: 'app-organization-card',
    templateUrl: './organization-card.component.html',
    styleUrls: ['./organization-card.component.scss']
})
export class OrganizationCardComponent {
    @Input() myOrganization: Organization;
}
