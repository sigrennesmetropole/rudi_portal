import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ProjectCatalogItem} from '@features/project/model/project-catalog-item';
import {TranslateService} from '@ngx-translate/core';
import {ProjectsDescription} from 'micro_service_modules/konsult/konsult-api';

@Component({
    selector: 'app-projects-section',
    templateUrl: './projects-section.component.html',
    styleUrls: ['./projects-section.component.scss']
})
export class ProjectsSectionComponent implements OnInit {
    @Input() projects: ProjectCatalogItem[];
    @Input() projectsDescription: ProjectsDescription;

    constructor(
        private router: Router,
        private translateService: TranslateService
    ) {
    }

    redirectToProject(): void {
        this.router.navigate(['/projets']);
    }

    ngOnInit(): void {
        this.projectsDescription.title1 = this.projectsDescription?.title1 ?? this.translateService.instant('home.projectsSection.title1');
        this.projectsDescription.title2 = this.projectsDescription?.title2 ?? this.translateService.instant('home.projectsSection.title2');
        this.projectsDescription.subtitle = this.projectsDescription?.subtitle ?? this.translateService.instant('home.projectsSection.subtitle');
        this.projectsDescription.description = this.projectsDescription?.description ?? this.translateService.instant('home.projectsSection.description');
    }
}
