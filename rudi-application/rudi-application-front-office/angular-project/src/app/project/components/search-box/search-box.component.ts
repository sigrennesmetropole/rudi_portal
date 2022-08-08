import {Component, Input, OnInit} from '@angular/core';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';

@Component({
  selector: 'app-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.scss']
})
export class SearchBoxComponent implements OnInit {
    @Input() mediaSize: MediaSize;
  constructor() { }

  ngOnInit(): void {
  }

}
