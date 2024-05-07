import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {DEFAULT_PROJECT_ORDER, ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {URIComponentCodec} from '@core/services/codecs/uri-component-codec';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {ProjectCatalogItem} from '../../model/project-catalog-item';

@Component({
    selector: 'app-list',
    templateUrl: './list.component.html',
    styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
    mediaSize: MediaSize;
    searchIsRunning = true;
    projectListTotal = 0;
    order = DEFAULT_PROJECT_ORDER;

    constructor(private projektMetierService: ProjektMetierService,
                private aclService: AclService,
                private readonly breakpointObserver: BreakpointObserverService,
                private readonly router: Router,
                private readonly uriComponentCodec: URIComponentCodec,
    ) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    onClickProject(project: ProjectCatalogItem): Promise<boolean> {
        return this.router.navigate(['/projets/detail/' + project.project.uuid + '/' + this.uriComponentCodec.normalizeString(project.project.title)]);

    }

}
