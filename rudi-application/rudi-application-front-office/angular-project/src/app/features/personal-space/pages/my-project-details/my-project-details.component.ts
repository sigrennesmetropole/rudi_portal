import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PageTitleService} from '@core/services/page-title.service';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {map} from 'rxjs/operators';
import {ProjectDependenciesFetchers, ProjectDependenciesService} from '@core/services/asset/project/project-dependencies.service';
import {OwnerInfo, Project} from 'micro_service_modules/projekt/projekt-api';
import {injectDependencies} from '@shared/utils/dependencies-utils';

interface MyProjectDetailsDependencies {
    project?: Project;
    logo?: string;
    ownerInfo?: OwnerInfo;
}

@Component({
    selector: 'app-my-project-details',
    templateUrl: './my-project-details.component.html',
    styleUrls: ['./my-project-details.component.scss']
})
export class MyProjectDetailsComponent implements OnInit {

    public project: Project;
    public projectLogo: string;
    public projectOwnerInfo: OwnerInfo;
    public loading = false;

    constructor(private readonly route: ActivatedRoute,
                private projectDependenciesService: ProjectDependenciesService,
                private projectDependenciesFetchers: ProjectDependenciesFetchers,
                private readonly pageTitleService: PageTitleService,
                private readonly translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.projectUuid = params.projectUuid;
        });
    }

    set projectUuid(uuid: string) {
        this.loading = true;
        this.projectDependenciesService.getProject(uuid).pipe(
            injectDependencies({
                ownerInfo: this.projectDependenciesFetchers.ownerInfo,
                logo: this.projectDependenciesFetchers.logo,
            }),
            map(({project, dependencies}) => {
                return {
                    project,
                    logo: dependencies.logo,
                    ownerInfo: dependencies.ownerInfo
                };
            })
        ).subscribe({
            next: (dependencies: MyProjectDetailsDependencies) => {
                if (dependencies.project.title) {
                    this.pageTitleService.setPageTitle(dependencies.project.title, this.translateService.instant('pageTitle.defaultDetail'));
                } else {
                    this.pageTitleService.setPageTitleFromUrl('/personal-space/my-activity');
                }
                this.project = dependencies.project;
                this.projectLogo = dependencies.logo;
                this.projectOwnerInfo = dependencies.ownerInfo;
                this.loading = false;
            },
            error: (error) => {
                console.error(error);
                this.loading = false;
            }
        });
    }

    /**
     * Récupération d'une valeur de champ date au format DD/MM/YYYY
     * @param fieldName le nom du champ du projet
     */
    getDateToFormat(fieldName: string): string {
        if (!this.project || this.project[fieldName] == null) {
            return null;
        }

        return moment(this.project[fieldName]).format('DD/MM/YYYY');
    }
}
