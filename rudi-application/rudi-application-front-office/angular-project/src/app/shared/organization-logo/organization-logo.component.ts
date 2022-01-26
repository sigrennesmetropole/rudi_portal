import {Component, Input, OnInit} from '@angular/core';
import {Base64EncodedLogo, DEFAULT_LOGO, OrganizationMetierService} from '../../core/services/organization-metier.service';
import {ProvidersMetierService} from '../../core/services/providers-metier.service';
import {ProducersMetierService} from '../../core/services/producers-metier.service';

const PRODUCER = 'producer';
const PROVIDER = 'provider';
export type OrganizationType = 'producer' | 'provider';

@Component({
    selector: 'app-organization-logo',
    templateUrl: './organization-logo.component.html',
    styleUrls: ['./organization-logo.component.scss']
})
export class OrganizationLogoComponent implements OnInit {
    @Input() organizationType: OrganizationType;
    @Input() organizationId: string;
    @Input() cssClass: string;
    defaultContent = DEFAULT_LOGO;
    content: Base64EncodedLogo;

    constructor(
        private readonly producersMetierService: ProducersMetierService,
        private readonly providersMetierService: ProvidersMetierService,
    ) {
    }

    ngOnInit(): void {
        const service = this.getService();
        if (service && this.organizationId) {
            service.getLogo(this.organizationId).subscribe(logo => this.content = logo);
        }
    }

    private getService(): OrganizationMetierService | undefined {
        switch (this.organizationType) {
            case PRODUCER:
                return this.producersMetierService;
            case PROVIDER:
                return this.providersMetierService;
            default:
                return undefined;
        }
    }

    get name(): string {
        switch (this.organizationType) {
            case PRODUCER:
                return 'logo du producteur';
            case PROVIDER:
                return 'logo du fournisseur';
            default:
                return 'logo';
        }
    }

}
