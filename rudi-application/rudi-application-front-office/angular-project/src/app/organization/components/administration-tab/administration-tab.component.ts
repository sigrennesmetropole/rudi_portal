import {Component, Input, OnInit} from '@angular/core';
import {Organization} from '../../../strukture/strukture-model';
import {OrganizationMetierService} from '../../../core/services/organization/organization-metier.service';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {ActivatedRoute} from '@angular/router';

@Component({
    selector: 'app-administration-tab',
    templateUrl: './administration-tab.component.html',
    styleUrls: ['./administration-tab.component.scss']
})
export class AdministrationTabComponent implements OnInit {

    @Input() isLoading: boolean;
    @Input() organization: Organization;

    /**
     * Indique si le captcha doit s'activer sur cette page
     */
    enableCaptchaOnPage = true;

    constructor(private readonly organizationService: OrganizationMetierService,
                private readonly iconRegistryService: IconRegistryService,
                private readonly route: ActivatedRoute) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
        if (this.route.snapshot.data?.aclAppInfo) { // Si on a pu recuperer l'info d'activation du captcha sinon il reste à false par défaut
            this.enableCaptchaOnPage = this.route.snapshot.data.aclAppInfo.captchaEnabled;
        } else {
            this.enableCaptchaOnPage = false;
        }
    }
}
