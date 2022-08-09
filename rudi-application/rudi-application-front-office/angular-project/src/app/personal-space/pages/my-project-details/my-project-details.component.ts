import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {OwnerInfo, Project} from '../../../projekt/projekt-api';
import * as moment from 'moment';
import {ProjectDependenciesFetchers, ProjectDependenciesService} from '../../../core/services/project-dependencies.service';
import {injectDependencies} from '../../../shared/utils/dependencies-utils';
import {map} from 'rxjs/operators';

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
                private projectDependenciesFetchers: ProjectDependenciesFetchers) {
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
