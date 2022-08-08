import {Component, Input} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {from, Observable} from 'rxjs';
import {Router} from '@angular/router';

@Component({
    selector: 'app-banner',
    templateUrl: './banner.component.html',
    styleUrls: ['./banner.component.scss']
})
export class BannerComponent {
    @Input() mediaSize: MediaSize;

    constructor(private breakpointObserver: BreakpointObserverService,
                private router: Router) {
    }

    ngOnInit(): void {
    }

    submitProject(): Observable<boolean> {
        return from(this.router.navigate(['/projets/soumettre-un-projet']));
    }
    declareReuse(): Observable<boolean> {
        return from(this.router.navigate(['/projets/declarer-une-reutilisation']));
    }
}
