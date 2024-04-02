import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {FiltersService} from '@core/services/filters.service';
import {Theme} from '@features/home/types';
import {SwiperBreakpoint} from '@shared/rudi-swiper/types';

@Component({
    selector: 'app-themes-section',
    templateUrl: './themes-section.component.html',
    styleUrls: ['./themes-section.component.scss']
})
export class ThemesSectionComponent {
    @Input() themes: Theme[];
    @Input() isLoading: boolean;

    cardSwiperBreakpoints: SwiperBreakpoint = {
        380: {
            slidesPerView: 3,
        },
        600: {
            slidesPerView: 3,
        },
        768: {
            slidesPerView: 4,
        },
        1024: {
            slidesPerView: 5,
        },
        1280: {
            slidesPerView: 6,
        },
        1480: {
            slidesPerView: 7,
        },
        1680: {
            slidesPerView: 8,
        },
    };

    constructor(
        private readonly filtersService: FiltersService,
        private readonly router: Router
    ) {
        this.themes = [];
        this.isLoading = true;
    }

    onClickThemeCard(themeCode: string): void {
        this.filtersService.deleteAllFilters();
        this.filtersService.themesFilter.value = [themeCode];
        this.router.navigate(['/catalogue']);
    }
}
