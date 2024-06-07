import {Component, Input, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {TranslateService} from '@ngx-translate/core';
import {CmsAsset, PagedCmsAssets} from 'micro_service_modules/api-cms';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {CmsProjectValuesDescription} from 'micro_service_modules/konsult/konsult-model';

const OFFSET: number = 0;
const LIMIT: number = 4;


@Component({
    selector: 'app-cms-project-values-section',
    templateUrl: './cms-project-values-section.component.html',
    styleUrls: ['./cms-project-values-section.component.scss']
})
export class CmsProjectValuesSectionComponent implements OnInit {
    @Input() cmsProjectValuesDescription: CmsProjectValuesDescription;
    displayComponent: boolean;
    projectValues: Array<SafeHtml>;
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
        this.projectValues = [];
    }

    ngOnInit() {
        this.initProjectValues();
    }

    initProjectValues() {
        this.cmsProjectValuesDescription.title1 = this.cmsProjectValuesDescription?.title1 ?? this.translateService.instant('home.projectValuesSection.title1');
        this.cmsProjectValuesDescription.title2 = this.cmsProjectValuesDescription?.title2 ?? this.translateService.instant('home.projectValuesSection.title2');
        this.cmsProjectValuesDescription.description = this.cmsProjectValuesDescription?.description ?? this.translateService.instant('home.projectValuesSection.description');

        this.konsultService.renderAssets(
            'PROJECTVALUES',
            this.cmsProjectValuesDescription.template_simple,
            [this.cmsProjectValuesDescription.category],
            [],
            this.translateService.currentLang,
            OFFSET,
            LIMIT
        ).subscribe({
            next: (pagedCmsAssets: PagedCmsAssets): void => {
                this.displayComponent = pagedCmsAssets.total > 0;
                if (this.displayComponent) {
                    pagedCmsAssets.elements.forEach((cmsAsset: CmsAsset) => {
                        this.projectValues.push(this.domSanitizer.bypassSecurityTrustHtml(cmsAsset.content));
                    });
                }
            },
            error(err) {
                this.logService.error(err);
                this.displayComponent = false;
            }
        });
    }
}
