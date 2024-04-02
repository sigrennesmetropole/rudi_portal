import {Component, Input, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit{

    @Input() mediaSize: MediaSize;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
    ) {

    }



    onChanges(search: string): void {
        console.log(search);
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }
}
