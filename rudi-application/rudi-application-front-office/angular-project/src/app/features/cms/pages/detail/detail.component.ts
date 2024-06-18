import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {ActivatedRoute, Params} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {PageTitleService} from '@core/services/page-title.service';
import {TranslateService} from '@ngx-translate/core';
import {CmsAsset} from 'micro_service_modules/api-cms';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {throwError} from 'rxjs';
import {catchError, switchMap} from 'rxjs/operators';


const TYPE_FIELD: string = 'type';
const UUID_FIELD: string = 'uuid';
const TEMPLATE_FIELD: string = 'template';
const NEWS_TITLE: string = 'titre';

@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html',
    styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

    isLoading: boolean;
    isSuccess: boolean;
    safeHtml: SafeHtml;
    mediaSize: MediaSize;

    constructor(
        private readonly route: ActivatedRoute,
        private readonly konsultService: KonsultService,
        private readonly logger: LogService,
        private readonly translateService: TranslateService,
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly pageTitleService: PageTitleService,
    ) {
        this.init();
    }

    private init() {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.isLoading = true;
        this.isSuccess = true;
    }

    ngOnInit(): void {
        this.route.params.pipe(
            switchMap((params: Params) => {
                if (params[TYPE_FIELD] && params[UUID_FIELD] && params[TEMPLATE_FIELD]) {
                    return this.konsultService.renderAsset(params[TYPE_FIELD].toUpperCase(), params[UUID_FIELD], params[TEMPLATE_FIELD], this.translateService.currentLang);
                } else {
                    return throwError(() => new Error('Certains paramètres obligatoire ne sont pas renseignées'));
                }
            }),
            catchError(error => {
                return throwError(() => error);
            })
        ).subscribe({
            next: (value: CmsAsset) => {
                // Pour définir le titre de l'onglet de détail de la news, on récupère le titre de la news dans la réponse de la requête
                if (value.title) {
                    this.pageTitleService.setPageTitle(value.title, this.translateService.instant('pageTitle.defaultDetail'));
                }
                this.safeHtml = this.domSanitizer.bypassSecurityTrustHtml(value.content);
                this.isLoading = false;
            },
            error: error => {
                this.isSuccess = false;
                this.isLoading = false;
                this.logger.error(error);
            }
        });
    }

}
