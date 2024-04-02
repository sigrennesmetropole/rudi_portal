import {Component, Input, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {TranslateService} from '@ngx-translate/core';
import {CmsAsset} from 'micro_service_modules/api-cms';
import {CmsNewsDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';

const DEFAULT_ORDER: string = '-updatedate';
const DEFAULT_CATEGORIES: Array<string> = ['/rudi/news/a-la-une'];

const LIMIT_TEMPLATE_WITH_IMAGE: number = 1;
const OFFSET_TEMPLATE_WITH_IMAGE: number = 0;

const LIMIT_TEMPLATE_WITHOUT_IMAGE: number = 2;
const OFFSET_TEMPLATE_WITHOUT_IMAGE: number = 1;

interface News {
    contentNewsWithImage: SafeHtml;
    contentsNewsWithoutImage: Array<SafeHtml>;
}

@Component({
    selector: 'app-cms-news-section',
    templateUrl: './cms-news-section.component.html',
    styleUrls: ['./cms-news-section.component.scss']
})
export class CmsNewsSectionComponent implements OnInit {
    @Input() cmsNewsDescription: CmsNewsDescription;
    displayComponent: boolean;
    displayNewsWithoutImage: boolean;
    news: News;
    mediaSize: MediaSize;

    constructor(
        private readonly konsultService: KonsultService,
        private readonly logService: LogService,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly translateService: TranslateService,
        private readonly domSanitizer: DomSanitizer
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.displayComponent = false;
        this.displayNewsWithoutImage = false;
        this.news = {
            contentNewsWithImage: '',
            contentsNewsWithoutImage: []
        };
    }

    ngOnInit(): void {
        this.initNews();
    }

    private initNews() {
        const date: Date = new Date();
        const formattedDate: string = date.toISOString().slice(0, 10);
        const publishDateFilter: string = 'publishdate[lte]=' + formattedDate;
        const unpublishDateFilter: string = 'unpublishdate[gt]=' + formattedDate;

        this.konsultService.renderAssets('NEWS', this.cmsNewsDescription.template_simple_with_image, DEFAULT_CATEGORIES, [publishDateFilter, unpublishDateFilter], this.translateService.currentLang, OFFSET_TEMPLATE_WITH_IMAGE, LIMIT_TEMPLATE_WITH_IMAGE, DEFAULT_ORDER)
            .subscribe({
                next: (cmsAsset: Array<CmsAsset>) => {
                    this.displayComponent = cmsAsset.length > 0;
                    if (this.displayComponent) {
                        this.news.contentNewsWithImage = this.domSanitizer.bypassSecurityTrustHtml(cmsAsset.pop()?.content);
                    }
                },
                error: (err) => {
                    this.logService.error(err);
                    this.displayComponent = false;
                }
            });

        this.konsultService.renderAssets('NEWS', this.cmsNewsDescription.template_simple, DEFAULT_CATEGORIES, [publishDateFilter, unpublishDateFilter], this.translateService.currentLang, OFFSET_TEMPLATE_WITHOUT_IMAGE, LIMIT_TEMPLATE_WITHOUT_IMAGE, DEFAULT_ORDER)
            .subscribe({
                next: (cmsAsset: Array<CmsAsset>) => {
                    this.displayNewsWithoutImage = cmsAsset.length > 0;
                    if (this.displayNewsWithoutImage) {
                        cmsAsset.forEach((cmsAsset: CmsAsset) => {
                            this.news.contentsNewsWithoutImage.push(this.domSanitizer.bypassSecurityTrustHtml(cmsAsset.content));
                        });
                    }
                },
                error: (err) => {
                    this.logService.error(err);
                    this.displayNewsWithoutImage = false;
                }
            });
    }
}
