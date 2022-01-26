import {Component, Input, OnInit} from '@angular/core';
import {MediaSize} from '../services/breakpoint-observer.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  @Input() mediaSize: MediaSize;

  constructor() { }

  ngOnInit(): void {
  }

  goToTop(): void {
    window.scroll({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }

}
