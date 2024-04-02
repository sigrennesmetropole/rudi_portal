import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {map} from 'rxjs/operators';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {
    LinkedDatasetTaskDependenciesService,
    LinkedDatasetTaskDependencyFetchers
} from '@core/services/tasks/projekt/linked-dataset-task-dependencies.service';
import {
    NewDatasetRequestTaskDepenciesService,
    NewDatasetRequestTaskDependencyFetchers
} from '@core/services/tasks/projekt/new-dataset-request-task-depencies.service';
import {injectDependencies} from '@shared/utils/dependencies-utils';

export interface ProjectOwnerDependencies {
    ownerName: string;
    ownerEmail: string;
}

@Component({
    selector: 'app-project-owner-detail',
    templateUrl: './project-owner-detail.component.html',
    styleUrls: ['./project-owner-detail.component.scss']
})
export class ProjectOwnerDetailComponent implements OnInit {

    loading = true;
    dependencies: ProjectOwnerDependencies;
    inconnu = ProjektMetierService.UNKOWN_USER_INFO_NAME;
    @Input()
    isNewRequest: boolean;

    constructor(private readonly route: ActivatedRoute,
                private readonly linkedDatasetTaskDependenciesService: LinkedDatasetTaskDependenciesService,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
                private readonly newDatasetRequestTaskDepenciesService: NewDatasetRequestTaskDepenciesService,
                private readonly newDatasetRequestTaskDependencyFetchers: NewDatasetRequestTaskDependencyFetchers, ) {
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            if (this.isNewRequest) {
                this.loadNewDatasetRequestOwnerInfos(idTask);
            } else {
                this.loadLinkedDatasetOwnerInfos(idTask);
            }
        }
    }

    private loadLinkedDatasetOwnerInfos(idTask: string): void {
        this.loading = true;
        this.linkedDatasetTaskDependenciesService.getTaskWithDependencies(idTask).pipe(
            injectDependencies({
                project: this.linkedDatasetDependencyFetchers.project,
            }),
            injectDependencies({
                ownerInfo: this.linkedDatasetDependencyFetchers.ownerInfo,
            }),
            map(({task, asset, dependencies}) => {
                return {
                    ownerName: dependencies.ownerInfo.name,
                    ownerEmail: dependencies.project.contact_email
                };
            })
        ).subscribe({
            next: (dependencies: ProjectOwnerDependencies) => {
                this.loading = false;
                this.dependencies = dependencies;
            },
            error: (error) => {
                this.loading = false;
                console.error(error);
            }
        });
    }

    private loadNewDatasetRequestOwnerInfos(idTask: string): void {
        this.loading = true;
        this.newDatasetRequestTaskDepenciesService.getTaskWithDependencies(idTask).pipe(
            injectDependencies({
                project: this.newDatasetRequestTaskDependencyFetchers.project,
            }),
            injectDependencies({
                ownerInfo: this.newDatasetRequestTaskDependencyFetchers.ownerInfo,
            }),
            map(({task, asset, dependencies}) => {
                return {
                    ownerName: dependencies.ownerInfo.name,
                    ownerEmail: dependencies.project.contact_email
                };
            })
        ).subscribe({
            next: (dependencies: ProjectOwnerDependencies) => {
                this.loading = false;
                this.dependencies = dependencies;
            },
            error: (error) => {
                this.loading = false;
                console.error(error);
            }
        });
    }
}
