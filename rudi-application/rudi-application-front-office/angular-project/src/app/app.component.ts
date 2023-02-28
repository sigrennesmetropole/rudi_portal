import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../environments/environment';

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

    mediaSize: MediaSize;

    /**
     * Méthode de chargement de scripts supplémentaires
     * @private
     */
    private static loadScript(): void {
        // Chargement du script de tracking avec tarteaucitron si URL définie
        if (environment.tarteaucitronUrl) {
            const node = document.createElement('script');
            node.src = environment.tarteaucitronUrl;
            node.type = 'text/javascript';
            node.async = true;
            document.getElementsByTagName('head')[0].appendChild(node);
        }
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        AppComponent.loadScript();
    }
}
