import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

import {BreakpointObserverService, MediaSize} from './core/services/breakpoint-observer.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  mediaSize: MediaSize;

  constructor(
    private readonly breakpointObserver: BreakpointObserverService,
    private readonly translate: TranslateService,
  ) {
    translate.setDefaultLang('fr');
  }

  ngOnInit(): void {
    this.mediaSize = this.breakpointObserver.getMediaSize();
  }

}
