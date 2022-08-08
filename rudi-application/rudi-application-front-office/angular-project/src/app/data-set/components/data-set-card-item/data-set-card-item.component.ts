import {Component, Input, OnInit} from '@angular/core';
import {Metadata} from '../../../api-kaccess';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';

@Component({
    selector: 'app-data-set-card-item',
    templateUrl: './data-set-card-item.component.html',
    styleUrls: ['./data-set-card-item.component.scss']
})
export class DataSetCardItemComponent implements OnInit {
    @Input() dataset: Metadata;

    /**
     * Add mat-divider at the bottom ot the card item. Default : false.
     */
    @Input() divider = false;

    mediaSize: MediaSize;

    constructor(
        private readonly breakpointObserverService: BreakpointObserverService,
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }

    ngOnInit(): void {
    }

}
