import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '@core/services/breakpoint-observer.service';
import {URIComponentCodec} from '@core/services/codecs/uri-component-codec';
import {ProjectCatalogItem} from '@features/project/model/project-catalog-item';

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
        private readonly uriComponentCodec: URIComponentCodec,
        private readonly router: Router,
        private readonly projektMetierService: ProjektMetierService,
    ) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        if (!this.projectCatalogItem.logo) {
            this.projektMetierService.getProjectLogo(this.projectCatalogItem.project.uuid).subscribe((logo) => {
                this.projectCatalogItem.logo = logo;
            });
        }
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

    get projectOwnerInfo(): string {
        if (this.projectCatalogItem != null) {
            // ownerInfo n'est plus nullable, il renvoie [Utilisateur inconnu] si user not found (RUDI-2408)
            return this.projectCatalogItem?.ownerInfo?.name;
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
        this.router.navigate(['/projets/detail/' + this.projectCatalogItem.project.uuid + '/' + this.uriComponentCodec.normalizeString(this.projectCatalogItem.project.title)]);
    }
}
