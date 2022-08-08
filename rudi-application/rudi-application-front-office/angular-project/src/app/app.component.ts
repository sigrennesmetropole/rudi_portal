import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

import {BreakpointObserverService, MediaSize} from './core/services/breakpoint-observer.service';
import {RouteHistoryService} from './core/services/route-history.service';
import {NavigationEnd, Router} from '@angular/router';
import {filter, map} from 'rxjs/operators';
import {PageTitleService} from './core/services/page-title.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    mediaSize: MediaSize;

    constructor(
        private readonly routeHistoryService: RouteHistoryService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly translate: TranslateService,
        private readonly router: Router,
        private readonly pageTitleService: PageTitleService,
    ) {
        translate.setDefaultLang('fr');
        router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(event => event as NavigationEnd),
            map(event => event.url)
        ).subscribe(url => pageTitleService.setPageTitleFromUrl(url));
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

}
