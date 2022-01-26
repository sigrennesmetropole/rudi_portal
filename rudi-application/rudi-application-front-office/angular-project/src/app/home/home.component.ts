import {Component, OnInit} from '@angular/core';
import {BreakpointObserverService} from '../core/services/breakpoint-observer.service';

export interface DialogData {
}

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

    mediaSize = {
        isXs: false,
        isSm: false,
        isMd: false,
        isLg: false,
        isXl: false
    };

    constructor(
        public breakpointObserver: BreakpointObserverService
    ) {
    }

    ngOnInit(): void {
        this.breakpointObserver.getMediaSize();
    }

}
