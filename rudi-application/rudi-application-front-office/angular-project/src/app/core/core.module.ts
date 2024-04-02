import {CommonModule} from '@angular/common';
import {InjectionToken, NgModule} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {URIComponentCodec} from '@core/services/codecs/uri-component-codec';
import {GetBackendPropertyPipe} from '@shared/pipes/get-backend-property.pipe';
import {ProjectListService} from './services/project-list.service';
import {WorkerLinkedDatasetService} from './services/tasks-aggregator/projekt/worker-linked-dataset.service';
import {WorkerNewDatasetRequestService} from './services/tasks-aggregator/projekt/worker-new-dataset-request.service';
import {WorkerProjectService} from './services/tasks-aggregator/projekt/worker-project.service';
import {WorkerSelfdataInformationRequestService} from './services/tasks-aggregator/selfdata/worker-selfdata-information-request.service';
import {Worker} from './services/tasks-aggregator/worker.interface';

/**
 * Clé d'injection pour les workers de l'aggrégateur des tasks (affichage bannette)
 */
export const WORKERS_AGGREGATOR_TASKS = new InjectionToken<Worker>('tasksWorker');

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
    ],
    exports: [],
    providers: [
        {provide: WORKERS_AGGREGATOR_TASKS, useClass: WorkerLinkedDatasetService, multi: true},
        {provide: WORKERS_AGGREGATOR_TASKS, useClass: WorkerSelfdataInformationRequestService, multi: true},
        {provide: WORKERS_AGGREGATOR_TASKS, useClass: WorkerNewDatasetRequestService, multi: true},
        {provide: WORKERS_AGGREGATOR_TASKS, useClass: WorkerProjectService, multi: true},
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
        {
            provide: MatSnackBarRef,
            useValue: {}
        }, {
            provide: MAT_SNACK_BAR_DATA,
            useValue: {} // Add any data you wish to test if it is passed/used correctly
        },
        ProjectListService,
        GetBackendPropertyPipe,
        URIComponentCodec,
    ]
})

export class CoreModule {
}

// tslint:disable-next-line:no-any
export function initializeApp(ConfigurationService: { load: () => any; }): () => any {
    return () => ConfigurationService.load();
}

