import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {BreakpointObserverService} from '@core/services/breakpoint-observer.service';

@Component({
    selector: 'app-error-page',
    templateUrl: './error-page.component.html',
    styleUrls: ['./error-page.component.scss']
})
export class ErrorPageComponent implements OnInit {
    statusError: number;
    libelleError: string;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly route: ActivatedRoute,
    ) {
    }

    ngOnInit(): void {
        this.route.params
            .subscribe({
                next: (params: Params) => {
                    const status: number = Number(params.status);
                    this.statusError = 404;
                    if (!isNaN(status)) {
                        this.statusError = status;
                        if (this.statusError < 300 || this.statusError > 599) {
                            this.statusError = 404;
                        }
                    }
                    this.libelleError = this.initLibelle(this.statusError);
                }
            });
    }

    initLibelle(status: number): string {
        switch (status) {
            case 400: {
                return 'pageNotFound.invalidUrlErrorMessage';
            }
            case 404: {
                return 'pageNotFound.ressourceNotFoundErrorMessage';
            }
            default: {
                return 'pageNotFound.defaultErrorMessage';
            }
        }
    }

}
