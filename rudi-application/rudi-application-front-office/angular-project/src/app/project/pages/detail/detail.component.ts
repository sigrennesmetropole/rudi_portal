import {Component, OnInit} from '@angular/core';
import {Metadata} from '../../../api-kaccess';
import {ActivatedRoute, Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {EMPTY} from 'rxjs';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {ProjektMetierService} from '../../../core/services/projekt-metier.service';
import {OwnerType, Project, ProjectAllOf} from '../../../projekt/projekt-model';
import {Base64EncodedLogo} from '../../../core/services/image-logo.service';
import {catchError} from 'rxjs/operators';
import {AclService} from '../../../acl/acl-api';
import {User} from '../../../acl/acl-model';
import {TranslateService} from '@ngx-translate/core';
import {PageTitleService} from '../../../core/services/page-title.service';

const ICON_INFO = '../assets/icons/icon_info_default_color.svg';

@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html',
    styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
    project: Project;
    mediaSize: MediaSize;
    formatsMenuActive = false;
    projectLogo: Base64EncodedLogo = '/assets/images/logo_projet_par_defaut.png';
    managerUser: User;
    linkedDatasets: Metadata[];

    constructor(
        private readonly matIconRegistry: MatIconRegistry,
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly route: ActivatedRoute,
        private readonly projektMetierService: ProjektMetierService,
        private readonly router: Router,
        private readonly aclService: AclService,
        private readonly translateService: TranslateService,
        private readonly pageTitleService: PageTitleService,
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.matIconRegistry.addSvgIcon(
            'icon-info',
            this.domSanitizer.bypassSecurityTrustResourceUrl(ICON_INFO)
        );
    }

    get userIdentity(): string | undefined {
        if (!this.managerUser) {
            return undefined;
        }
        return `${this.managerUser.firstname} ${this.managerUser.lastname}`;
    }

    set projectUuid(projectUuid: string) {
        if (projectUuid) {
            this.projektMetierService.getProject(projectUuid).pipe(
                catchError(() => {
                    this.router.navigate(['/projets']);
                    return EMPTY;
                })
            ).subscribe(project => {
                this.project = project;
                if (project.owner_type === OwnerType.User) {
                    this.aclService.getUserInfo(project.owner_uuid).subscribe(user => this.managerUser = user);
                }
                this.projektMetierService.getMetadataLinked(this.project.uuid)
                    .subscribe(linkedDatasets => this.linkedDatasets = linkedDatasets);
                this.pageTitleService.setPageTitle(project.title, 'Détail');
            });
            this.projektMetierService.getProjectLogo(projectUuid).pipe(
                catchError(() => EMPTY) // la console remonte déjà l'erreur HTTP
            ).subscribe(projectLogo => this.projectLogo = projectLogo);
        }
    }

    get projectType(): string {
        return this.project?.type?.label || this.translateService.instant('project.detail.defaultType');
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.projectUuid = params.uuid;
        });
    }

    onClickAccessUrl(): void {
        window.open(this.project.access_url, '_blank');
    }

}
