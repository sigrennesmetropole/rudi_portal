import {Component, OnInit, ViewChild} from '@angular/core';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {Router} from '@angular/router';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {TranslateService} from '@ngx-translate/core';
import {injectDependenciesEach} from '../../../shared/utils/task-utils';
import {mapEach} from '../../../shared/utils/ObservableUtils';
import {LinkedDatasetTaskDependencyFetchers, LinkedDatasetTaskService} from '../../../core/services/linked-dataset-task.service';

export interface RequestToStudy {
    taskId: string;
    receivedDate: Date;
    datasetTitle: string;

    /**
     * Prénom + Nom du porteur ou nom de l'organisation
     */
    ownerName: string;
    status: string;

    /** Date au format ISO. Exemple : "2022-04-13T17:09:00+02:00" */
    endDate?: Date;
}

export interface RequestToPrint {
    taskId: string;
    receivedDate: Date;
    datasetTitle: string;
    comment: string;

    /**
     * Prénom + Nom du porteur ou nom de l'organisation
     */
    ownerName: string;
    ownerEmail: string;
    status: string;

    /** Date au format ISO. Exemple : "2022-04-13T17:09:00+02:00" */
    endDate?: Date;
}
@Component({
    selector: 'app-linked-dataset-tasks',
    templateUrl: './linked-dataset-tasks.component.html',
    styleUrls: ['./linked-dataset-tasks.component.scss']
})
export class LinkedDatasetTasksComponent implements OnInit {

    searchIsRunning = false;
    requestsToStudyDisplayedColumns: string[] = ['receivedDate', 'datasetTitle', 'ownerName', 'status', 'endDate'];
    restrictedDatasetIcon = 'key_icon_88_secondary-color';
    dataSource: MatTableDataSource<RequestToStudy>;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    constructor(
        iconRegistryService: IconRegistryService,
        private readonly router: Router,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly linkedDatasetTaskService: LinkedDatasetTaskService,
        private readonly linkedDatasetDependencyFetchers: LinkedDatasetTaskDependencyFetchers,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    private _requestsToStudy: RequestToStudy[];

    get requestsToStudy(): RequestToStudy[] {
        return this._requestsToStudy;
    }

    set requestsToStudy(requestsToStudy: RequestToStudy[]) {
        this._requestsToStudy = requestsToStudy;
        this.dataSource = new MatTableDataSource<RequestToStudy>(this.requestsToStudy);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {
        this.loadTasks();
    }

    private loadTasks(): void {
        this.searchIsRunning = true;
        this.linkedDatasetTaskService.searchTasks().pipe(
            injectDependenciesEach({
                dataset: this.linkedDatasetDependencyFetchers.dataset,
                project: this.linkedDatasetDependencyFetchers.project,
            }),
            injectDependenciesEach({
                ownerInfo: this.linkedDatasetDependencyFetchers.ownerInfo,
            }),
            mapEach(({task, asset, dependencies}) => ({
                taskId: task.id,
                receivedDate: new Date(task.updatedDate),
                datasetTitle: dependencies.dataset.resource_title,
                ownerName: dependencies.ownerInfo.name,
                status: task.functionalStatus,
                endDate: asset.end_date ? new Date(asset.end_date) : undefined,
            } as RequestToStudy)),
        ).subscribe(requestsToStudy => {
            this.requestsToStudy = requestsToStudy;
        }, (e) => {
            console.error('Cannot retrieve requests to study', e);
            this.snackBarService.openSnackBar({
                message: this.translateService.instant('error.technicalError'),
                level: Level.ERROR
            });
            this.searchIsRunning = false;
        }, () => {
            this.searchIsRunning = false;
        });
    }
}
