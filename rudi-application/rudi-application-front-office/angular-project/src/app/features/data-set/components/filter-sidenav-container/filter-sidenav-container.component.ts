import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-filter-sidenav-container',
  templateUrl: './filter-sidenav-container.component.html',
  styleUrls: ['./filter-sidenav-container.component.scss']
})
export class FilterSidenavContainerComponent {

  @Input() titleKey: string;
  @Output() close = new EventEmitter<void>();

}
