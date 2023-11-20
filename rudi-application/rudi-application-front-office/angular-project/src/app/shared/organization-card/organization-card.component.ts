import {Component, Input} from '@angular/core';
import {OrganizationBean} from '@app/strukture/api-strukture';
import {Router} from '@angular/router';

@Component({
    selector: 'app-organization-card',
    templateUrl: './organization-card.component.html',
    styleUrls: ['./organization-card.component.scss']
})
export class OrganizationCardComponent {
    @Input() organizationBean: OrganizationBean;

    constructor(private readonly router: Router) {}

    onClickOrganization(uuid: string): Promise<boolean> {
        return this.router.navigate(['/organization/detail/' +uuid]);
    }
}
