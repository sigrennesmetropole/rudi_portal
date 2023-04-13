import {Component, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {ProjectCatalogItem} from '../../model/project-catalog-item';
import {DEFAULT_PROJECT_ORDER, ProjektMetierService} from '../../../core/services/asset/project/projekt-metier.service';
import {AclService} from '../../../acl/acl-api';
import {Router} from '@angular/router';

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
                private readonly router: Router) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    onClickProject(project: ProjectCatalogItem): Promise<boolean> {
        return this.router.navigate(['/projets/detail/' + project.project.uuid]);

    }

}
