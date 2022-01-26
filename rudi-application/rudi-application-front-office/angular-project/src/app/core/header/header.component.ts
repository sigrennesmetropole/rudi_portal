import {Component, Input} from '@angular/core';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {UserService} from '../services/user.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../services/breakpoint-observer.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
    @Input() mediaSize: MediaSize;
    isCollapsed = true;

    constructor(
        private location: Location,
        public dialog: MatDialog,
        public router: Router,
        public readonly utilisateurService: UserService,
        private readonly breakpointObserver: BreakpointObserverService,
    ) {
    }

    get ngClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('header-container');
    }

    handleClickBurger(): void {
        this.isCollapsed = !this.isCollapsed;
    }

    goBack(): void {
        this.location.back();
    }
}
