import {Component, Inject, OnInit, SecurityContext} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {DomSanitizer} from '@angular/platform-browser';
import {Event, Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {Observable} from 'rxjs';
import {distinctUntilChanged, skip, take} from 'rxjs/operators';

export interface Data {
    /** Any translated string or HTML message */
    message: string;

    /**
     * true if the SnackBar should be displayed after the first RouteChange but dismissed after the second RouteChange.
     * Useful for displaying a message immediately after a router.navigate.
     * Default : false.
     */
    keepBeforeSecondRouteChange?: boolean;

    /**
     * Par défaut : INFO
     */
    level?: Level;
}

export enum Level {
    INFO, ERROR
}

/**
 * @return false only when all URL are defined and different
 */
function eventUrlComparator(e1: Event, e2: Event): boolean {
    // tslint:disable-next-line
    const e1Url = (e1 as any)?.url;
    // tslint:disable-next-line
    const e2Url = (e2 as any)?.url;
    if (!e1Url || !e2Url) {
        return true;
    }
    return e1Url === e2Url;
}

@Component({
    selector: 'app-notification-template',
    templateUrl: './notification-template.component.html',
    styleUrls: ['./notification-template.component.scss']
})
export class NotificationTemplateComponent implements OnInit {
    /**
     * Pour savoir comment restituer le composant en mode desktop/mobile
     */
    mediaSize: MediaSize;
    readonly data?: Data;

    constructor(@Inject(MAT_SNACK_BAR_DATA) data: string | Data,
                private breakpointObserver: BreakpointObserverService,
                public snackBarRef: MatSnackBarRef<NotificationTemplateComponent>,
                private readonly router: Router,
                private readonly domSanitizer: DomSanitizer,
    ) {
        if (typeof data === 'string') {
            this.data = {
                message: data
            };
        } else {
            this.data = data;
        }
        this.sanitizeData();
    }

    private sanitizeData(): void {
        this.data.message = this.domSanitizer.sanitize(SecurityContext.HTML, this.data.message);
    }

    ngOnInit(): void {
        // On récupère les infos sur la restitution
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.scheduleDismiss();
    }

    get faIcon(): string {
        if (this.data.level === Level.ERROR) {
            return 'fa-exclamation-circle';
        } else {
            return 'fa-check-circle';
        }
    }

    get dismissTriggerEvent$(): Observable<Event> {
        if (this.data?.keepBeforeSecondRouteChange) {
            // Trigger dismiss only after route URL really changed
            return this.router.events.pipe(
                distinctUntilChanged(eventUrlComparator),
                skip(1),
                take(1),
            );
        } else {
            // Trigger dismiss as soon as the first RouteChange event
            return this.router.events.pipe(
                take(1),
            );
        }
    }

    private scheduleDismiss(): void {
        this.dismissTriggerEvent$.subscribe(() => {
            this.snackBarRef.dismiss();
        });
    }
}
