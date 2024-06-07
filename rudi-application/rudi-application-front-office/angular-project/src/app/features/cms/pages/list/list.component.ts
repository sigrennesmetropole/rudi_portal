import {Component} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize, NgClassObject} from '@core/services/breakpoint-observer.service';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {Order} from '@core/services/konsult-metier.service';
import {LogService} from '@core/services/log.service';
import {Item} from '@features/data-set/components/filter-forms/array-filter-form.component';
import {TranslateService} from '@ngx-translate/core';
import {CmsAsset, PagedCmsAssets} from 'micro_service_modules/api-cms';
import {CustomizationDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Observable, of, Subject} from 'rxjs';
import {tap} from 'rxjs/operators';


const DEFAULT_NEWS_ORDER = '-publishdate';
const FIRST_PAGE = 1;
const DEFAULT_PICTO: Base64EncodedLogo = '/assets/images/hero_section_default_picto.svg';

interface OrderItem extends Item {
    libelle: string;
    value: Order;
}

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrl: './list.component.scss'
})
export class ListComponent {
    isLoading: boolean;
    mediaSize: MediaSize;
    order = DEFAULT_NEWS_ORDER;
    orderItems: OrderItem[] = [];

    title1 = '';
    title2 = '';


    searchIsRunning = true;
    newsListTotal = 0;

    private destroy$: Subject<boolean>;


    customizationDescription: CustomizationDescription;
    customizationDescriptionIsLoading = false;

    leftPictoSrc$: Observable<Base64EncodedLogo>;
    loadingLeftPicto: boolean;
    rightPictoSrc$: Observable<Base64EncodedLogo>;
    loadingRightPicto: boolean;


    displayComponent: boolean;
    newsList: SafeHtml[];

    maxResultsPerPage = 4;
    disableScrollOnPageChange = false;

    readonly maxPageDesktop = 9;
    /** minimum = 5 */
    readonly maxPageMobile = 5;
    currentPage = FIRST_PAGE;
    offset = 0;
    limit = 12;

    constructor(
        private readonly konsultService: KonsultService,
        private readonly logger: LogService,
        private readonly translateService: TranslateService,
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserver: BreakpointObserverService,
        private imageLogoService: ImageLogoService,
        private logService: LogService
    ) {
        this.destroy$ = new Subject<boolean>();
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.isLoading = true;
        this.page = FIRST_PAGE;
        this.displayComponent = false;
        this.newsList = [];
    }


    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.isLoading = true;
        this.initCustomizationDescription();
        this.loadingLeftPicto = false;
        this.loadingRightPicto = false;
        this.leftPictoSrc$ = of(DEFAULT_PICTO);
        this.rightPictoSrc$ = of(DEFAULT_PICTO);
    }

    private initCustomizationDescription(): void {
        this.customizationDescriptionIsLoading = true;
        this.konsultService.getCustomizationDescription(this.translateService.currentLang)
            .subscribe({
                next: (customizationDescription: CustomizationDescription) => {
                    this.customizationDescription = customizationDescription;
                    this.title1 = customizationDescription.news_page_description.title1;
                    this.title2 = customizationDescription.news_page_description.title2;
                    this.orderItems = customizationDescription.news_page_description.orders as OrderItem[];
                    this.initLeftPicto();
                    this.initRightPicto();
                    this.initCmsAssets();
                    this.customizationDescriptionIsLoading = false;
                },
                error: (error) => {
                    this.logger.error(error);
                    this.customizationDescriptionIsLoading = false;
                }
            });
    }

    initLeftPicto(): void {
        this.loadingLeftPicto = true;
        this.konsultService.downloadCustomizationResource(this.customizationDescription.hero_description?.left_image)
            .subscribe({
                next: (blob: Blob) => {
                    this.leftPictoSrc$ = this.imageLogoService.createImageFromBlob(blob)
                        .pipe(
                            tap(() => {
                                this.loadingLeftPicto = false;
                            })
                        );
                },
                error: (error) => {
                    this.logger.error(error);
                    this.leftPictoSrc$ = of(DEFAULT_PICTO);
                    this.loadingLeftPicto = false;
                }
            });
    }

    initRightPicto(): void {
        this.loadingRightPicto = true;
        this.konsultService.downloadCustomizationResource(this.customizationDescription.hero_description?.right_image)
            .subscribe({
                next: (blob: Blob) => {
                    this.rightPictoSrc$ = this.imageLogoService.createImageFromBlob(blob)
                        .pipe(
                            tap(() => {
                                this.loadingRightPicto = false;
                            })
                        );
                },
                error: (error) => {
                    this.logger.error(error);
                    this.rightPictoSrc$ = of(DEFAULT_PICTO);
                    this.loadingRightPicto = false;
                }
            });
    }

    private initCmsAssets(): void {
        this.konsultService.renderAssets('NEWS', this.customizationDescription.cms_news_description.template_news_list, undefined, undefined, this.translateService.currentLang, this.offset, this.limit, this.order)
            .subscribe({
                next: (pagedCmsAssets: PagedCmsAssets) => {
                    this.newsListTotal = pagedCmsAssets.total;
                    this.searchIsRunning = false;
                    this.displayComponent = pagedCmsAssets.total > 0;
                    if (this.displayComponent) {
                        this.newsList = [];
                        pagedCmsAssets.elements.forEach((cmsAsset: CmsAsset) => {
                            this.newsList.push(this.domSanitizer.bypassSecurityTrustHtml(cmsAsset.content));
                        });
                    }
                },
                error: (err) => {
                    this.logService.error(err);
                    this.displayComponent = false;
                }
            });
    }

    onOrderChange(order: string): void {
        console.log(order);
        this.order = order;
        this.initCmsAssets();
    }

    get page(): number {
        return this.currentPage;
    }

    set page(value: number) {
        if (value < FIRST_PAGE) {
            console.warn('Page number cannot be less than ' + FIRST_PAGE);
            value = FIRST_PAGE;
        }
        this.currentPage = value;
        this.offset = (this.currentPage - 1) * this.maxResultsPerPage;
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    /**
     * Fonction permettant la gestion la pagination
     */
    handlePageChange(page: number): void {
        this.page = page;
        this.offset = (page - 1) * this.limit;
        this.initCmsAssets();
        if (!this.disableScrollOnPageChange) {
            window.scroll(0, 0);
        }
    }
}

