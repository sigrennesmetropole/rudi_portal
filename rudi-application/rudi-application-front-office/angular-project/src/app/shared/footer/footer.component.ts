import {Component, Input, OnInit} from '@angular/core';
import {MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {RedirectService} from '@core/services/redirect.service';
import {AppInfo} from 'micro_service_modules/acl/acl-api/model/models';
import {MiscellaneousService} from 'micro_service_modules/konsult/konsult-api';
import {forkJoin} from 'rxjs';
import {FooterUtils} from '../utils/footer-utils';

@Component({
    selector: 'app-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    @Input() mediaSize: MediaSize;
    appInfo: AppInfo;
    loading = false;
    twitterLinkHref: string;
    linkedinLinkHref: string;

    constructor(
        private readonly miscellaneousService: MiscellaneousService,
        private readonly redirectService: RedirectService,
        private readonly propertiesService: PropertiesMetierService,
        private readonly logService: LogService
    ) {
    }

    ngOnInit(): void {
        this.miscellaneousService.getApplicationInformation().subscribe(appInfo => this.appInfo = appInfo);
        this.loading = true;
        forkJoin({
            twitterLinkHref: this.propertiesService.get('rudidatarennes.twitter'),
            linkedinLinkHref: this.propertiesService.get('rudidatarennes.linkedin'),
        }).subscribe({
            next: ({twitterLinkHref, linkedinLinkHref}) => {
                this.loading = false;
                this.twitterLinkHref = twitterLinkHref;
                this.linkedinLinkHref = linkedinLinkHref;
            },
            error: (error) => {
                this.loading = false;
                this.logService.error(error);
            }
        });
    }

    goToTop(): void {
        this.redirectService.goToTop();
    }

    get currentYear(): number {
        return FooterUtils.getCurrentYear();
    }
}
