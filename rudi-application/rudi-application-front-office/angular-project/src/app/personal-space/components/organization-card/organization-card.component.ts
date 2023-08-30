import {Component, Input} from '@angular/core';
import {Organization} from '../../../strukture/strukture-model';
import {ProjectCatalogItem} from '../../../project/model/project-catalog-item';
import {FiltersService} from '../../../core/services/filters.service';
import {BreakpointObserverService} from '../../../core/services/breakpoint-observer.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-organization-card',
    templateUrl: './organization-card.component.html',
    styleUrls: ['./organization-card.component.scss']
})
export class OrganizationCardComponent {
    @Input() myOrganization: Organization;

    constructor(private readonly router: Router) {}

    onClickOrganization(organization: Organization): Promise<boolean> {
        return this.router.navigate(['/organization/detail/' +organization.uuid]);

    }
}
