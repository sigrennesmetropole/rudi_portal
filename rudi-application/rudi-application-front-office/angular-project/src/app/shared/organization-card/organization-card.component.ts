import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {OrganizationBean} from 'micro_service_modules/strukture/api-strukture';
import {URIComponentCodec} from '@core/services/codecs/uri-component-codec';

@Component({
    selector: 'app-organization-card',
    templateUrl: './organization-card.component.html',
    styleUrls: ['./organization-card.component.scss']
})
export class OrganizationCardComponent {
    @Input() organizationBean: OrganizationBean;
    @Input() datasetCountLoading: boolean;
    @Input() projectCountLoading$: boolean;

    constructor(private readonly router: Router, private readonly uriComponentCodec: URIComponentCodec) {
    }

    onClickOrganization(uuid: string, name: string): Promise<boolean> {
        return this.router.navigate(['/organization/detail/' + uuid + '/' + this.uriComponentCodec.normalizeString(name)]);
    }

    get datasetsCountTranslationKey(): string {
        return `organization.card.datasetsCount.${this.organizationBean.datasetCount > 1 ? 'plural' : 'single'}`;
    }

    get projectsCountTranslationKey(): string {
        return `organization.card.projectsCount.${this.organizationBean.projectCount > 1 ? 'plural' : 'single'}`;
    }

}
