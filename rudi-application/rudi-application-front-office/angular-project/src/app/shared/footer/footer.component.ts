import {Component, Input, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {RedirectService} from '@core/services/redirect.service';
import {TranslateService} from '@ngx-translate/core';
import {AppInfo} from 'micro_service_modules/acl/acl-api/model/models';
import {CmsAsset} from 'micro_service_modules/api-cms';
import {CustomizationDescription, KonsultService, MiscellaneousService} from 'micro_service_modules/konsult/konsult-api';
import {CmsTermsDescription} from 'micro_service_modules/konsult/konsult-model';
import {forkJoin} from 'rxjs';
import {FooterUtils} from '../utils/footer-utils';

const OFFSET: number = 0;
const LIMIT: number = 3;

@Component({
    selector: 'app-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    @Input() mediaSize: MediaSize;
    appInfo: AppInfo;
    loading = false;
    twitterLinkHref: string;
    linkedinLinkHref: string;

    customizationDescription: CustomizationDescription;
    cmsTermsDescription: CmsTermsDescription;
    customizationDescriptionIsLoading: boolean;

    displayComponent: boolean;
    termsValues: Array<SafeHtml>;


    constructor(
        private readonly miscellaneousService: MiscellaneousService,
        private readonly redirectService: RedirectService,
        private readonly propertiesService: PropertiesMetierService,
        private readonly logService: LogService,
        private readonly translateService: TranslateService,
        private readonly konsultService: KonsultService,
        private readonly logger: LogService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly domSanitizer: DomSanitizer
    ) {
        this.customizationDescriptionIsLoading = false;
        this.displayComponent = false;
        this.termsValues = [];
        this.initCustomizationDescription();

    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.miscellaneousService.getApplicationInformation().subscribe(appInfo => this.appInfo = appInfo);
        this.loading = true;
        forkJoin({
            twitterLinkHref: this.propertiesService.get('rudidatarennes.twitter'),
            linkedinLinkHref: this.propertiesService.get('rudidatarennes.linkedin'),
        }).subscribe({
            next: ({twitterLinkHref, linkedinLinkHref}) => {
                this.loading = false;
                this.twitterLinkHref = twitterLinkHref;
                this.linkedinLinkHref = linkedinLinkHref;
            },
            error: (error) => {
                this.loading = false;
                this.logService.error(error);
            }
        });
    }

    initTerms(): void {
        this.konsultService.renderAssets(
            'TERMS',
            this.cmsTermsDescription.template_simple,
            [this.cmsTermsDescription.category],
            [],
            this.translateService.currentLang,
            OFFSET,
            LIMIT
        ).subscribe({
            next: (cmsAssets: Array<CmsAsset>): void => {
                this.displayComponent = cmsAssets.length > 0;
                if (this.displayComponent) {
                    cmsAssets.forEach((cmsAsset: CmsAsset) => {
                        this.termsValues.push(this.domSanitizer.bypassSecurityTrustHtml(cmsAsset.content));
                    });
                }
            },
            error(err) {
                this.logService.error(err);
                this.displayComponent = false;
            }
        });
    }

    goToTop(): void {
        this.redirectService.goToTop();
    }

    get currentYear(): number {
        return FooterUtils.getCurrentYear();
    }

    private initCustomizationDescription(): void {
        this.customizationDescriptionIsLoading = true;
        this.konsultService.getCustomizationDescription(this.translateService.currentLang)
            .subscribe({
                next: (customizationDescription: CustomizationDescription) => {
                    this.customizationDescription = customizationDescription;
                    this.cmsTermsDescription = customizationDescription.cms_terms_description
                    this.customizationDescriptionIsLoading = false;
                    this.initTerms();
                },
                error: (error) => {
                    this.logger.error(error);
                    this.customizationDescriptionIsLoading = false;
                }
            });
    }
}
