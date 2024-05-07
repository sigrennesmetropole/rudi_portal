import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class SidenavOpeningsService {

    private readonly sidenavOpening: Subject<void> = new Subject<void>();
    readonly sideNavOpening$: Observable<void> = this.sidenavOpening.asObservable();

    openSidenav(): void {
        this.sidenavOpening.next();
    }

}
