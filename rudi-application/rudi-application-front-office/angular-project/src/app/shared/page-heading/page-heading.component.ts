import {Component, Input} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';

@Component({
    selector: 'app-page-heading',
    templateUrl: './page-heading.component.html',
    styleUrls: ['./page-heading.component.scss']
})
export class PageHeadingComponent {

    @Input()
    organizationId: string;

    @Input()
    organizationName: string;

    @Input()
    icon: string;

    @Input()
    resourceTitle: string;

    mediaSize: MediaSize;

    constructor(private readonly breakpointObserverService: BreakpointObserverService) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }
}
