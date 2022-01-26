import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidenavOpeningsService {

  private readonly sidenavOpening = new Subject<void>();
  readonly sideNavOpening$ = this.sidenavOpening.asObservable();

  openSidenav(): void {
    this.sidenavOpening.next();
  }

}
