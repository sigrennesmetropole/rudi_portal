import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {injectDependencies} from '../../../shared/utils/task-utils';
import {map} from 'rxjs/operators';
import {LinkedDatasetTaskDependencyFetchers, LinkedDatasetTaskService} from '../../../core/services/linked-dataset-task.service';

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

    loading: boolean = true;
    dependencies: ProjectOwnerDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly linkedDatasetTaskService: LinkedDatasetTaskService,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers) {
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            this.loading = true;
            this.linkedDatasetTaskService.getTask(idTask).pipe(
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
    }

}
