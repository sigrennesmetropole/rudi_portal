import {HttpErrorResponse} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {
    LinkedDatasetMetadatas,
    ProjectDependenciesFetchers,
    ProjectDependenciesService,
} from '@core/services/asset/project/project-dependencies.service';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {Base64EncodedLogo} from '@core/services/image-logo.service';
import {LogService} from '@core/services/log.service';
import {PageTitleService} from '@core/services/page-title.service';
import {TranslateService} from '@ngx-translate/core';
import {injectDependencies} from '@shared/utils/dependencies-utils';
import {AclService} from 'micro_service_modules/acl/acl-api';
import {OwnerInfo, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {LinkedDatasetStatus, Project} from 'micro_service_modules/projekt/projekt-model';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

const ICON_INFO = '../assets/icons/icon_tab_infos.svg';
const PROJECT_LOGO = '/assets/images/logo_projet_par_defaut.png';

/**
 * les dépendances attendues pour un projet
 */
interface Dependencies {
    validatedLinks?: LinkedDatasetMetadatas[];
    project: Project;
    ownerInfo: OwnerInfo;
    logo: string;
}

@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html',
    styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
    project: Project;
    mediaSize: MediaSize;
    formatsMenuActive = false;
    loading = false;
    projectLogo: Base64EncodedLogo = '';
    linkedDatasets: LinkedDatasetMetadatas[] = [];
    userIdentity: string;
    isKnownUser: boolean;

    constructor(
        private readonly matIconRegistry: MatIconRegistry,
        private readonly domSanitizer: DomSanitizer,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly route: ActivatedRoute,
        private readonly projektService: ProjektService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly router: Router,
        private readonly aclService: AclService,
        private readonly translateService: TranslateService,
        private readonly pageTitleService: PageTitleService,
        private readonly projectDependenciesService: ProjectDependenciesService,
        private readonly projectDependenciesFetchers: ProjectDependenciesFetchers,
        private readonly logService: LogService
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.matIconRegistry.addSvgIcon(
            'icon-info',
            this.domSanitizer.bypassSecurityTrustResourceUrl(ICON_INFO)
        );
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.projectUuid = params.uuid;
        });
    }

    set projectUuid(projectUuid: string) {
        if (projectUuid) {
            this.loading = true;
            this.loadProjectDependencies(projectUuid).subscribe({
                next: (datasetDependencies: Dependencies) => {
                    this.project = datasetDependencies.project;
                    // le ownerInfo n'est plus nullable, si inconnu il renvoie la chaine [Utilisateur inconnu]
                    this.userIdentity = datasetDependencies.ownerInfo?.name;
                    // Si inconnu on n'affiche pas d'adresse dans le bouton de contact
                    this.isKnownUser = this.userIdentity !== ProjektMetierService.UNKOWN_USER_INFO_NAME;
                    this.linkedDatasets = datasetDependencies.validatedLinks;
                    this.pageTitleService.setPageTitle(datasetDependencies.project.title, 'Détail');
                    if (datasetDependencies.logo) {
                        this.projectLogo = datasetDependencies.logo;
                    } else {
                        this.projectLogo = PROJECT_LOGO;
                    }
                },
                error: (error: HttpErrorResponse) => {
                    this.logService.error(error);
                    this.loading = false;
                    if (error.status == 400) {
                        this.router.navigate(['/error/400']);
                    }
                    if (error.status == 404) {
                        this.router.navigate(['/error/404']);
                    }
                },
                complete: () => {
                    // Une fois qu'on a toutes les dépendances, on enlève le loader
                    this.loading = false;
                }
            });
        }
    }

    /**
     * Méthode qui charge le projet et ses dependencies
     * @param projectUuid , uuid du projet
     * @private
     */
    private loadProjectDependencies(projectUuid: string): Observable<Dependencies> {
        return this.projectDependenciesService.getProject(projectUuid).pipe(
            injectDependencies({
                linkedDatasetMetadatas: this.projectDependenciesFetchers.linkedDatasetMetadatas([LinkedDatasetStatus.Validated, LinkedDatasetStatus.Archived]),
            }),
            injectDependencies({
                logo: this.projectDependenciesFetchers.logo
            }),
            injectDependencies({
                ownerInfo: this.projectDependenciesFetchers.ownerInfo
            }),
            map(({project, dependencies}) => {
                return {
                    validatedLinks: dependencies.linkedDatasetMetadatas,
                    ownerInfo: dependencies.ownerInfo,
                    logo: dependencies.logo,
                    project
                };
            })
        );
    }

    get projectType(): string {
        return this.project?.type?.label || this.translateService.instant('project.detail.defaultType');
    }


    onClickAccessUrl(): void {
        window.open(this.project.access_url, '_blank');
    }

}
