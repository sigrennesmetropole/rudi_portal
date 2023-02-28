import {Component, Input, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../core/services/breakpoint-observer.service';

@Component({
    selector: 'app-page-title',
    templateUrl: './page-title.component.html',
    styleUrls: ['./page-title.component.scss']
})
export class PageTitleComponent implements OnInit {
    mediaSize: MediaSize;
    @Input() urlToDoc: string;
    @Input() title1: string;
    @Input() title2: string;
    @Input() title3: string;
    @Input() title4: string;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
    ) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }
}
