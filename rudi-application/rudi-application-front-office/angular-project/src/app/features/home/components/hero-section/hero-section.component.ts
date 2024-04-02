import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FiltersService} from '@core/services/filters.service';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {LogService} from '@core/services/log.service';
import {TranslateService} from '@ngx-translate/core';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {HeroDescription} from 'micro_service_modules/konsult/konsult-model';
import {Observable, of, Subject} from 'rxjs';
import {takeUntil, tap} from 'rxjs/operators';

const DEFAULT_PICTO: Base64EncodedLogo = '/assets/images/hero_section_default_picto.svg';

@Component({
    selector: 'app-hero-section',
    templateUrl: './hero-section.component.html',
    styleUrls: ['./hero-section.component.scss']
})
export class HeroSectionComponent implements OnInit, OnDestroy {
    private destroy$: Subject<boolean>;

    @Input() heroDescription: HeroDescription;

    leftPictoSrc$: Observable<Base64EncodedLogo>;
    loadingLeftPicto: boolean;
    rightPictoSrc$: Observable<Base64EncodedLogo>;
    loadingRightPicto: boolean;
    title1: string;
    title2: string;
    searchbarPlaceholder: string;

    constructor(
        private filtersService: FiltersService,
        private konsultService: KonsultService,
        private translateService: TranslateService,
        private imageLogoService: ImageLogoService,
        private logger: LogService,
        private router: Router,
    ) {
        this.destroy$ = new Subject<boolean>();
    }

    initLeftPicto(): void {
        this.loadingLeftPicto = true;
        this.konsultService.downloadCustomizationResource(this.heroDescription.left_image)
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
        this.konsultService.downloadCustomizationResource(this.heroDescription.right_image)
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

    onSearchInputChanges(search: string): void {
        this.filtersService.deleteAllFilters();
        this.filtersService.searchFilter.value = search;
        this.router.navigate(['/catalogue']);
    }

    ngOnInit(): void {
        this.filtersService.searchFilter.value$
            .pipe(takeUntil(this.destroy$))
            .subscribe();

        this.loadingLeftPicto = false;
        this.loadingRightPicto = false;
        this.searchbarPlaceholder = this.translateService.instant('home.heroSection.searchbarPlaceholder');
        this.title1 = this.heroDescription?.title1 ?? this.translateService.instant('home.heroSection.title1');
        this.title2 = this.heroDescription?.title2 ?? this.translateService.instant('home.heroSection.title2');
        this.leftPictoSrc$ = of(DEFAULT_PICTO);
        this.rightPictoSrc$ = of(DEFAULT_PICTO);

        if (this.heroDescription?.left_image) {
            this.initLeftPicto();
        }
        if (this.heroDescription?.right_image) {
            this.initRightPicto();
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.complete();
    }
}
