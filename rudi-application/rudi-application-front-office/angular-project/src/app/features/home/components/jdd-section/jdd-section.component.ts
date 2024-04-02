import {Component, Input} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {Metadata} from 'micro_service_modules/api-kaccess';

@Component({
    selector: 'app-jdd-section',
    templateUrl: './jdd-section.component.html',
    styleUrls: ['./jdd-section.component.scss']
})
export class JddSectionComponent {
    @Input()
    jdds: Metadata[];

    @Input()
    isLoading: boolean;

    mediaSize: MediaSize;

    constructor(private readonly breakpointObserverService: BreakpointObserverService) {
        this.jdds = [];
        this.isLoading = true;
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }
}
