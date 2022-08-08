import {Component, OnInit} from '@angular/core';
import {BreakpointObserverService} from '../../../core/services/breakpoint-observer.service';
import {ActivatedRoute, Router} from '@angular/router';
import {map} from 'rxjs/operators';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {injectDependencies} from '../../../shared/utils/task-utils';
import {LinkedDatasetTaskDependencyFetchers, LinkedDatasetTaskService} from '../../../core/services/linked-dataset-task.service';
import {Moment} from 'moment';
import * as moment from 'moment';

/**
 * les dépendances de la page globale : détail de la demande
 */
export interface RequestDetailDependencies {
    ownerName: string;
    ownerEmail: string;
    receivedDate: Moment;
    datasetTitle: string;
}

@Component({
    selector: 'app-request-detail',
    templateUrl: './request-detail.component.html',
    styleUrls: ['./request-detail.component.scss']
})
export class RequestDetailComponent implements OnInit {

    headingLoading: boolean;
    dependencies: RequestDetailDependencies;

    constructor(private readonly route: ActivatedRoute,
                private readonly router: Router,
                private readonly breakpointObserverService: BreakpointObserverService,
                private readonly linkedDatasetTaskService: LinkedDatasetTaskService,
                private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
                private readonly iconRegistry: MatIconRegistry,
                private readonly sanitizer: DomSanitizer) {
        iconRegistry.addSvgIcon(
            'request',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/key_icon_circle.svg'));
        iconRegistry.addSvgIcon(
            'project',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/projet.svg'));
        iconRegistry.addSvgIcon(
            'historical',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/historique.svg'));
        iconRegistry.addSvgIcon(
            'restricted-dataset',
            sanitizer.bypassSecurityTrustResourceUrl('assets/icons/jdd_restreint.svg'));
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            this.headingLoading = true;
            this.linkedDatasetTaskService.getTask(idTask).pipe(
                injectDependencies({
                    dataset: this.linkedDatasetDependencyFetchers.dataset,
                    project: this.linkedDatasetDependencyFetchers.project,
                }),
                injectDependencies({
                    ownerInfo: this.linkedDatasetDependencyFetchers.ownerInfo,
                }),
                map(({task, asset, dependencies}) => {
                    return {
                        ownerName: dependencies.ownerInfo.name,
                        ownerEmail: dependencies.project.contact_email,
                        receivedDate: moment(task.updatedDate),
                        datasetTitle: dependencies.dataset.resource_title
                    };
                })
            ).subscribe({
                next: (dependencies: RequestDetailDependencies) => {
                    this.headingLoading = false;
                    this.dependencies = dependencies;
                },
                error: (error) => {
                    this.headingLoading = false;
                    console.error(error);
                }
            });
        }
    }
}
