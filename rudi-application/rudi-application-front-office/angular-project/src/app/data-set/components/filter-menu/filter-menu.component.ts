import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatMenuTrigger} from '@angular/material/menu';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-filter-menu',
  templateUrl: './filter-menu.component.html',
  styleUrls: ['./filter-menu.component.scss']
})
export class FilterMenuComponent {
  @Input() buttonTextKey: string;
  @Input() counter$: Observable<number>;
  @Input() isHidden = false;
  @Output() closed = new EventEmitter<void>();
  @ViewChild('matMenuTrigger') matMenuTrigger: MatMenuTrigger;
  private _menuIsOpened = false;

  get menuIsOpened(): boolean {
    return this._menuIsOpened;
  }

  openMenuOrSidenav(): void {
    this.matMenuTrigger.openMenu();
  }

  close(): void {
    this.matMenuTrigger.closeMenu();
  }

  onMenuOpen(): void {
    this._menuIsOpened = true;
  }

  onMenuClose(): void {
    this._menuIsOpened = false;
    this.closed.emit();
  }

}
