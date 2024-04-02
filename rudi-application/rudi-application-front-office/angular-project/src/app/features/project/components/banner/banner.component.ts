import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {from, Observable} from 'rxjs';
import {MediaSize} from '@core/services/breakpoint-observer.service';

@Component({
    selector: 'app-banner',
    templateUrl: './banner.component.html',
    styleUrls: ['./banner.component.scss']
})
export class BannerComponent {
    @Input() mediaSize: MediaSize;

    constructor(
        private router: Router) {
    }

    submitProject(): Observable<boolean> {
        return from(this.router.navigate(['/projets/soumettre-un-projet']));
    }

    declareReuse(): Observable<boolean> {
        return from(this.router.navigate(['/projets/declarer-une-reutilisation']));
    }
}
