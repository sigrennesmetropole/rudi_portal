import {Component, Input, OnInit} from '@angular/core';
import { AppInfo } from 'src/app/acl/acl-api/model/models';
import {MediaSize} from '../../core/services/breakpoint-observer.service';
import {RedirectService} from '../../core/services/redirect.service';
import {FooterUtils} from '../utils/footer-utils';
import {MiscellaneousService} from '../../konsult/konsult-api';
import {PropertiesMetierService} from '../../core/services/properties-metier.service';
import {forkJoin} from 'rxjs';
import {LogService} from '../../core/services/log.service';

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
        }).subscribe(
            ({twitterLinkHref, linkedinLinkHref}) => {
                this.loading = false;
                this.twitterLinkHref = twitterLinkHref;
                this.linkedinLinkHref = linkedinLinkHref;
            },
            (error) => {
                this.loading = false;
                this.logService.error(error);
            });
    }

    goToTop(): void {
        this.redirectService.goToTop();
    }

    get currentYear(): number {
        return FooterUtils.getCurrentYear();
    }
}
