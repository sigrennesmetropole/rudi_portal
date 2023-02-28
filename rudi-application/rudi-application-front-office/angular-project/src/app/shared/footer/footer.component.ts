import {Component, Input, OnInit} from '@angular/core';
import {MediaSize} from '../../core/services/breakpoint-observer.service';
import {AppInfo, MiscellaneousService} from '../../api-konsult';
import {RedirectService} from '../../core/services/redirect.service';
import {FooterUtils} from '../utils/footer-utils';

@Component({
    selector: 'app-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    @Input() mediaSize: MediaSize;
    appInfo: AppInfo;

    constructor(
        private readonly miscellaneousService: MiscellaneousService
        ,
        private readonly redirectService: RedirectService
    ) {
    }

    ngOnInit(): void {
        this.miscellaneousService.getApplicationInformation().subscribe(appInfo => this.appInfo = appInfo);
    }

    goToTop(): void {
        this.redirectService.goToTop();
    }

    get currentYear(): number {
        return FooterUtils.getCurrentYear();
    }
}
