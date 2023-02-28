import {Component, Input, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../core/services/breakpoint-observer.service';

@Component({
    selector: 'app-dataset-heading',
    templateUrl: './dataset-heading.component.html',
    styleUrls: ['./dataset-heading.component.scss']
})
export class DatasetHeadingComponent implements OnInit {

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

    ngOnInit(): void {
    }
}
