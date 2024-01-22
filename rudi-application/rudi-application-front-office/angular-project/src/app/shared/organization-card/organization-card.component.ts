import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {OrganizationBean} from '@app/strukture/api-strukture';

@Component({
    selector: 'app-organization-card',
    templateUrl: './organization-card.component.html',
    styleUrls: ['./organization-card.component.scss']
})
export class OrganizationCardComponent {
    @Input() organizationBean: OrganizationBean;
    @Input() datasetCountLoading: boolean;
    @Input() projectCountLoading$: boolean;

    constructor(private readonly router: Router) {
    }

    onClickOrganization(uuid: string): Promise<boolean> {
        return this.router.navigate(['/organization/detail/' + uuid]);
    }

    get datasetsCountTranslationKey(): string {
        return `organization.card.datasetsCount.${this.organizationBean.datasetCount > 1 ? 'plural' : 'single'}`;
    }

    get projectsCountTranslationKey(): string {
        return `organization.card.projectsCount.${this.organizationBean.projectCount > 1 ? 'plural' : 'single'}`;
    }

}
