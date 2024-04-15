import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatSidenavModule} from '@angular/material/sidenav';
import {CmsNewsSectionComponent} from '@features/home/components/cms-news-section/cms-news-section.component';
import {CmsProjectValuesSectionComponent} from '@features/home/components/cms-project-values-section/cms-project-values-section.component';
import {HeroSectionComponent} from '@features/home/components/hero-section/hero-section.component';
import {JddSectionComponent} from '@features/home/components/jdd-section/jdd-section.component';
import {KeyFiguresSectionComponent} from '@features/home/components/key-figures-section/key-figures-section.component';
import {ProjectsSectionComponent} from '@features/home/components/projects-section/projects-section.component';
import {ThemesSectionComponent} from '@features/home/components/themes-section/themes-section.component';
import {HomeComponent} from '@features/home/pages/home/home.component';
import {SharedModule} from '@shared/shared.module';

@NgModule({
    declarations: [
        HomeComponent,
        HeroSectionComponent,
        ThemesSectionComponent,
        KeyFiguresSectionComponent,
        ThemesSectionComponent,
        ProjectsSectionComponent,
        JddSectionComponent,
        CmsNewsSectionComponent,
        CmsProjectValuesSectionComponent
    ],
    imports: [
        CommonModule,
        MatSidenavModule,
        SharedModule,
    ],
    exports: [
        HomeComponent,
    ]
})
export class HomeModule {
}
