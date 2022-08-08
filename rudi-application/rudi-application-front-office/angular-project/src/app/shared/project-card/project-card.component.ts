import {Component, Input, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../core/services/breakpoint-observer.service';
import {ProjectCatalogItem} from '../../project/model/project-catalog-item';
import {Router} from '@angular/router';
import {OwnerType, Project} from '../../projekt/projekt-model';

@Component({
    selector: 'app-project-card',
    templateUrl: './project-card.component.html',
    styleUrls: ['./project-card.component.scss']
})
export class ProjectCardComponent implements OnInit {
    @Input() projectCatalogItem: ProjectCatalogItem;
    @Input() mediaSize: MediaSize;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly router: Router,
    ) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    get ngClass(): NgClassObject {
        const ngClassFromMediaSize: NgClassObject = this.breakpointObserver.getNgClassFromMediaSize('project-card');
        return {
            ...ngClassFromMediaSize,
        };
    }

    get projectPicture(): string {
        if (this.projectCatalogItem != null && this.projectCatalogItem.logo != null) {
            return this.projectCatalogItem.logo;
        } else {
            return '/assets/images/logo_projet_par_defaut.png';
        }
    }

    get projectManager(): string {
        if (this.projectCatalogItem != null && this.projectCatalogItem.project.owner_type === OwnerType.User
            && this.projectCatalogItem.manager != null) {
            return this.projectCatalogItem.manager.name;
        }
        return '';
    }

    get projectTitle(): string {
        if (this.projectIsNotNull()) {
            return this.projectCatalogItem.project.title;
        }
        return '';
    }

    get projectDescription(): string {
        if (this.projectIsNotNull()) {
            return this.projectCatalogItem.project.description;
        }
        return '';
    }

    private projectIsNotNull(): boolean {
        return (this.projectCatalogItem != null && this.projectCatalogItem.project != null);
    }

    clickCard(): void {
        this.router.navigate(['/projets/detail/' + this.projectCatalogItem.project.uuid]);
    }
}
