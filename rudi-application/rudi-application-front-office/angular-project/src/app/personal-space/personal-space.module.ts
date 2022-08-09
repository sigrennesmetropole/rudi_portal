import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PersonalSpaceRoutingModule} from './personal-space-routing.module';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {LinkedDatasetTasksComponent} from './components/linked-dataset-tasks/linked-dataset-tasks.component';
import {MatTableModule} from '@angular/material/table';
import {ReceivedAccessRequestsComponent} from './pages/received-access-requests/received-access-requests.component';
import {ProjectDetailComponent} from './components/project-detail/project-detail.component';
import {SharedModule} from '../shared/shared.module';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {CoreModule} from '../core/core.module';
import {AccesDetailsTable3Component} from './components/project-detail/acces-details-table3/acces-details-table3.component';
import {AccesDetailsTable2Component} from './components/project-detail/acces-details-table2/acces-details-table2.component';
import {AccesDetailsTable1Component} from './components/project-detail/acces-details-table1/acces-details-table1.component';
import { RequestDetailComponent } from './pages/request-detail/request-detail.component';
import { TaskDetailComponent } from './components/task-detail/task-detail.component';
import { ProjectOwnerDetailComponent } from './components/project-owner-detail/project-owner-detail.component';
import { MyProjectsComponent } from './pages/projects/my-projects.component';
import { ReusesComponent } from './components/reuses/reuses.component';
import { MyProjectDetailsComponent } from './pages/my-project-details/my-project-details.component';
import { ProjectBasicDetailsComponent } from './components/project-basic-details/project-basic-details.component';
import { ProjectApiTabComponent } from './components/project-api-tab/project-api-tab.component';
import { ProjectDatasetsTabComponent } from './components/project-datasets-tab/project-datasets-tab.component';
import { DialogSubscribeDatasetsComponent } from './components/dialog-subscribe-datasets/dialog-subscribe-datasets.component';
import { ProjectTasksComponent } from './components/project-tasks/project-tasks.component';
import { LinkedDatasetHistoryComponent } from './components/linked-dataset-history/linked-dataset-history.component';
import { MyLinkedDatasetsComponent } from './components/my-linked-datasets/my-linked-datasets.component';
import { ProjectInformationComponent } from './components/project-information/project-information.component';

@NgModule({
    declarations: [
        MyAccountComponent,
        ReceivedAccessRequestsComponent,
        LinkedDatasetTasksComponent,
        RequestDetailComponent,
        ProjectDetailComponent,
        AccesDetailsTable1Component,
        AccesDetailsTable2Component,
        AccesDetailsTable3Component,
        TaskDetailComponent,
        ProjectOwnerDetailComponent,
        MyProjectsComponent,
        ReusesComponent,
        ProjectOwnerDetailComponent,
        MyProjectDetailsComponent,
        ProjectBasicDetailsComponent,
        ProjectApiTabComponent,
        ProjectDatasetsTabComponent,
        DialogSubscribeDatasetsComponent,
        ProjectTasksComponent,
        LinkedDatasetHistoryComponent,
        MyLinkedDatasetsComponent,
        ProjectInformationComponent,
    ],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        PersonalSpaceRoutingModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
    ]
    ,
    providers:
        [
            {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}
        ],
    entryComponents: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PersonalSpaceModule {
}
